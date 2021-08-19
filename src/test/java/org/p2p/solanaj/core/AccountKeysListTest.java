package org.p2p.solanaj.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.p2p.solanaj.programs.SystemProgram;

public class AccountKeysListTest {

    @Test
    public void getSortedList() {
        AccountKeysList list = new AccountKeysList();

        list.addAll(
                Arrays.asList(new AccountMeta(new PublicKey("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3"), true, true),
                        new AccountMeta(new PublicKey("7ytmC1nT1xY4RfxCV2ZgyA7UakC93do5ZdyhdF3EtPj7"), false, true),
                        new AccountMeta(new PublicKey("HwRVBufQ4haG5XSgpspwKtNd3PC9GM9m1196uJW36vds"), false, false),
                        new AccountMeta(new PublicKey("6Cust2JhvweKLh4CVo1dt21s2PJ86uNGkziudpkNPaCj"), false, false),
                        new AccountMeta(SystemProgram.PROGRAM_ID, false, false),
                        new AccountMeta(new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"), false, false),
                        new AccountMeta(Sysvar.SYSVAR_RENT_ADDRESS, false, false),
                        new AccountMeta(new PublicKey("8VBafTNv1F8k5Bg7DTVwhitw3MGAMTmekHsgLuMJxLC8"), false, false),
                        new AccountMeta(new PublicKey("8VBafTNv1F8k5Bg7DTVwhitw3MGAMTmekHsgLuMJxLC8"), false, true),
                        new AccountMeta(new PublicKey("6Cust2JhvweKLh4CVo1dt21s2PJ86uNGkziudpkNPaCj"), false, false),
                        new AccountMeta(new PublicKey("7ytmC1nT1xY4RfxCV2ZgyA7UakC93do5ZdyhdF3EtPj7"), false, true),
                        new AccountMeta(new PublicKey("3gF2KMe9KiC6FNVBmfg9i267aMPvK37FewCip4eGBFcT"), false, false),
                        new AccountMeta(new PublicKey("3gF2KMe9KiC6FNVBmfg9i267aMPvK37FewCip4eGBFcT"), true, false),
                        new AccountMeta(new PublicKey("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL"), false, false),
                        new AccountMeta(new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"), false, false)));

        List<AccountMeta> accountKeysList = list.getList();

        assertEquals(10, accountKeysList.size());

        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", accountKeysList.get(0).getPublicKey().toBase58());
        assertEquals("3gF2KMe9KiC6FNVBmfg9i267aMPvK37FewCip4eGBFcT", accountKeysList.get(1).getPublicKey().toBase58());
        assertEquals("7ytmC1nT1xY4RfxCV2ZgyA7UakC93do5ZdyhdF3EtPj7", accountKeysList.get(2).getPublicKey().toBase58());
        assertEquals("8VBafTNv1F8k5Bg7DTVwhitw3MGAMTmekHsgLuMJxLC8", accountKeysList.get(3).getPublicKey().toBase58());
        assertEquals("HwRVBufQ4haG5XSgpspwKtNd3PC9GM9m1196uJW36vds", accountKeysList.get(4).getPublicKey().toBase58());
        assertEquals("6Cust2JhvweKLh4CVo1dt21s2PJ86uNGkziudpkNPaCj", accountKeysList.get(5).getPublicKey().toBase58());
        assertEquals("11111111111111111111111111111111", accountKeysList.get(6).getPublicKey().toBase58());
        assertEquals("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", accountKeysList.get(7).getPublicKey().toBase58());
        assertEquals("SysvarRent111111111111111111111111111111111", accountKeysList.get(8).getPublicKey().toBase58());
        assertEquals("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL", accountKeysList.get(9).getPublicKey().toBase58());
    }
}
