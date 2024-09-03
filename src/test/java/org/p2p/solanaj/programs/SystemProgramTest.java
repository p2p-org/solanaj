package org.p2p.solanaj.programs;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import org.bitcoinj.core.Base58;

class SystemProgramTest {

    @Test
    void transferInstruction() {
        PublicKey fromPublicKey = new PublicKey("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo");
        PublicKey toPublickKey = new PublicKey("GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5");
        int lamports = 3000;

        TransactionInstruction instruction = SystemProgram.transfer(fromPublicKey, toPublickKey, lamports);

        assertEquals(SystemProgram.PROGRAM_ID, instruction.getProgramId());
        assertEquals(2, instruction.getKeys().size());
        assertEquals(toPublickKey, instruction.getKeys().get(1).getPublicKey());

        assertArrayEquals(new byte[] { 2, 0, 0, 0, -72, 11, 0, 0, 0, 0, 0, 0 }, instruction.getData());
    }

    @Test
    void createAccountInstruction() {
        TransactionInstruction instruction = SystemProgram.createAccount(SystemProgram.PROGRAM_ID,
                SystemProgram.PROGRAM_ID, 2039280, 165, SystemProgram.PROGRAM_ID);

        assertEquals("11119os1e9qSs2u7TsThXqkBSRUo9x7kpbdqtNNbTeaxHGPdWbvoHsks9hpp6mb2ed1NeB",
                Base58.encode(instruction.getData()));
    }

}
