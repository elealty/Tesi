import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author eleonora
 * 
 */
public class SqlLiteDb {

    static Connection conn = null;
    static Statement stmt = null;
    static String database_name = "";

    public static void openDb(String db_name) {
        System.out.println("Try to open db " + db_name);
        try {
            Class.forName("org.sqlite.JDBC");
            database_name = db_name;
            conn = DriverManager.getConnection("jdbc:sqlite:" + db_name);
            conn.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");

    }

    public static void createDbTables(String db_name) {

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager
                    .getConnection("jdbc:sqlite:"+db_name);
            System.out.println("Opened database successfully");

            stmt = conn.createStatement();

            String sql = "CREATE TABLE MACHINE "
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT     NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL, "
                    + " RAM            CHAR(50)     NOT NULL, "
                    + " PROCESSOR      CHAR(100) )";
            stmt.executeUpdate(sql);

            System.out.println("MACHINE table created");

            sql = "CREATE TABLE THEOREM_INFO"
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL,"
                    + " PROVABLE       INTEGER    NOT NULL, "
                    + " SUCCESS        INTEGER    NOT NULL, "
                    + " EXECUTION_TIME INTEGER NOT NULL )";
            stmt.executeUpdate(sql);
            System.out.println("THEOREM_INFO table created");

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public static void insertTheoremRow(String name, int provable, int success,
            int executionTime) {
        System.out.println("insertTheoremRow data");
        try {
            if (conn.isClosed() == true) {
                System.out.println("Connection closed, be reopen");
                conn = DriverManager
                        .getConnection("jdbc:sqlite:"+database_name);
            }
            stmt = conn.createStatement();
            String sql = "INSERT OR REPLACE into THEOREM_INFO"
                    + " (name, provable, success, execution_time) VALUES("
                    + "'" + name + "'," + provable + "," + success + ","
                    + executionTime + " )";
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("ERRORE insertTheoremRow"
                    + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static ResultSet getTheoremWithMaxExecution() {
        try {
            stmt = conn.createStatement();
            String sql = "SELECT name, max(execution_time) as max_execution from theorem_info";
            ResultSet res = stmt.executeQuery(sql);
            return res;
            //while (res.next()) {
            //    System.out.println("THEOREM WITH MAX EXECUTION:"
            //            + res.getString("name") + "TIME:"
            //            + res.getInt("max_execution") + " ms");
           // }
        } catch (Exception e) {
            System.err.println("ERRORE getTheoremWithMaxExecution : "
                    + e.getMessage());
        }
		return null;
    }
}
