package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import model.Theorem;
import dbconnection.SqlLiteDb;

public class TheoremParser {
    static String NBU_HEADER            = "*";
    static String COMMON_HEADER         = "%";
    static String COMMON_PROVER_HEADER  = "% Prover";
    static String COMMON_TESTSET_HEADER = "% Testset";

    /**
     * Riceve il file da processare. Controlla le prime righe di intestazione
     * per capire quale sia il formato del file e quindi richiama la
     * corrispondente funzione di parsing
     * 
     * @param file
     *            File da processare
     * @throws IOException
     *             se non riesce a leggere il file
     * @throws SQLException
     */

    public static void processFile(File file, Integer machine_id)
            throws IOException, SQLException {

        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String firstLine = br.readLine();
        br.close();

        if (firstLine.startsWith(NBU_HEADER)) {
            parseNbuProverFile(file, machine_id);
        } else {
            parseProverFile(file, machine_id);
        }
    }

    /**
     * TheoremParser tracciato fileFcube. Legge riga per riga e scrive nel
     * database
     * 
     * @param file
     * @throws IOException
     */
    private static void parseProverFile(File file, Integer machine_id)
            throws IOException, SQLException {
        System.out.println("parseProverfile");
        FileInputStream fstream = new FileInputStream(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        String prover = "";
        String testset = "";

        while ((strLine = br.readLine()) != null) {
            if (strLine.startsWith(COMMON_PROVER_HEADER)) {
                prover = getInfoFromHeader(strLine, COMMON_HEADER);
            }
            if (strLine.startsWith(COMMON_TESTSET_HEADER)) {
                testset = getInfoFromHeader(strLine, COMMON_TESTSET_HEADER);
            }
            if (strLine.trim().startsWith(COMMON_HEADER)
                    || strLine.trim().isEmpty()) {
                continue;
            }

            String[] lineInfo = strLine.split(";");

            Theorem parsedTheorem = Theorem.getTheoremFromString(lineInfo,
                    prover, testset);

            SqlLiteDb.insertTheoremRow(parsedTheorem.name, prover,
                    parsedTheorem.family, testset, parsedTheorem.provable,
                    parsedTheorem.success, parsedTheorem.execution_time,
                    machine_id);
        }

        br.close();
    }

    private static String getInfoFromHeader(String strLine, String header_info) {
        String info = "";
        if (strLine.startsWith(header_info)) {
            info = strLine.split(":")[1].trim();
        }
        return info.toUpperCase();
    }

    /**
     * TheoremParser tracciato Nbu Legge riga per riga e scrive nel database
     * 
     * @param file
     * @throws SQLException
     * @throws IOException
     */
    private static void parseNbuProverFile(File file, Integer machine_id)
            throws SQLException, IOException {
        FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        while ((strLine = br.readLine()) != null) {
            if (strLine.startsWith(NBU_HEADER)) {
                System.out.println("linea di intestazione, salto");
                continue;
            }

            String[] lineInfo = strLine.split(";");
            Theorem parsedTheorem = Theorem.getTheoremFromNbuString(
                    lineInfo[0], lineInfo[1]);
            SqlLiteDb.insertTheoremRow(parsedTheorem.name,
                    parsedTheorem.prover, parsedTheorem.family, "SYJ",
                    parsedTheorem.provable, parsedTheorem.success,
                    parsedTheorem.execution_time, machine_id);
        }
        br.close();
    };

}
