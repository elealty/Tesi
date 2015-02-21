package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import model.Theorem;
import dbconnection.SqlLiteDb;

public class TheoremParser {
    static String NBU_HEADER                = "*";
    static String COMMON_HEADER             = "%";
    static String COMMON_PROVER_HEADER      = "% Prover";
    static String COMMON_TESTSET_HEADER     = "% Testset";
    static String COMMON_MAX_TIMEOUT_HEADER = "% Timeout (sec)";

    /**
     * Riceve il file da processare. Controlla le prime righe di intestazione
     * per capire quale sia il formato del file e quindi richiama la
     * corrispondente funzione di parsing
     * 
     * @param file
     *            File to process
     * @param machine_id
     *            indicate where problems in file are executed
     * @exception IOException
     *                se non riesce a leggere il file
     * @exception SQLException
     *                if database il locked or sql sintax wrong
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
     * @exception IOException
     */
    private static void parseProverFile(File file, int machine_id)
            throws IOException, SQLException {
        System.out.println("parseProverfile");

        String strLine;
        String prover = "";
        String testset = "";
        int max_timeout = -1;

        // System.out.println("PATH:" + file.getPath());
        // Files.lines(file.getPath())
        // Stream<String> lines = Files.lines(Paths.get(file.getPath()));
        // String test = lines.filter(w -> w.startsWith(COMMON_PROVER_HEADER))
        // .findFirst().toString().split(":")[1].trim();
        // ;
        // System.out.println("TEST:" + test);
        // Stream<String> dataLines = lines.filter(line -> line
        // .startsWith(COMMON_HEADER) == false);
        // System.out.println("DATALINES:" + dataLines.count());
        // lines.close();
        // lines.forEach(line -> {
        // if (line.startsWith(COMMON_PROVER_HEADER)) {
        // prover = getInfoFromHeader(line, COMMON_HEADER);
        // }
        // if (line.startsWith(COMMON_TESTSET_HEADER)) {
        // testset = getInfoFromHeader(line, COMMON_TESTSET_HEADER);
        // }
        //
        // if (line.startsWith(COMMON_MAX_TIMEOUT_HEADER)) {
        // max_timeout = Integer.valueOf(getInfoFromHeader(line,
        // COMMON_MAX_TIMEOUT_HEADER));
        // }
        // if (line.trim().startsWith(COMMON_HEADER) || line.trim().isEmpty()) {
        // continue;
        // }
        //
        // });

        FileInputStream fstream = new FileInputStream(file);
        FileChannel fileChannelRead = fstream.getChannel();
        System.out.println("channel" + fileChannelRead.size());

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        while ((strLine = br.readLine()) != null) {
            System.out.println("strline" + strLine);
            if (strLine.startsWith(COMMON_PROVER_HEADER)) {
                prover = getInfoFromHeader(strLine, COMMON_HEADER);
                continue;
            }
            if (strLine.startsWith(COMMON_TESTSET_HEADER)) {
                testset = getInfoFromHeader(strLine, COMMON_TESTSET_HEADER);
                continue;
            }

            if (strLine.startsWith(COMMON_MAX_TIMEOUT_HEADER)) {
                max_timeout = Integer.valueOf(getInfoFromHeader(strLine,
                        COMMON_MAX_TIMEOUT_HEADER));
                continue;
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
                    parsedTheorem.timeout, machine_id, max_timeout);

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
     * @exception SQLException
     *                if database il locked or sql sintax wrong
     * @exception IOException
     *                if there are problem during parse file
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
                    parsedTheorem.execution_time, parsedTheorem.timeout,
                    machine_id, -1);
        }
        br.close();
    };

}
