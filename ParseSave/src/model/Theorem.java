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

    public Theorem(String name, String prover, boolean provable,
            boolean success, int execution_time, int id) {
        this.name = name;
        this.prover = prover;
        this.provable = (provable == true ? 1 : 0);
        this.success = (success == true ? 1 : 0);
        this.execution_time = execution_time;
        this.id = id;
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

        String[] tValues = strTheorem.replace("test:", "").split(",");
        String[] exValues = execution.replace("times (ms):", "")
                .replace(" ", "").split(",");
        String name = tValues[0].replace(" ", "");
        String prover = "NBU";
        boolean isProvable = (tValues[1].trim().equals("PROVABLE"));
        boolean isSuccess = (tValues[2].trim().equals("SUCCESS"));
        int timeExecution = Integer.parseInt(exValues[0]);

        return new Theorem(name, prover, isProvable, isSuccess, timeExecution,
                -1);

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
    public static Theorem getTheoremFromFcubeString(String[] strTheorem) {
        String name = strTheorem[0].replace(" ", "");
        String prover = "FCUBE";
        boolean isProvable = (strTheorem[1].trim().equals("provable"));
        boolean isSuccess = true;
        String time = strTheorem[2].trim();

        int timeExecution = 0;
        if (!time.startsWith("timeout")) {
            timeExecution = Integer.parseInt(time);
        }

        return new Theorem(name, prover, isProvable, isSuccess, timeExecution,
                -1);

    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
}
