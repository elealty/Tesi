public class Theorem {

    public int id;
    public String name;
    public int provable;
    public int success;
    public int execution_time;

    // os??

    public Theorem(String name, boolean provable, boolean success,
            int execution_time, int id) {
        this.name = name;
        this.provable = (provable == true ? 1 : 0);
        this.success = (provable == true ? 1 : 0);
        this.execution_time = execution_time;
        this.id = id;
    }

    public static Theorem getTheoremFromString(String strTheorem,
            String execution) {
        System.out.println("getTheoremFromString:" + strTheorem + "EX:"
                + execution);
        String[] tValues = strTheorem.replace("test:", "").split(",");
        String[] exValues = execution.replace("times (ms):", "")
                .replace(" ", "").split(",");
        String name = tValues[0].replace(" ", "");
        boolean isProvable = (tValues[1] == "PROVABLE");
        boolean isSuccess = (tValues[2] == "SUCCESS");
        int timeExecution = Integer.parseInt(exValues[0]);

        return new Theorem(name, isProvable, isSuccess, timeExecution, -1);

    }
}
