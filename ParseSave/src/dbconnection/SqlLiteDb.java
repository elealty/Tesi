package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Iterator;

import javafx.collections.ObservableList;

/**
 * @author eleonora
 */
public class SqlLiteDb {

    static Connection conn          = null;
    static Statement  stmt          = null;
    static String     database_name = "ParseSave/db/theorem.sqlite3";

    public static void openDb() {
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

    private static void createDbTables() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);

            stmt = conn.createStatement();

            String sql = "CREATE TABLE MACHINE "
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL, "
                    + " DESCRIPTION           CHAR(400)    )";
            stmt.executeUpdate(sql);

            sql = "CREATE UNIQUE INDEX \"unique_name\" on machine (NAME ASC)";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE THEOREM"
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + " NAME           CHAR(200)    NOT NULL,"
                    + " PROVER         CHAR(200)    NOT NULL,"
                    + " PROVABLE       INTEGER    NOT NULL, "
                    + " SUCCESS        INTEGER    NOT NULL, "
                    + " EXECUTION_TIME INTEGER NOT NULL, " + "FAMILY TEXT, "
                    + "MACHINE_ID INTEGER" + ")";
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
                    + " FROM theorem WHERE provable = 1";
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

    public static ResultSet getTheoremsFromSearch(Integer machine,
            ObservableList<String> provers) throws SQLException {
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT t.id,  t.name as name, prover, provable, "
                    + "execution_time as execution, "
                    + "success, family, m.name as machine "
                    + " FROM theorem t  LEFT JOIN machine m "
                    + " ON m.id = t.machine_id" + " WHERE m.id = " + machine
                    + " AND prover IN (";
            Iterator<String> iter = provers.iterator();
            while (iter.hasNext()) {
                sql += "'" + iter.next().toUpperCase() + "'";
                if (!iter.hasNext())
                    break;
                sql += ",";
            }
            sql += ") ORDER BY prover";
            System.out.println(sql);
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getTheoremsFromSearch : "
                    + e.getMessage());
        }
        return null;
    }

    public static ResultSet getAllProvers() throws SQLException {
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT distinct(prover) from theorem";
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getAllProvers : " + e.getMessage());
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
            String sql = "SELECT id, name, description FROM machine";
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
    public static void insertMachineRow(String name, String description)
            throws SQLException, SQLIntegrityConstraintViolationException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "INSERT into MACHINE" + " (name, description) "
                + " VALUES ('" + name + "','" + description + "')";
        System.out.println("insert machine sql:" + sql);
        stmt.execute(sql);
    }

    /**
     * Delete machine description
     * 
     * @param name
     * @throws SQLException
     */
    public static void deleteMachineRow(Integer id) throws SQLException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "DELETE FROM MACHINE WHERE id = " + id;
        stmt.executeUpdate(sql);
    }

    /**
     * Update machine name
     * 
     * @param name
     * @throws SQLException
     */
    public static void updateMachineName(Integer id, String new_name)
            throws SQLException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "UPDATE MACHINE SET name = '" + new_name + "' WHERE id = "
                + id;
        stmt.executeUpdate(sql);
    }

    public static void updateMachineDescription(Integer id, String new_value)
            throws SQLException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "UPDATE MACHINE SET description = '" + new_value + "'"
                + " WHERE id = " + id;
        stmt.executeUpdate(sql);
    }

    public static void checkDbTables() throws SQLException {
        System.out.println("checkDbTables");
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        System.out.println("Try to select");
        stmt = conn.createStatement();
        try {
            stmt.executeQuery("SELECT count(*) FROM machine");
        } catch (SQLException e) {
            createDbTables();
        }

    }

    public static void deleteAllMachines() throws SQLException {
        System.out.println("DELETE ALL MACHINE");
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "DELETE FROM MACHINE";
        System.out.println("DELETE machine sql:" + sql);
        stmt.executeUpdate(sql);

    }

    public static void deleteAllTheorems() throws SQLException {
        System.out.println("DELETE ALL THEOREM");
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "DELETE FROM THEOREM";
        stmt.executeUpdate(sql);

    }

}
