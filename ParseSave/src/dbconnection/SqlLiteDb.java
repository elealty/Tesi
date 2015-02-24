package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.List;
import java.util.ListIterator;

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
                    + " TESTSET        CHAR(200)    NOT NULL,"
                    + " FAMILY TEXT, "
                    + " PROVABLE       INTEGER    NOT NULL, "
                    + " SUCCESS        INTEGER    NOT NULL, "
                    + " TIMEOUT        INTEGER, "
                    + " EXECUTION_TIME INTEGER NOT NULL, "
                    + " MACHINE_ID INTEGER, " + " MAX_TIMEOUT        INTEGER "
                    + ")";
            stmt.executeUpdate(sql);

            sql = "CREATE INDEX testset_idx ON theorem(testset)";
            stmt.execute(sql);
            sql = "CREATE INDEX prover_idx ON theorem(prover)";
            stmt.execute(sql);
            sql = "CREATE INDEX family_idx ON theorem(family)";
            stmt.execute(sql);
            sql = "CREATE UNIQUE INDEX \"unique_theorem\" ON theorem "
                    + "(TESTSET ASC, PROVER ASC,  FAMILY ASC, NAME ASC, MACHINE_ID ASC)";
            stmt.execute(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("ERROR createDbTables:" + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    private static void createDbViews() {

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);

            stmt = conn.createStatement();

            String sql = "CREATE VIEW \"provable_by_prover\" AS "
                    + " SELECT testset, family,  prover, machine_id , "
                    + " count(*) as total_provable, sum(execution_time) as execution_sum"
                    + " FROM theorem t WHERE provable = 1 "
                    + " GROUP BY prover, family, machine_id "
                    + " ORDER BY prover, family";
            stmt.execute(sql);

            sql = "CREATE VIEW \"total_by_prover\" AS "
                    + " SELECT testset, prover, family,  count(*) as total, machine_id "
                    + " FROM theorem t GROUP BY prover, family, machine_id "
                    + " ORDER BY prover, family";
            stmt.execute(sql);

            sql = "CREATE VIEW \"unprovable_by_prover\" AS"
                    + " SELECT testset, prover, family, max_timeout, "
                    + " machine_id, count(*) as total_unprovable"
                    + " FROM theorem t" + " WHERE provable <> 1"
                    + " GROUP BY prover, family, machine_id"
                    + " ORDER BY prover, family";
            stmt.execute(sql);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("ERROR createDbViews:" + e.getMessage());
            System.exit(0);
        }
        System.out.println("Views created successfully");
    }

    public static void insertTheoremRow(String name, String prover,
            String family, String testset, int provable, int success,
            int executionTime, int timeout, int machine_id, int max_timeout)
            throws SQLException {

        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        stmt.setQueryTimeout(10);
        String sql = "INSERT OR REPLACE into THEOREM"
                + " (name, prover, family, testset, provable, success, "
                + "execution_time, timeout, machine_id, max_timeout) "
                + "VALUES(" + "'" + name + "','" + prover + "','" + family
                + "','" + testset + "'," + provable + "," + success + ","
                + executionTime + "," + timeout + "," + machine_id + ","
                + max_timeout + ")";
        System.out.println(sql);
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
            String sql = "SELECT t.id,  t.name, prover, testset, provable, execution_time as execution, "
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
            String testset, List<String> selectedProvers) throws SQLException {
        System.out.println("getTheoremsFromSearch" + selectedProvers);
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT t.id,  t.name as name, prover, provable, "
                    + " execution_time as execution, testset,"
                    + " success, family, m.name as machine "
                    + " FROM theorem t  LEFT JOIN machine m "
                    + " ON m.id = t.machine_id" + " WHERE m.id = " + machine
                    + " AND testset ='" + testset + "'" + " AND prover IN (";
            ListIterator<String> iter = selectedProvers.listIterator();
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

    public static ResultSet getProvableTheoremsFromSearch(Integer machine,
            String testset, List<String> selectedProvers) throws SQLException {
        System.out.println("getTheoremsFromSearch" + selectedProvers);
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT testset, prover, family, t.name as name, provable, execution_time as execution"
                    + " FROM theorem t  LEFT JOIN machine m "
                    + " ON m.id = t.machine_id"
                    + " WHERE m.id = "
                    + machine
                    + " AND testset ='" + testset + "'" + " AND prover IN (";
            ListIterator<String> iter = selectedProvers.listIterator();
            while (iter.hasNext()) {
                sql += "'" + iter.next().toUpperCase() + "'";
                if (!iter.hasNext())
                    break;
                sql += ",";
            }
            sql += ") AND provable=1 ORDER BY prover";
            System.out.println(sql);
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getTheoremsFromSearch : "
                    + e.getMessage());
        }
        return null;
    }

    public static ResultSet getAllTestsetProvers(String testset, int machine_id)
            throws SQLException {
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT distinct(prover) FROM theorem"
                    + " WHERE testset ='" + testset.toUpperCase() + "'"
                    + " AND machine_id = " + machine_id + " ORDER BY prover";
            System.out.println(sql);
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getAllProvers : " + e.getMessage());
        }
        return null;
    }

    public static ResultSet getAllTestsets() throws SQLException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT distinct(testset) as name FROM theorem";
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getAllTestsets : " + e.getMessage());
        }
        return null;
    }

    /**
     * Return all machine stored in db.
     * 
     * @return Resultset
     * @exception SQLException
     *                if database il locked or sql sintax wrong
     */
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
     * Insert new machine
     * 
     * @param name
     *            machine name
     * @param description
     *            machine description
     * @exception SQLException
     *                if database il locked or sql sintax wrong
     * @exception SQLIntegrityConstraintViolationException
     *                if there are another machine with same name
     */
    public static void insertMachineRow(String name, String description)
            throws SQLException, SQLIntegrityConstraintViolationException {
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }
        stmt = conn.createStatement();
        String sql = "INSERT into MACHINE" + " (name, description) "
                + " VALUES ('" + name + "','" + description + "')";
        System.out.println("insert machine sql:" + sql);
        stmt.execute(sql);
    }

    /**
     * Delete machine from database with ID
     * 
     * @param id
     *            machine id
     * @exception SQLException
     *                if database il locked or sql sintax wrong if database il
     *                locked or sql sintax wrong
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
     * @param id
     *            machine id
     * @param new_name
     *            machine name
     * @exception SQLException
     *                if database il locked or sql sintax wrong
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
            createDbViews();
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

    public static ResultSet getTotals(Integer machine, String testset,
            List<String> selectedProvers) throws SQLException {
        if (conn.isClosed() == true) {
            System.out.println("Connection closed, be reopen");
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = "SELECT distinct total_provable, total, t.prover, t.testset,"
                    + "t.family, p.execution_sum "
                    + " FROM total_by_prover t "
                    + " LEFT JOIN provable_by_prover p"
                    + " ON (p.testset = t.testset "
                    + " AND p.prover = t.prover "
                    + " AND p.family = t.family "
                    + ")"
                    + " WHERE t.testset ='"
                    + testset
                    + "'"
                    + " AND t.machine_id ="
                    + machine
                    + " AND upper(t.prover) IN (";
            ListIterator<String> iter = selectedProvers.listIterator();
            while (iter.hasNext()) {
                sql += "'" + iter.next().toUpperCase() + "'";
                if (!iter.hasNext())
                    break;
                sql += ",";
            }
            sql += ")" + " GROUP BY t.prover, t.family, t.machine_id"
                    + " ORDER BY t.prover, t.family";
            System.out.println(sql);
            ResultSet res = stmt.executeQuery(sql);
            return res;

        } catch (Exception e) {
            System.err.println("ERRORE getTotals : " + e.getMessage());

        }
        return null;
    }

    public static ResultSet getMediaTheoremsTimeoutFromSearch(Integer machine,
            String testset, List<String> selectedProvers) throws SQLException {
        System.out.println("getMediaTheoremsTimeoutFromSearch");
        if (conn.isClosed() == true) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database_name);
        }

        try {
            stmt = conn.createStatement();
            String sql = " SELECT t.prover, t.family as name, "
                    // +"total, execution_sum as T1, "
                    // + " total_provable, total_unprovable, max_timeout, "
                    // + "(total_unprovable * max_timeout) as T2,"
                    + " (( cast(execution_sum as real) + cast((total_unprovable * max_timeout) as real)) / total ) as execution"
                    + " FROM total_by_prover t  "
                    + " LEFT JOIN unprovable_by_prover up"
                    + "    ON (up.testset = t.testset "
                    + "    AND up.prover = t.prover AND up.family = t.family )"
                    + " LEFT JOIN provable_by_prover p "
                    + "    ON (p.testset = t.testset  "
                    + "    AND p.prover = t.prover  AND p.family = t.family ) "
                    + " WHERE t.machine_id = " + machine + " AND t.testset ='"
                    + testset + "'" + " AND t.prover IN (";
            ListIterator<String> iter = selectedProvers.listIterator();
            while (iter.hasNext()) {
                sql += "'" + iter.next().toUpperCase() + "'";
                if (!iter.hasNext())
                    break;
                sql += ",";
            }
            sql += ") GROUP BY t.prover, t.family, t.machine_id "
                    + "ORDER BY t.prover, t.family, name";
            System.out.println(sql);
            return stmt.executeQuery(sql);

        } catch (Exception e) {
            System.err.println("ERRORE getMediaTheoremsTimeoutFromSearch : "
                    + e.getMessage());
        }
        return null;
    }
}
