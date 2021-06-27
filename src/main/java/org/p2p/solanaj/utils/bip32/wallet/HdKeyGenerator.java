/**
 * Copyright (c) 2018 orogvany
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.p2p.solanaj.utils.bip32.wallet;

import org.p2p.solanaj.utils.bip32.crypto.Hash;
import org.p2p.solanaj.utils.bip32.crypto.HdUtil;
import org.p2p.solanaj.utils.bip32.crypto.HmacSha512;
import org.p2p.solanaj.utils.bip32.crypto.Secp256k1;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.p2p.solanaj.utils.bip32.wallet.key.HdPrivateKey;
import org.p2p.solanaj.utils.bip32.wallet.key.HdPublicKey;
import org.p2p.solanaj.utils.bip32.wallet.key.SolanaCurve;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HdKeyGenerator {

    private static final EdDSAParameterSpec ED25519SPEC = EdDSANamedCurveTable.getByName("ed25519");
    public static final String MASTER_PATH = "m";

    public HdAddress getAddressFromSeed(byte[] seed, SolanaCoin solanaCoin) {

        SolanaCurve curve = solanaCoin.getCurve();
        HdPublicKey publicKey = new HdPublicKey();
        HdPrivateKey privateKey = new HdPrivateKey();
        HdAddress address = new HdAddress(privateKey, publicKey, solanaCoin, MASTER_PATH);

        byte[] I;
        I = HmacSha512.hmac512(seed, curve.getSeed().getBytes(StandardCharsets.UTF_8));

        //split into left/right
        byte[] IL = Arrays.copyOfRange(I, 0, 32);
        byte[] IR = Arrays.copyOfRange(I, 32, 64);

        BigInteger masterSecretKey = HdUtil.parse256(IL);

        //In case IL is 0 or >=n, the master key is invalid.
        if ( masterSecretKey.compareTo(BigInteger.ZERO) == 0 || masterSecretKey.compareTo(Secp256k1.getN()) > 0) {
            throw new RuntimeException("The master key is invalid");
        }

        privateKey.setDepth(0);
        privateKey.setFingerprint(new byte[]{0, 0, 0, 0});
        privateKey.setChildNumber(new byte[]{0, 0, 0, 0});
        privateKey.setChainCode(IR);
        privateKey.setKeyData(HdUtil.append(new byte[]{0}, IL));

        ECPoint point = Secp256k1.point(masterSecretKey);

        publicKey.setDepth(0);
        publicKey.setFingerprint(new byte[]{0, 0, 0, 0});
        publicKey.setChildNumber(new byte[]{0, 0, 0, 0});
        publicKey.setChainCode(IR);
        publicKey.setKeyData(Secp256k1.serP(point));

        privateKey.setPrivateKey(IL);
        EdDSAPrivateKey sk = new EdDSAPrivateKey(new EdDSAPrivateKeySpec(IL, ED25519SPEC));
        EdDSAPublicKey pk = new EdDSAPublicKey(new EdDSAPublicKeySpec(sk.getA(), sk.getParams()));
        publicKey.setPublicKey(HdUtil.append(new byte[]{0}, pk.getAbyte()));

        return address;
    }

    public HdPublicKey getPublicKey(HdPublicKey parent, long child, boolean isHardened) {
        if (isHardened) {
            throw new RuntimeException("Cannot derive child public keys from hardened keys");
        }

        byte[] key = parent.getKeyData();
        byte[] data = HdUtil.append(key, HdUtil.ser32(child));
        //I = HMAC-SHA512(Key = cpar, Data = serP(point(kpar)) || ser32(i))
        byte[] I = HmacSha512.hmac512(data, parent.getChainCode());

        byte[] IL = Arrays.copyOfRange(I, 0, 32);
        byte[] IR = Arrays.copyOfRange(I, 32, 64);

        HdPublicKey publicKey = new HdPublicKey();

        publicKey.setVersion(parent.getVersion());
        publicKey.setDepth(parent.getDepth() + 1);

        byte[] pKd = parent.getKeyData();
        byte[] h160 = Hash.h160(pKd);
        byte[] childFingerprint = new byte[]{h160[0], h160[1], h160[2], h160[3]};

        BigInteger ILBigInt = HdUtil.parse256(IL);
        ECPoint point = Secp256k1.point(ILBigInt);
        point = point.add(Secp256k1.deserP(parent.getKeyData()));

        if (ILBigInt.compareTo(Secp256k1.getN()) > 0 || point.isInfinity()) {
            throw new RuntimeException("This key is invalid, should proceed to next key");
        }

        byte[] childKey = Secp256k1.serP(point);

        publicKey.setFingerprint(childFingerprint);
        publicKey.setChildNumber(HdUtil.ser32(child));
        publicKey.setChainCode(IR);
        publicKey.setKeyData(childKey);

        return publicKey;
    }

    public HdAddress getAddress(HdAddress parent, long child, boolean isHardened) {
        HdPrivateKey privateKey = new HdPrivateKey();
        HdPublicKey publicKey = new HdPublicKey();
        HdAddress address = new HdAddress(privateKey, publicKey, parent.getCoinType(),
                getPath(parent.getPath(), child, isHardened));

        if (isHardened) {
            child += 0x80000000;
        }

        byte[] xChain = parent.getPrivateKey().getChainCode();
        ///backwards hmac order in method?
        byte[] I;
        if (isHardened) {
            //If so (hardened child): let I = HMAC-SHA512(Key = cpar, Data = 0x00 || ser256(kpar) || ser32(i)). (Note: The 0x00 pads the private key to make it 33 bytes long.)
            BigInteger kpar = HdUtil.parse256(parent.getPrivateKey().getKeyData());
            byte[] data = HdUtil.append(new byte[]{0}, HdUtil.ser256(kpar));
            data = HdUtil.append(data, HdUtil.ser32(child));
            I = HmacSha512.hmac512(data, xChain);
        } else {
            //I = HMAC-SHA512(Key = cpar, Data = serP(point(kpar)) || ser32(i))
            //just use public key
            byte[] key = parent.getPublicKey().getKeyData();
            byte[] xPubKey = HdUtil.append(key, HdUtil.ser32(child));
            I = HmacSha512.hmac512(xPubKey, xChain);
        }

        //split into left/right
        byte[] IL = Arrays.copyOfRange(I, 0, 32);
        byte[] IR = Arrays.copyOfRange(I, 32, 64);
        //The returned child key ki is parse256(IL) + kpar (mod n).
        BigInteger parse256 = HdUtil.parse256(IL);
        BigInteger kpar = HdUtil.parse256(parent.getPrivateKey().getKeyData());
        BigInteger childSecretKey = parse256.add(kpar).mod(Secp256k1.getN());

        byte[] childNumber = HdUtil.ser32(child);
        byte[] fingerprint = HdUtil.getFingerprint(parent.getPrivateKey().getKeyData());

        privateKey.setVersion(parent.getPrivateKey().getVersion());
        privateKey.setDepth(parent.getPrivateKey().getDepth() + 1);
        privateKey.setFingerprint(fingerprint);
        privateKey.setChildNumber(childNumber);
        privateKey.setChainCode(IR);
        privateKey.setKeyData(HdUtil.append(new byte[]{0}, HdUtil.ser256(childSecretKey)));

        ECPoint point = Secp256k1.point(childSecretKey);

        publicKey.setVersion(parent.getPublicKey().getVersion());
        publicKey.setDepth(parent.getPublicKey().getDepth() + 1);

        // can just use fingerprint, but let's use data from parent public key
        byte[] pKd = parent.getPublicKey().getKeyData();
        byte[] h160 = Hash.h160(pKd);
        byte[] childFingerprint = new byte[]{h160[0], h160[1], h160[2], h160[3]};

        publicKey.setFingerprint(childFingerprint);
        publicKey.setChildNumber(childNumber);
        publicKey.setChainCode(IR);
        publicKey.setKeyData(Secp256k1.serP(point));

        privateKey.setPrivateKey(IL);
        h160 = Hash.h160(parent.getPublicKey().getPublicKey());
        childFingerprint = new byte[]{h160[0], h160[1], h160[2], h160[3]};
        publicKey.setFingerprint(childFingerprint);
        privateKey.setFingerprint(childFingerprint);
        privateKey.setKeyData(HdUtil.append(new byte[]{0}, IL));

        EdDSAPrivateKey sk = new EdDSAPrivateKey(new EdDSAPrivateKeySpec(IL, ED25519SPEC));
        EdDSAPublicKey pk = new EdDSAPublicKey(new EdDSAPublicKeySpec(sk.getA(), sk.getParams()));
        publicKey.setPublicKey(HdUtil.append(new byte[]{0}, pk.getAbyte()));

        return address;
    }

    private String getPath(String parentPath, long child, boolean isHardened) {
        if(parentPath == null)
        {
            parentPath = MASTER_PATH;
        }
        return parentPath + "/" + child + (isHardened ? "H":"");
    }
}
