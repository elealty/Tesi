import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class SystemInfo {

    public static String getSystemInfo() {

        String nameOS = "os.name";
        String versionOS = "os.version";
        String architectureOS = "os.arch";
        OperatingSystemMXBean bean = ManagementFactory
                .getOperatingSystemMXBean();

        return " Informazioni sistema \n\n" + "Sistema operativo: "
                + System.getProperty(nameOS) + "\n" + "Versione :"
                + System.getProperty(versionOS) + "\n" + "Architettura:"
                + System.getProperty(architectureOS) + "\n"
                // System.out.println("Architecture of THe OS: "
                // + System.getProperty(architectureOS));
                // "Proprieta' :" + System.getProperties() + "\n"
                + " Processori:" + bean.getAvailableProcessors();

        // System.out.println("\n version:" + bean.getVersion());
        // System.out.println("\n name:" + bean.getName());
    }
}
