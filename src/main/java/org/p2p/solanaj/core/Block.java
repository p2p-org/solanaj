package org.p2p.solanaj.core;


import java.util.List;

public class Block {

    private long blockHeight;

    private long blockTime;

    public long getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(long blockTime) {
        this.blockTime = blockTime;
    }

    public String getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    public long getParentSlot() {
        return parentSlot;
    }

    public void setParentSlot(long parentSlot) {
        this.parentSlot = parentSlot;
    }

    public String getPreviousBlockhash() {
        return previousBlockhash;
    }

    public void setPreviousBlockhash(String previousBlockhash) {
        this.previousBlockhash = previousBlockhash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    private String blockhash;

    private long parentSlot;

    private String previousBlockhash;

    private List<Transaction> transactions;

    private List<Reward> rewards;

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }


}
