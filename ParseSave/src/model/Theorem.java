package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Returns class that represent Theorem object. the screen, the data will be
 * loaded. The graphics primitives that draw the image will incrementally paint
 * on the screen.
 *
 * @see Theorem
 */
public class Theorem {
    public int    id;
    public String name;
    public String prover;
    public int    provable;
    public int    success;
    public int    execution_time;
    public String family;
    public String testset;
    public int    timeout;

    public Theorem(String name, String prover, String family, String testset,
            boolean provable, boolean success, int execution_time, int timeout,
            int id) {
        this.name = name;
        this.prover = prover;
        this.family = family;
        this.testset = testset;
        this.provable = (provable == true ? 1 : 0);
        this.success = (success == true ? 1 : 0);
        this.execution_time = execution_time;
        this.timeout = timeout;
        this.setId(id);
    }

    /**
     * Return Theorem class parsing Nbu file row.
     * 
     * @param strTheorem
     *            represent file row
     * @param execution
     *            represent the execution time of the theorem
     * @return theorem from Nbu file
     */
    public static Theorem getTheoremFromNbuString(String strTheorem,
            String execution) {
        System.out.println("getTheoremFromNbuString");
        String[] tValues = strTheorem.replace("test:", "").split(",");
        String[] exValues = execution.replace("times (ms):", "")
                .replace(" ", "").split(",");
        String name = tValues[0].replace(" ", "");
        String family = name.substring(0, getSeparatOfName(name)).toUpperCase();
        String prover = "NBU";
        String testset = "SYJ";
        boolean isProvable = (tValues[1].trim().equals("PROVABLE"));
        boolean isSuccess = (tValues[2].trim().equals("SUCCESS"));
        int timeExecution = Integer.parseInt(exValues[0]);
        int timeout = 0;

        return new Theorem(name, prover, family, testset, isProvable,
                isSuccess, timeExecution, timeout, -1);

    }

    /**
     * Return Theorem class parsing Fcube file row.
     * 
     * @param strTheorem
     *            represent file row
     * @param execution
     *            represent the execution time of the theorem
     * @return theorem from Fcube file
     */
    public static Theorem getTheoremFromString(String[] strTheorem,
            String prover, String testset) {
        String name = strTheorem[0].replace(" ", "");
        String family = name.substring(0, getSeparatOfName(name)).toUpperCase();
        boolean isProvable = (strTheorem[1].trim().equals("provable"));
        boolean isSuccess = true;
        String time = strTheorem[2].trim();
        Integer timeout = 0;
        int timeExecution = 0;

        if (!time.startsWith("timeout")) {
            timeExecution = Integer.parseInt(time);
        } else {
            timeout = Integer.parseInt(time.replaceAll("[^0-9]", ""));
        }

        return new Theorem(name, prover, family, testset, isProvable,
                isSuccess, timeExecution, timeout, -1);

    }

    private static int getSeparatOfName(String name) {
        if (name.contains("+")) {
            return name.indexOf("+");
        }
        if (name.contains("_")) {
            return name.lastIndexOf("_");
        }
        return 0;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
