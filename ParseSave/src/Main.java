/**
 * Main class
 */

/**
 * @author eleonora
 * 
 */
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("main Baseparser");
        SqlLiteDb.openDb("ParseSave/db/theorem.sqlite3");

        for (String s : args) {
            System.out.println("ARGS:" + s);
        }

       /* try {
            Integer createTables = Integer.parseInt(args[0]);
            if (createTables == 1) {
                SqlLiteDb.createDbTables("db/theorem.sqlite3");
            }
        } catch (NumberFormatException e) {
            System.err.println("Argument" + args[0] + " must be an integer.");
        }
*/
        BaseParser.parseStandardFile("ParseSave/time-example/esempio-tempi");
        System.out.println("================================");
        SqlLiteDb.getTheoremWithMaxExecution();

    }

}
