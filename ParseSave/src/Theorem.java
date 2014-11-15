public class Theorem {

    public int    id;
    public String name;
    public int    provable;
    public int    success;
    public int    execution_time;

    public Theorem(String name, boolean provable, boolean success,
            int execution_time, int id) {
        this.name = name;
        this.provable = (provable == true ? 1 : 0);
        this.success = (success == true ? 1 : 0);
        this.execution_time = execution_time;
        this.id = id;
    }

    public static Theorem getTheoremFromNbuString(String strTheorem,
            String execution) {

        String[] tValues = strTheorem.replace("test:", "").split(",");
        String[] exValues = execution.replace("times (ms):", "")
                .replace(" ", "").split(",");
        String name = tValues[0].replace(" ", "");

        boolean isProvable = (tValues[1] == "PROVABLE");
        boolean isSuccess = (tValues[2] == "SUCCESS");
        int timeExecution = Integer.parseInt(exValues[0]);

        return new Theorem(name, isProvable, isSuccess, timeExecution, -1);

    }

    public static Theorem getTheoremFromFcubeString(String[] strTheorem) {
        System.out.println("GET TH FROMFCUBE" + strTheorem[0] + " - "
                + strTheorem[1]);
        String name = strTheorem[0].replace(" ", "");
        System.out.println("=== t provable:" + strTheorem[1].trim() + " - "
                + strTheorem[1].equals("provable"));
        System.out.println(((Object) strTheorem[1]).getClass().getName());
        boolean isProvable = strTheorem[1].trim() == "provable";
        boolean isSuccess = true;
        String time = strTheorem[2].trim();
        int timeExecution = 0;
        if (!time.startsWith("timeout")) {
            timeExecution = Integer.parseInt(time);
        }

        return new Theorem(name, isProvable, isSuccess, timeExecution, -1);

    }
}
