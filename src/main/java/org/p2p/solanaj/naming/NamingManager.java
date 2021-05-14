package org.p2p.solanaj.naming;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.NamingServiceProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.AccountInfo;
import org.p2p.solanaj.rpc.types.ConfigObjects;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class NamingManager {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);
    private static final Logger LOGGER = Logger.getLogger(NamingManager.class.getName());
    private static final long DATA_LENGTH = 1000L;
    private static final String HASH_PREFIX = "SPL Name Service";
    private static final PublicKey NAME_PROGRAM_ID = new PublicKey("namesLPneVptA9Z5rqUDD9tMTWEJwofgaYwp8cawRkX");
    private static final PublicKey SYSTEM_PROGRAM_ID = new PublicKey("11111111111111111111111111111111");
    private static final int SPACE = 1000;
    private static final int TWITTER_PUBLIC_KEY_OFFSET = 32;
    private static final int TWITTER_VERIFICATION_AUTHORITY_OFFSET = 64;
    private static final int TWITTER_ACCOUNT_LENGTH = 114;
    private static final int TWITTER_HANDLE_START_OFFSET = 96;
    private static final PublicKey TWITTER_VERIFICATION_AUTHORITY = new PublicKey("867BLob5b52i81SNaV9Awm5ejkZV6VGSv9SxLcwukDDJ");

    /**
     * Creates a .sol domain name with the specified name and payer.
     *
     * @param name domain name
     * @param payer account paying for this transaction
     * @param nameOwner pubkey to associate this domain with
     * @param nameClass "The class of this new name"
     * @param parentName "The parent name of the new name. If specified its owner needs to sign"
     * @return true if domain name creation succeeded.
     */
    public boolean createNameRegistry(final String name,
                                     final Account payer,
                                     final PublicKey nameOwner,
                                     final PublicKey nameClass,
                                     final PublicKey parentName) {

        String fullDomainName = HASH_PREFIX + name;
        byte[] hashedName = getHashedName(fullDomainName);
        LOGGER.info(String.format("Name = %s, Sha256 = %s", fullDomainName, ByteUtils.bytesToHex(hashedName)).toLowerCase());

        PublicKey nameAccountKey = getNameAccountKey(hashedName, nameClass, parentName);
        LOGGER.info(String.format("nameAccountKey = %s", nameAccountKey));

        long minimumBalanceForRentExemption = getMinimumBalanceForRentExemption();
        LOGGER.info(String.format("minimumBalanceForRentExemption = %d", minimumBalanceForRentExemption));

        // nameParentOwner and parentAccount in bindings.ts are seemingly never used, so we ignore
        final Transaction transaction = new Transaction();
        transaction.addInstruction(
                NamingServiceProgram.createNameRegistry(
                        NAME_PROGRAM_ID,
                        SYSTEM_PROGRAM_ID,
                        nameAccountKey,
                        nameOwner,
                        payer.getPublicKey(),
                        hashedName,
                        minimumBalanceForRentExemption,
                        SPACE,
                        nameClass,
                        parentName
                )
        );

        // Call sendTransaction
        String result;
        try {
            result = client.getApi().sendTransaction(transaction, payer);
            LOGGER.info("Result = " + result);
        } catch (RpcException e) {
            e.printStackTrace();
        }

        return true;
    }

    private PublicKey getNameAccountKey(byte[] hashedName, PublicKey nameClass, PublicKey parentName) {
        PublicKey.ProgramDerivedAddress nameAccountKey = null;

        byte[] nameClassBytes, parentNameBytes;

        if (nameClass == null) {
            nameClassBytes = ByteBuffer.allocate(32).array();
        } else {
            nameClassBytes = nameClass.toByteArray();
        }

        if (parentName == null) {
            parentNameBytes = ByteBuffer.allocate(32).array();
        } else {
            parentNameBytes = parentName.toByteArray();
        }

        try {
            nameAccountKey = PublicKey.findProgramAddress(Arrays.asList(hashedName, nameClassBytes, parentNameBytes), NAME_PROGRAM_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO - cleanup NPE
        return nameAccountKey.getAddress();
    }

    /**
     * Sha-256 the prefix + name, into byte array
     * @param input domain name
     * @return byte array of sha256 hashed string
     */
    private byte[] getHashedName(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Calculate rent exemption used in domain name creation
     */
    private long getMinimumBalanceForRentExemption() {
        try {
            return client.getApi().getMinimumBalanceForRentExemption(DATA_LENGTH);
        } catch (RpcException e) {
            e.printStackTrace();
        }
        return DATA_LENGTH * 10;
    }

    public AccountInfo getAccountInfo(PublicKey publicKey) {
        try {
            return client.getApi().getAccountInfo(publicKey);
        } catch (RpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Looks up a Bonfida-associated Twitter account from a given owner's Pubkey
     * @param publicKey owner of Bonfida-associated Twitter account
     * @return twitter handle of the pubkey
     */
    public String getTwitterHandle(PublicKey publicKey) {
        List<ProgramAccount> programAccounts = null;
        try {
            programAccounts = client.getApi().getProgramAccounts(
                    NAME_PROGRAM_ID,
                    List.of(
                            new ConfigObjects.Memcmp(
                                    TWITTER_PUBLIC_KEY_OFFSET,
                                    publicKey.toBase58()
                            ),
                            new ConfigObjects.Memcmp(
                                    TWITTER_VERIFICATION_AUTHORITY_OFFSET,
                                    TWITTER_VERIFICATION_AUTHORITY.toBase58()
                            )
                    )
            );
        } catch (RpcException e) {
            e.printStackTrace();
        }

        if (programAccounts != null) {
            for (ProgramAccount programAccount : programAccounts) {
                byte[] programAccountData = Base58.decode(programAccount.getAccount().getData());
                if (programAccountData.length == TWITTER_ACCOUNT_LENGTH) {
                    String twitterHandle = new String(
                            ByteUtils.trim(
                                    Arrays.copyOfRange(
                                            programAccountData,
                                            TWITTER_HANDLE_START_OFFSET,
                                            TWITTER_ACCOUNT_LENGTH)
                            )
                    );

                    LOGGER.info(String.format("Found Twitter account for %s: %s", publicKey.toBase58(), twitterHandle));
                    return twitterHandle;
                } else {
                    LOGGER.warning(String.format("Unable to find Twitter account for %s", publicKey.toBase58()));
                }
            }
        }

        return null;
    }
}
