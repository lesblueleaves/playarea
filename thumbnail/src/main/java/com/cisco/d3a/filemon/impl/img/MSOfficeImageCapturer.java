package com.cisco.d3a.filemon.impl.img;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.TimeoutObserver;
import org.apache.commons.exec.Watchdog;

import com.cisco.d3a.filemon.api.CaptureSpec;

public class MSOfficeImageCapturer extends AbstractImageCapturer {
    private String pathToOfficeToPng;
	
	public void setPathToOfficeToPng(String pathToOfficeToPng) {
		this.pathToOfficeToPng = pathToOfficeToPng;
	}
	
	@Override
	protected void doCapture(File from, File to, CaptureSpec captureSpec) throws Exception {
		if(from.getName().endsWith("xlsx")) {
			if(from.length() <= 8746) return; // empty excel file
		} else if(from.getName().endsWith("pptx")) {
			if(from.length() <= 27140) return; // empty powerpoint file
		}
		
        String[] cmdArray = new String[5];
        cmdArray[0] = pathToOfficeToPng;
        cmdArray[1] = "-i";
        cmdArray[2] = from.getCanonicalPath();
        cmdArray[3] = "-o";
        cmdArray[4] = to.getCanonicalPath();
        LOGGER.debug("Executing command: " + Arrays.asList(cmdArray));
        System.out.println(Arrays.asList(cmdArray));
        
//        try {
//            Process process = Runtime.getRuntime().exec(cmdArray);
//            ProcessRunner.waitForOrKill(from.getName(), process, 120);
//        } catch(Exception e) {
//            LOGGER.error("Error in executing command: " + Arrays.asList(cmdArray));
//        }
        
        try {
            Map<String, File> map = new HashMap<String, File>();
            map.put("i", from);
            map.put("o", to);
            CommandLine cmdLine = new CommandLine(pathToOfficeToPng);
            cmdLine.addArgument("-i");
            cmdLine.addArgument("${i}");
            cmdLine.addArgument("-o");
            cmdLine.addArgument("${o}");
            cmdLine.setSubstitutionMap(map);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(0);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(120000);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);
            
            Watchdog dog = new Watchdog(120000);
            dog.addTimeoutObserver(new TimeoutObserver() {
                public void timeoutOccured(Watchdog w) {
					String[] cmd = new String[4];
					cmd[0] = "powershell";
					cmd[1] = "Stop-Process";
					cmd[2] = "-processname";
					cmd[3] = "EXCEL";
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
					}
                }	
            });

        } catch(Exception e) {
            LOGGER.error("Error in executing command: " + Arrays.asList(cmdArray) + " - " + e.getMessage());
        }
	}

    public static void main(String[] args) throws Exception {
    	MSOfficeImageCapturer cap = new MSOfficeImageCapturer();
    	cap.setPathToOfficeToPng("./bin/OfficeToPng.exe");
//    	cap.doCapture(new File("./bin/1ns.pptx"), new File("./bin"), null);
    	cap.doCapture(new File("down/Scala-SBT-Documentation.pdf"), new File("down"), null);
    	System.out.println("Done");
    }
}
