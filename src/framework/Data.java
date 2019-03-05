package framework;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.testng.log4testng.Logger;
import org.w3c.dom.Document;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class Data {
	
	  private static final ThreadLocal<Data> T = new ThreadLocal<Data>();
	  
	    public static Data get()
	    {
	        return T.get();
	    }
	 
	    public static void set(Data data)
	    {
	    	T.set(data);
	    }
	   
//***************************************************************************************************************
	    
	public static class Common{
		
		public static WebDriver driver;
		public static ExtentHtmlReporter htmlReporter;
	  	public static ExtentReports reports;
	  	public static ExtentTest test;
		public static HashMap<String, String> envConfigData;
		public static HashMap<String, String> executionConfigData;
		public static Logger log4j;
		public static Document objectRepository;
		public static String mainWindowHandle;
		public static String curTestName;
		
	}
	
	public static class Configure{
		
		public static final String PROPERTY_FILE_QA="Config\\env_qa.properties";
		public static final String PROPERTY_FILE_DEV="Config\\env_dev.properties";
		public static final String PROPERTY_FILE_EXECUTION_CONFIG="Config\\execution_config.properties";
		
		public static final String LOG4J_PROPERTIES="Config\\log4j.properties";
		public static final String REPOSITORY_PROPERTIES="Object Repositories\\Repository.xml";
		
		public static final String CHROME_DRIVER_PATH="Drivers\\chromedriver.exe";
		public static final String FIREFOX_DRIVER_PATH="Drivers\\geckodriver.exe";
		public static final String IE_DRIVER_PATH="Drivers\\IEDriverServer.exe";
		public static final String EDGE_DRIVER_PATH="Drivers\\MicrosoftWebDriver.exe";
		
	}
}
