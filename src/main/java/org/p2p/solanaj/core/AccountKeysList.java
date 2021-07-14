package org.p2p.solanaj.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

public class AccountKeysList {
    private HashMap<String, AccountMeta> accounts;

    public AccountKeysList() {
        accounts = new HashMap<String, AccountMeta>();
    }

    public void add(AccountMeta accountMeta) {
        String key = accountMeta.getPublicKey().toString();

        if (accounts.containsKey(key)) {
            if ((!accounts.get(key).isWritable() && accountMeta.isWritable())
                    || (!accounts.get(key).isSigner() && accountMeta.isSigner())) {
                accounts.put(key, accountMeta);
            }
        } else {
            accounts.put(key, accountMeta);
        }
    }

    public void addAll(Collection<AccountMeta> metas) {
        for (AccountMeta meta : metas) {
            add(meta);
        }
    }

    public ArrayList<AccountMeta> getList() {
        ArrayList<AccountMeta> accountKeysList = new ArrayList<AccountMeta>(accounts.values());
        accountKeysList.sort(metaComparator);

        return accountKeysList;
    }

    private static final Comparator<AccountMeta> metaComparator = new Comparator<AccountMeta>() {

        @Override
        public int compare(AccountMeta am1, AccountMeta am2) {

            int cmpSigner = am1.isSigner() == am2.isSigner() ? 0 : am1.isSigner() ? -1 : 1;
            if (cmpSigner != 0) {
                return cmpSigner;
            }

            int cmpkWritable = am1.isWritable() == am2.isWritable() ? 0 : am1.isWritable() ? -1 : 1;
            if (cmpkWritable != 0) {
                return cmpkWritable;
            }

            return Integer.compare(cmpSigner, cmpkWritable);
        }
    };

}
