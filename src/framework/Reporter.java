package framework;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.log4testng.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Reporter extends Configuration {

	private static final ThreadLocal<Reporter> T = new ThreadLocal<Reporter>();

	public static Reporter get() {
		return T.get();
	}

	public static void set(Reporter reporter) {
		T.set(reporter);
	}

//*********************************************************************************************************************************************

	@BeforeTest
	public void initiateReport(ITestContext ctx) throws IOException {
		UtilityMethods.set(new UtilityMethods());

		UtilityMethods.get()
				.makePath(System.getProperty("user.dir") + Data.Common.executionConfigData.get("screenshotPath"));
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String logFilePath = System.getProperty("user.dir") + "\\"
				+ Data.Common.executionConfigData.get("extentReportPath") + "\\" + ctx.getSuite().getName() + " - "
				+ timeStamp + ".html";
		Data.Common.curTestName = ctx.getName();
		Data.Common.htmlReporter = new ExtentHtmlReporter(logFilePath);
		Data.Common.reports = new ExtentReports();

		Data.Common.reports.attachReporter(Data.Common.htmlReporter);
		
		try {

			Data.Common.reports.setSystemInfo("Host Name", InetAddress.getLocalHost().getHostName());
			Data.Common.reports.setSystemInfo("IP Address", InetAddress.getLocalHost().getHostAddress());
			Data.Common.reports.setSystemInfo("User Name", System.getProperty("user.name"));

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		Data.Common.htmlReporter.config()
				.setDocumentTitle("Execution results for Suit : " + ctx.getSuite().getName() + " - " + timeStamp);
		Data.Common.htmlReporter.config().setReportName("Report for Test : " + ctx.getSuite().getName());
		Data.Common.htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		Data.Common.htmlReporter.config().setTheme(Theme.STANDARD);
	}

//************************************************************************************************************************************************

	@AfterTest(alwaysRun = true)
	public void afterTest(ITestContext ctx) {

		Status currentResult = Data.Common.test.getStatus();

		switch (currentResult) {
		case PASS:
			Data.Common.test.log(Status.PASS,
					MarkupHelper.createLabel("Test Case has been Passed.", ExtentColor.GREEN));
			Assert.assertTrue(true);
			break;

		case FAIL:
			Data.Common.test.log(Status.FAIL, MarkupHelper.createLabel("Test Case has been Passed.", ExtentColor.RED));
			Assert.fail("Test " + ctx.getName() + " has been failed.");
			break;

		case WARNING:
			Data.Common.test.log(Status.WARNING,
					MarkupHelper.createLabel("Test Case has been Passed.", ExtentColor.YELLOW));
			Assert.assertTrue(true, "Test " + ctx.getName() + " passed with warnings.");
			break;

		default:
			break;
		}

		Data.Common.reports.flush();
	}

//************************************************************************************************************************************************
	/**
	 * Takes a screenshot when there is an assertion failure
	 * 
	 * @param locator - The current ID, Xpath etc. that has not been found on the
	 *                page
	 */
	public String getScreenshot(String locator) {
		
		String legalNamelocator = locator.toString().replaceAll("[^a-zA-Z0-9\\._]+", "");
		String screenShotDir = System.getProperty("user.dir") + "\\"+ Data.Common.executionConfigData.get("screenshotPath");
		String timeStamp=new SimpleDateFormat("MM.dd_HH.mm.ss:SSS").format(new Date());
		File screenshot = ((TakesScreenshot) Driver.get()).getScreenshotAs(OutputType.FILE);
		
		String newFilePath = legalNamelocator + "_"+ timeStamp + "_" + Data.Common.curTestName+ ".png";
		File newFileName = new File(newFilePath);

		try {
			FileUtils.moveFile(screenshot,newFileName);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return newFilePath;

	}// End screenShot (String locator)

//****************************************************************************************************************************************

	public static void highlightElement(By by, String stepName, int maxTimeToWait) {
		WebElement element = Events.get().waitForElementToDisplay(by, stepName, maxTimeToWait);
		JavascriptExecutor jse = (JavascriptExecutor) Driver.get();
		jse.executeScript("arguments[0].style.border='3px solid red'", element);
	}

//***************************************************************************************************************************

	public void highlightElement(WebElement element, String stepName, int maxTimeToWait) {
		JavascriptExecutor jse = (JavascriptExecutor) Driver.get();
		jse.executeScript("arguments[0].style.border='3px solid red'", element);
	}

//************************************************************************************************************************************

	public String captureScreenshotToBase64(String imageName) {
		String encodedfile = "";

		String ImagePath = getScreenshot(imageName);

		try {
			File imageFile = new File(ImagePath);
			FileInputStream fileInputStreamReader = new FileInputStream(imageFile);
			byte[] bytes = new byte[(int) imageFile.length()];
			fileInputStreamReader.read(bytes);
			
			encodedfile = Base64.getEncoder().encodeToString(bytes);
			fileInputStreamReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return encodedfile;
	}

//********************************************************************************************************************************************

	public void writeLog(String status, String stepName, String actualResult) {
		Status stepStatus;

		switch (status.toLowerCase()) {
		case "pass":
			stepStatus = Status.PASS;
			break;
		case "fail":
			stepStatus = Status.FAIL;
			break;

		case "warning":
			stepStatus = Status.WARNING;
			break;

		default:
			stepStatus = Status.INFO;
			break;

		}

		Data.Common.test.log(stepStatus, stepName + '\n' + '\n' + actualResult);
	}

//**************************************************************************************************************************

	public void writeLog(String status, String stepName, String actualResult, String screenshotName) {
		Status stepStatus;

		switch (status.toLowerCase()) {
		case "pass":
			stepStatus = Status.PASS;
			break;
		case "fail":
			stepStatus = Status.FAIL;
			break;

		case "warning":
			stepStatus = Status.WARNING;
			break;

		default:
			stepStatus = Status.INFO;
			break;
		}

		try {
			Data.Common.test.log(stepStatus,
					"Step Name : " + stepName + '\n' + '\n' + "Actual Result : " + actualResult, MediaEntityBuilder
							.createScreenCaptureFromBase64String(captureScreenshotToBase64(screenshotName)).build());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
//**************************************************************************************************************************************************

	public void setlog4jPropertyFile(ITestContext ctx) throws IOException {

		Data.Common.log4j = Logger.getLogger(ctx.getClass());
		String log4JPropertyFile = Data.Configure.LOG4J_PROPERTIES;
		Properties p = new Properties();

		try {
			p.load(new FileInputStream(log4JPropertyFile));
			PropertyConfigurator.configure(p);

		} catch (IOException e) {
			System.out.println("Failed to log the log4j.properties file in Config folder.");
			throw new IOException("failed to load the log4j.properties.");
		}
	}
}
