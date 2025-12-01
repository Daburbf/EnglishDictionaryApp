package main.model;

public class CorruptOfficer {
    private String name;
    private String position;
    private long stolenFunds;
    
    public CorruptOfficer(String name, String position, long stolenFunds) {
        this.name = name;
        this.position = position;
        this.stolenFunds = stolenFunds;
    }

    public String getName() { return name; }
    public String getPosition() { return position; }
    public long getStolenFunds() { return stolenFunds; }

    public void stealFunds(long amount) {
        this.stolenFunds += amount;
        System.out.println("DUIT" + name + " stole Rp " + amount);
    }
    
    public String getCorruptStatus() {
        return name + " - " + position + " | Stolen: Rp " + stolenFunds;
    }
}