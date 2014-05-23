package com.cisco.d3a.filemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cisco.springroll.util.MainApp;

/**
 * User: haihxiao
 * Date: 11/12/12
 * Time: 10:49 AM
 */
public class MonitorMain extends MainApp {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorMain.class);

	private String configFile;   
	
	public static void main( String[] args ) throws Exception {
		MainApp.main(new MonitorMain("Booting in " + environment() + " mode"), args);
    }

	public MonitorMain(String status) {
		super(status);
    	configFile = "applicationContext-" + environment() + ".xml";
	}
	
	@Override
	protected void boot(String[] args) throws Exception {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configFile);
        Runtime.getRuntime().addShutdownHook(new Thread() {
        	public void run() {
        		Thread.currentThread().setName(MonitorMain.class.getSimpleName());
        		context.close();
        		context.destroy();
        		LOGGER.info("SpringRoll thumbnail service is about to quit.");
        	}
        });
	}
}
