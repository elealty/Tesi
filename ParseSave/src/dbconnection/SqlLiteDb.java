package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author eleonora
 */
public class SqlLiteDb {

    static Connection conn          = null;
    static Statement  stmt          = null;
    static String     database_name = "ParseSave/db/theorem.sqlite3";

    public static void openDb() {
        System.out.println("Try to open db " + database_name);
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            conn.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");

    }

    public static void createDbTables() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            System.out.println("Opened database successfully");

            stmt = conn.createStatement();

            String sql = "CREATE TABLE MACHINE "
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT     NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL )";
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
            int executionTime, String family) throws SQLException {
        System.out.println("INSERT THEOREM ROW");
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        System.out.println("CREATE - SQL INSERT");
        String sql = "INSERT OR REPLACE into THEOREM_INFO"
                + " (name, provable, success, execution_time, family) VALUES("
                + "'" + name + "'," + provable + "," + success + ","
                + executionTime + "," + family + " )";
        System.out.println("insertTheoremRow sql:" + sql);
        stmt.execute(sql);
    }

    public static ResultSet getTheoremProvableWithMaxExecution() {
        System.out.println("getTheoremProvableWithMaxExecution");
        try {
            stmt = conn.createStatement();
            String sql = "SELECT name, max(execution_time) as max_execution FROM theorem_info";
            ResultSet res = stmt.executeQuery(sql);
            System.out.println("MaxExecution" + res);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getTheoremProvableWithMaxExecution : "
                    + e.getMessage());
        }
        return null;
    }

    public static ResultSet getAllTheorems() throws SQLException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT id, name, provable, execution_time as execution, success, family FROM theorem_info";
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getAllTheorems : " + e.getMessage());
        }
        return null;
    }

    public static ResultSet getAllMachines() throws SQLException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT id, name FROM machine";
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getAllMachines : " + e.getMessage());
        }
        return null;
    }

    /**
     * Insert machine description
     * 
     * @param name
     * @throws SQLException
     */
    public static void insertMachineRow(String name) throws SQLException {
        System.out.println("INSERT MACHINE ROW");
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "INSERT OR REPLACE into MACHINE" + " (name) VALUES('"
                + name + "')";
        System.out.println("insert machine sql:" + sql);
        stmt.execute(sql);
    }
}
