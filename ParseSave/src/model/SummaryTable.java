package model;

/**
 * Returns class that represent Theorem object. the screen, the data will be
 * loaded. The graphics primitives that draw the image will incrementally paint
 * on the screen.
 *
 * @see SummaryTable
 */
public class SummaryTable {
    private String prover;
    private String family;
    private String testset;
    private String machineName;
    private int    totalProvable;
    private int    total;
    private int    totalExecution;

    public SummaryTable(String prover, String family, String testset,
            int totalProvable, int total, int totalExecution, String machineName) {
        this.setProver(prover);
        this.setFamily(family);
        this.setTestset(testset);
        this.setMachineName(machineName);
        this.setTotalProvable(totalProvable);
        this.setTotal(total);
        this.setTotalExecution(totalExecution);

    }

    public void setTotalExecution(int totalExecution) {
        this.totalExecution = totalExecution;
    }

    public int getTotalExecution() {
        return totalExecution;

    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machine_name) {
        this.machineName = machine_name;
    }

    public String getTestset() {
        return testset;
    }

    public void setTestset(String testset) {
        this.testset = testset;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getProver() {
        return prover;
    }

    public void setProver(String prover) {
        this.prover = prover;
    }

    public int getTotalProvable() {
        return totalProvable;
    }

    public void setTotalProvable(int total_provable) {
        this.totalProvable = total_provable;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}