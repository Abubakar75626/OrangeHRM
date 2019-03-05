package framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.Assert;

public class Common {

	private static final ThreadLocal<Common> T = new ThreadLocal<Common>();

	public static Common get() {
		return T.get();
	}

	public static void set(Common common) {
		T.set(common);
	}

//*****************************************************************************************************
	public void launchApplication(String browser, String url) {

		switch (browser.toLowerCase()) {
		case "chrome":
			System.setProperty("webdriver.chrome.driver",Data.Configure.CHROME_DRIVER_PATH);
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--ignore-certificate-errors", "--disable-extensions", "--dns-prefetch-disable",
					"lang=en_US.UTF-8", "--disable-infobars", "--new-window", "--start-maximized");
			chromeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
			HashMap<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("credentials_enable_service", false);
			prefs.put("profile.password_manager_enabled", false);
			chromeOptions.setExperimentalOption("prefs", prefs);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Driver.set(new ChromeDriver(chromeOptions));
			Driver.get().get(url);

			break;
		case "ie":
			InternetExplorerOptions ieoptions = new InternetExplorerOptions();
			ieoptions.ignoreZoomSettings();
			ieoptions.destructivelyEnsureCleanSession();
			UtilityMethods.enableProtectedMode();
			Driver.set(new InternetExplorerDriver(ieoptions));
			// Data.Common.driver = new InternetExplorerDriver(ieoptions);
			Driver.get().get(url);

		case "firefox":
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.setProperty("webdriver.gecko.driver", Data.Configure.FIREFOX_DRIVER_PATH);
			FirefoxOptions opts = new FirefoxOptions().setLogLevel(FirefoxDriverLogLevel.TRACE);
			Driver.set(new FirefoxDriver(opts));
			Driver.get().get(url);
		default:
			break;
		}

		WebElement element = Events.get().waitForElementToDisplay(
				UtilityMethods.getBy_from_Repository("AfterLaunch"), "Launch the Application", 40);

		if (element != null) {
			Reporter.get().writeLog("pass", "Launch application", "Application has been launched.",
					"Launch Application");
		} else {
			Reporter.get().writeLog("fail", "Launch application", "Application WAS NOT launched.",
					"Launch Application");
			Assert.assertTrue(false, "Test Failed as Application was not launched.");
		}

		Data.Common.mainWindowHandle = Driver.get().getWindowHandle();
	}

//*********************************************************************************************************

	public void Login(String userName, String passWord) {

		Events.enterValue(UtilityMethods.getBy_from_Repository("UserName"), userName, "Enter UserName");
		Events.enterValue(UtilityMethods.getBy_from_Repository("Password"), passWord, "Enter Password");
		Events.get().clickElement(UtilityMethods.getBy_from_Repository("LoginButton"), "Click on Login Button.");

		WebElement AfterLogin = Events.get().waitForElementToDisplay(
				UtilityMethods.getBy_from_Repository("AfterLogin"),
				"Click on Login button after entering the details.", 30);

		if (AfterLogin != null) {
			Reporter.get().writeLog("pass", "Login to the Application with User : " + userName, "Login is successful.",
					"Login to Application");
		} else {
			Reporter.get().writeLog("fail", "Login to the Application with User : " + userName, "Login is Failed.",
					"Login to Application");
			Assert.assertTrue(false, "Skipping the test as login is failed.");
			// throw new SkipException("Execution has been stopped as login is failed.");
		}
	}

//***************************************************************************************************************************
	public void switchToMainWindow() {

		try {
			Driver.get().switchTo().window(Data.Common.mainWindowHandle);

		} catch (NoSuchWindowException nsw) {
			System.out.println("Main browser has been closed. Unable to switch to main window.");
		}
	}

//***************************************************************************************************
	public boolean switchToWindow(String pageTitle) {
		boolean isBrowserFound = false;
		Set<String> allHandles = Driver.get().getWindowHandles();

		for (String handle : allHandles) {
			Driver.get().switchTo().window(handle);
			String actPageTitle = Driver.get().getTitle();
			if (actPageTitle.contains(pageTitle)) {
				isBrowserFound = true;
				break;
			}
		}
		return isBrowserFound;
	}

//********************************************************************************************************
	public void switchToFrame(By by) {

		try {
			WebElement element = Data.Common.driver.findElement(by);
			Data.Common.driver.switchTo().frame(element);
		} catch (NoSuchElementException nse) {
			Reporter.get().writeLog("fail", "Switch to the frame : " + by.toString(),
					"No frame exists with given locator");
			Assert.assertTrue(false, "Test Failed as frame does not exist");
		}
	}
//************************************************************************************************************************

	public void GenerateReport() {
		try {
			// define a HTML String Builder
			StringBuilder htmlStringBuilder = new StringBuilder();
			// append html header and title
			htmlStringBuilder.append("<html><head><title>Selenium Test </title></head>");
			// append body
			htmlStringBuilder.append("<body>");
			// append table
			htmlStringBuilder.append("<table border=\"1\" bordercolor=\"#000000\">");
			// append row
			htmlStringBuilder
					.append("<tr><td><b>TestId</b></td><td><b>TestName</b></td><td><b>TestResult</b></td></tr>");
			// append row
			htmlStringBuilder.append("<tr><td>001</td><td>Login</td><td>Passed</td></tr>");
			// append row
			htmlStringBuilder.append("<tr><td>002</td><td>Logout</td><td>Passed</td></tr>");
			// close html file
			htmlStringBuilder.append("</table></body></html>");
			// write html string content to a file
			WriteToFile(htmlStringBuilder.toString(), "testfile.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//******************************************************************************************************************************

	public void WriteToFile(String fileContent, String fileName) throws IOException {
		String projectPath = System.getProperty("user.dir");
		String tempFile = projectPath + File.separator + fileName;
		File file = new File(tempFile);
		// if file does exists, then delete and create a new file
		if (file.exists()) {
			try {
				File newFileName = new File(projectPath + File.separator + "backup_" + fileName);
				file.renameTo(newFileName);
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// write to file with OutputStreamWriter
		OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
		Writer writer = new OutputStreamWriter(outputStream);
		writer.write(fileContent);
		writer.close();
	}
}
