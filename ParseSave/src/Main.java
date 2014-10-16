/**
 * Main class
 */

/**
 * @author eleonora
 * 
 */
public class Main {

    /**
     * 
     */
    public Main() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("main Baseparser");
        SqlLiteDb.openDb("db/theorem.sqlite3");
        BaseParser.parseStandardFile("../esempio-tempi");
        System.out.println("fine");

        for (String s : args) {
            System.out.println("ARGS:" + s);
        }

        try {
            Integer createTables = Integer.parseInt(args[0]);
            if (createTables == 1) {
                SqlLiteDb.createDbTables("db/theorem.sqlite3");
            }
        } catch (NumberFormatException e) {
            System.err.println("Argument" + args[0] + " must be an integer.");
        }

    }

}
