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
    static String NBU_HEADER          = "*";
    static String FCUBE_HEADER        = "%";
    static String FCUBE_PROVER_HEADER = "% Prover";

    /**
     * Riceve il file da processare. Controlla le prime righe di intestazione
     * per capire quale sia il formato del file e quindi richiama la
     * corrispondente funzione di parsing
     * 
     * @param file
     *            File da processare
     * @throws IOException
     *             se non riesce a leggere il file
     */

    public static void processFile(File file, Integer machine_id)
            throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String firstLine = br.readLine();
        br.close();

        if (firstLine.startsWith("*")) {
            parseNbuProverFile(file, machine_id);
        } else {
            parseFcubeProverFile(file, machine_id);
        }
    }

    /**
     * TheoremParser tracciato fileFcube. Legge riga per riga e scrive nel
     * database
     * 
     * @param file
     * @throws IOException
     */
    private static void parseFcubeProverFile(File file, Integer machine_id)
            throws IOException {

        FileInputStream fstream = new FileInputStream(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        String prover = "";

        while ((strLine = br.readLine()) != null) {
            if (strLine.startsWith(FCUBE_PROVER_HEADER)) {
                prover = getProverFromHeader(strLine, FCUBE_HEADER);
            }
            if (strLine.trim().startsWith(FCUBE_HEADER)
                    || strLine.trim().isEmpty()) {
                continue;
            }
            String[] lineInfo = strLine.split(";");

            Theorem parsedTheorem = Theorem.getTheoremFromFcubeString(lineInfo);

            try {
                SqlLiteDb.insertTheoremRow(parsedTheorem.name, prover,
                        parsedTheorem.provable, parsedTheorem.success,
                        parsedTheorem.execution_time, "SYJ", machine_id);
            } catch (SQLException e) {
                System.err.println("ERRORE insertTheoremRow"
                        + e.getClass().getName() + ": " + e.getMessage());
                break;
            }

        }

        br.close();
    }

    private static String getProverFromHeader(String strLine, String header) {
        String prover = "";
        if (header.startsWith(FCUBE_HEADER)) {
            prover = strLine.split(":")[1].trim();
        }
        if (header.startsWith(NBU_HEADER)) {
            prover = strLine.split(":")[1].trim();
        }

        return prover.toUpperCase();
    }

    /**
     * TheoremParser tracciato Nbu Legge riga per riga e scrive nel database
     * 
     * @param file
     */
    private static void parseNbuProverFile(File file, Integer machine_id) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            System.out.println("Total file size to read (in bytes) : "
                    + fstream.available());
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    fstream));

            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith(NBU_HEADER)) {
                    System.out.println("linea di intestazione, salto");
                    continue;
                }

                String[] lineInfo = strLine.split(";");
                Theorem parsedTheorem = Theorem.getTheoremFromNbuString(
                        lineInfo[0], lineInfo[1]);
                try {
                    SqlLiteDb.insertTheoremRow(parsedTheorem.name,
                            parsedTheorem.prover, parsedTheorem.provable,
                            parsedTheorem.success,
                            parsedTheorem.execution_time, "SYJ", machine_id);
                } catch (SQLException e) {
                    System.out.println("Errore durante la scrittura riga"
                            + "nel database");
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

}
