package com.cisco.d3a.filemon.impl.img;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import com.cisco.d3a.filemon.api.CaptureSpec;

public class PdfToImageCapturer extends AbstractImageCapturer {
    private String pathToPdfToImage;
		
	public void setPathToPdfToImage(String pathToPdfToImage) {
		this.pathToPdfToImage = pathToPdfToImage;
	}

	@Override
	protected void doCapture(final File from, File to, CaptureSpec captureSpec) throws Exception {
        String[] cmdArray = new String[7];
        cmdArray[0] = pathToPdfToImage;
        cmdArray[1] = "-i";
        cmdArray[2] = from.getCanonicalPath();
        cmdArray[3] = "-o";
        cmdArray[4] = to.getCanonicalPath();
        cmdArray[5] = "-t";
        cmdArray[6] = "png";
        LOGGER.debug("Executing command: " + Arrays.asList(cmdArray));
        System.out.println(Arrays.asList(cmdArray));
        
//        try {
//            Process process = Runtime.getRuntime().exec(cmdArray);
//            ProcessWatchdog.watch(from.getName(), process, 120, new Runnable() {
//            	public void run() {
//                    String[] cmd = new String[4];
//                    cmd[0] = "powershell";
//                    cmd[1] = "Stop-Process";
//                    cmd[2] = "-processname";
//                    cmd[3] = "EXCEL";
//            		try {
//						Runtime.getRuntime().exec(cmd);
//					} catch (IOException e) {
//					}
//            	}
//            });
//        } catch(Exception e) {
//            LOGGER.error("Error in executing command: " + Arrays.asList(cmdArray), e);
//        }
        
        try {
            Map<String, File> map = new HashMap<String, File>();
            map.put("i", from);
            map.put("o", to);
            CommandLine cmdLine = new CommandLine(pathToPdfToImage);
            cmdLine.addArgument("-i");
            cmdLine.addArgument("${i}");
            cmdLine.addArgument("-o");
            cmdLine.addArgument("${o}");
            cmdLine.addArgument("-t");
            cmdLine.addArgument("png");
            cmdLine.setSubstitutionMap(map);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(120000);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);
        } catch(Exception e) {
            LOGGER.error("Error in executing command: " + Arrays.asList(cmdArray) + " - " + e.getMessage());
        }       
        
	}

    public static void main(String[] args) throws Exception {
    	PdfToImageCapturer cap = new PdfToImageCapturer();
    	cap.setPathToPdfToImage("./bin/PdfToImage.exe");
    	cap.doCapture(new File("./bin/websocket_hybi-1.pdf"), new File("./bin/"), null);
    	System.out.println("Done");
    }
}
