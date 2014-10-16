import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BaseParser {

    public BaseParser() {
        // TODO Auto-generated constructor stub
    }

    public static void parseStandardFile(String filename) {
        System.out.println("parseFileA");
        try {
            FileInputStream fstream = new FileInputStream(filename);
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
                String[] test = lineInfo[0].split(",");
                String[] times = lineInfo[1].replace("times (ms):", "").split(
                        ",");

                System.out.println("TEST:" + test.toString());
                System.out.println("TIME:" + times.toString());

                String[] infoToSave = new String[] { test[0], test[1], times[0] };
                SqlLiteDb.insertTheoremRow(infoToSave);

            }

            // Close the input stream
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

}
