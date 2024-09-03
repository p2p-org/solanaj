package org.p2p.solanaj.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class AccountKeysList {
    private final List<AccountMeta> accountsList;

    public AccountKeysList() {
        accountsList = new ArrayList<>();
    }

    public void add(AccountMeta accountMeta) {
        accountsList.add(accountMeta);
    }

    public void addAll(Collection<AccountMeta> metas) {
        accountsList.addAll(metas);
    }

    public List<AccountMeta> getList() {
        ArrayList<AccountMeta> uniqueMetas = new ArrayList<>();

        for (AccountMeta accountMeta : accountsList) {
            PublicKey pubKey = accountMeta.getPublicKey();

            int index = AccountMeta.findAccountIndex(uniqueMetas, pubKey);
            if (index > -1) {
                uniqueMetas.set(index,
                        new AccountMeta(pubKey, accountsList.get(index).isSigner() || accountMeta.isSigner(),
                                accountsList.get(index).isWritable() || accountMeta.isWritable()));
            } else {
                uniqueMetas.add(accountMeta);
            }
        }

        uniqueMetas.sort(metaComparator);

        return uniqueMetas;
    }

    private static final Comparator<AccountMeta> metaComparator = (am1, am2) -> {

        int cmpSigner = am1.isSigner() == am2.isSigner() ? 0 : am1.isSigner() ? -1 : 1;
        if (cmpSigner != 0) {
            return cmpSigner;
        }

        return am1.isWritable() == am2.isWritable() ? 0 : am1.isWritable() ? -1 : 1;
    };

}
