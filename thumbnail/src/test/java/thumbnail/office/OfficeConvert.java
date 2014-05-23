package thumbnail.office;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

public class OfficeConvert {

	public static void main(String[] args) {
		OfficeConvert officeConvert = new OfficeConvert();
		officeConvert.xpsConvertor();
	}
	
	private void xpsConvertor(){
//		 String[] cmdArray = new String[7];
//        cmdArray[0] = "D:\\Program Files (x86)\\docPrint Pro v6.0\\";
//        cmdArray[1] = "-i";
//        cmdArray[2] = "D:\\files\\1.pptx";
//        cmdArray[3] = "-o";
//        cmdArray[4] ="D:\\files\\1.xps";
//        System.out.println(Arrays.asList(cmdArray));
        
        try {
//            Map<String, File> map = new HashMap<String, File>();
        	Map<String, String> map = new HashMap<String, String>();
            map.put("i", "D:\\files\\1.pptx");
            map.put("o", "D:\\files\\2.xps");
            CommandLine cmdLine = new CommandLine("D:\\ptfprinter\\doc2pdf.exe");
            cmdLine.addArgument("-i");
            cmdLine.addArgument("${i}");
            cmdLine.addArgument("-o");
            cmdLine.addArgument("${o}");
   
            cmdLine.setSubstitutionMap(map);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(120000);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);
        } catch(Exception e) {
//            LOGGER.error("Error in executing command: " + Arrays.asList(cmdArray) + " - " + e.getMessage());
        	e.printStackTrace();
        }       
	}

}
