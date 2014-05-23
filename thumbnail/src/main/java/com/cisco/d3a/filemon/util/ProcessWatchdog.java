package com.cisco.d3a.filemon.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ProcessWatchdog implements Runnable {	
    public static void purgeStream(InputStream stream) {
    	try {
    		String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            while ((line = br.readLine()) != null) {
            	System.out.println(line);
            }
    	} catch(Exception e) {}
    }

	public static boolean isAlive( Process p ) {
	    try {
	        p.exitValue();
	        return false;
	    } catch (IllegalThreadStateException e) {
	        return true;
	    }
	}
	
    Process process;
    private boolean finished;
    private Runnable callback;
    
    public static void watch(String name, Process self, long numberOfSeconds) {
        watch(name, self, numberOfSeconds, null);
    }
    
    public static void watch(String name, Process self, long numberOfSeconds, Runnable callback) {
        ProcessWatchdog runnable = new ProcessWatchdog(self, callback);
        Thread thread = new Thread(runnable, "ProcessRunner-" + name);
        thread.start();
        runnable.waitForOrKill(numberOfSeconds);
    }
    
    private ProcessWatchdog(Process process, Runnable callback) {
        this.process = process;
        this.callback = callback;
    }
    
	public void run() {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            // Ignore
        }
        synchronized (this) {
            finished = true;
            notifyAll();
        }
    }

    public synchronized void waitForOrKill(long numberOfSeconds) {
    	while(!finished && numberOfSeconds -- > 0) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        process.destroy();
        if(callback != null) callback.run();
    }	
    
    public static void main(String[] args) throws Exception {
        String[] cmdArray = new String[5];
        cmdArray[0] = "C:/Workspaces/D3A/Src/Server/D3AFileMonitor/bin/OfficeToPng.exe";
        cmdArray[1] = "-i";
        cmdArray[2] = "C:/Workspaces/D3A/Src/Server/D3AFileMonitor/bin/1ns.pptx";
        cmdArray[3] = "-o";
        cmdArray[4] = "C:/Workspaces/D3A/Src/Server/D3AFileMonitor/bin/";
        try {
            System.out.println(Arrays.asList(cmdArray));
            Process process = Runtime.getRuntime().exec(cmdArray);
            ProcessWatchdog.watch("OfficeToPng", process, 120);
        } catch(Exception e) {
        }
    }
}
