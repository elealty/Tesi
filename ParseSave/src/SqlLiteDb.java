import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author eleonora
 * 
 */
public class SqlLiteDb {

    static Connection conn = null;
    static Statement stmt = null;

    public static void openDb(String db_name) {
        System.out.println("Try to open db " + db_name);
        try {
            Class.forName("org.sqlite.JDBC");

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
                    .getConnection("jdbc:sqlite:../theorem.sqlite3");
            System.out.println("Opened database successfully");

            stmt = conn.createStatement();
            String sql = "CREATE TABLE MACHINE "
                    + "(ID INT PRIMARY KEY     NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL, "
                    + " RAM            CHAR(50)     NOT NULL, "
                    + " PROCESSOR      CHAR(100) )";
            stmt.executeUpdate(sql);

            System.out.println("MACHINE table created");

            sql = "CREATE TABLE THEOREM_INFO"
                    + "(ID INT PRIMARY KEY     NOT NULL,"
                    + " PROVABLE       INT    NOT NULL, "
                    + " SUCCESS        INT    NOT NULL, "
                    + " EXECUTION_TIME INT NOT NULL )";
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

    public static void insertTheoremRow(String[] data) {
        System.out.println("insertTheoremRow data:" + data);
        try {
            if (conn.isClosed() == true) {
                System.out.println("Connection closed, be reopen");
                conn = DriverManager
                        .getConnection("jdbc:sqlite:../theorem.sqlite3");
            }
            stmt = conn.createStatement();
            String sql = "INSERT OR REPLACE into THEOREM_INFO"
                    + " (provable, success, execution, time) VALUES(" + data[0]
                    + "," + data[1] + "," + data[2] + "," + " )";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
