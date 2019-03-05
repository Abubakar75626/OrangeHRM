package framework;

import org.openqa.selenium.WebDriver;

public class Driver {

	private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<WebDriver>();

	public static WebDriver get() {
		return driverThread.get();
	}

	public static void set(WebDriver driver) {
		driverThread.set(driver);
	}
}
