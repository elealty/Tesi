import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BaseParser {

    public static void parseStandardFile(File file) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            System.out.println("Total file size to read (in bytes) : "
                    + fstream.available());
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    fstream));

            String strLine;

            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("*")) {
                    System.out.println("linea di intestazione, salto");
                    continue;
                }
                //
                String[] lineInfo = strLine.split(";");
                Theorem parsedTheorem = Theorem.getTheoremFromString(
                        lineInfo[0], lineInfo[1]);

                SqlLiteDb.insertTheoremRow(parsedTheorem.name,
                        parsedTheorem.provable, parsedTheorem.success,
                        parsedTheorem.execution_time);

            }

            // Close the input stream
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

}
