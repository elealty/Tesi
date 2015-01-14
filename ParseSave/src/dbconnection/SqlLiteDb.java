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

            stmt = conn.createStatement();

            String sql = "CREATE TABLE MACHINE "
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT     NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL )";
            stmt.executeUpdate(sql);

            System.out.println("MACHINE table created");

            sql = "CREATE TABLE THEOREM"
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL,"
                    + " PROVER         CHAR(200)    NOT NULL,"
                    + " PROVABLE       INTEGER    NOT NULL, "
                    + " SUCCESS        INTEGER    NOT NULL, "
                    + " EXECUTION_TIME INTEGER NOT NULL )";
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public static void insertTheoremRow(String name, String prover,
            int provable, int success, int executionTime, String family,
            Integer machine_id) throws SQLException {
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();

        String sql = "INSERT OR REPLACE into THEOREM"
                + " (name, prover, provable, success, execution_time, family, machine_id) VALUES("
                + "'" + name + "','" + prover + "'," + provable + "," + success
                + "," + executionTime + ",'" + family + "'," + machine_id + ")";
        System.out.println("insertTheoremRow sql:" + sql);
        stmt.execute(sql);
    }

    public static ResultSet getTheoremProvableWithMaxExecution() {
        try {
            stmt = conn.createStatement();
            String sql = "SELECT name, max(execution_time) as max_execution FROM theorem";
            ResultSet res = stmt.executeQuery(sql);
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
            String sql = "SELECT t.id,  t.name, prover, provable, execution_time as execution, "
                    + "success, family, m.name as machine "
                    + " FROM theorem t  LEFT JOIN machine m "
                    + " ON m.id = t.machine_id";
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getAllTheorems : " + e.getMessage());
        }
        return null;
    }

    public static ResultSet getProvableTheorems() throws SQLException {
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT  name, prover, execution_time as execution "
                    + " FROM theorem WHERE provable = 1 AND execution_time < 5000";
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err
                    .println("ERRORE getProvableTheorems : " + e.getMessage());
        }
        return null;
    }

    public static ResultSet getTheoremsProverMaxTimes() throws SQLException {
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT prover, max(execution_time) as max "
                    + "FROM theorem GROUP BY prover";

            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getTheoremsProverMaxTimes : "
                    + e.getMessage());
        }
        return null;
    }

    /******************************* MACHINES **********************************************/

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
