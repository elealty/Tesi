import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;


public class SystemInfo {

    public SystemInfo() {
	
	//return {'nameOS': System.getProperty("os.name")};  
	String nameOS="os.name";
        String versionOS = "os.version";  
        String architectureOS = "os.arch";
        
        System.out.println("\n  The information about OS");
        System.out.println("\nName of the OS: " +  System.getProperty(nameOS));
        System.out.println("Version of the OS: " + System.getProperty(versionOS));
        System.out.println("Architecture of THe OS: " + System.getProperty(architectureOS));
        System.out.println(System.getProperties());
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        
        System.out.println("\nprocessors:"+bean.getAvailableProcessors());
        //System.out.println("\nphysical mem size:"+bean.getTotalPhysicalMemorySize());
        System.out.println("\n version:"+bean.getVersion()); 
        System.out.println("\n name:"+bean.getName());
    }

}
