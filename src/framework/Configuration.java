package framework;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class Configuration {

	private static final ThreadLocal<Configuration> T = new ThreadLocal<Configuration>();

	public static Configuration get() {
		return T.get();
	}

	public static void set(Configuration configuration) {
		T.set(configuration);
	}

//***************************************************************************************************************

	@Parameters("environment")
	@BeforeSuite
	public void beforeSuit(@Optional String environment) {
		this.getEnvDetails(environment);
		UtilityMethods.loadRepository("Object Repositories\\Repository.xml");

	}
   
	public void getEnvDetails(@Optional String environment) {

		environment = (environment == null) ? "qa" : environment;
		System.out.println(environment);

		switch (environment.toLowerCase()) {
		case "qa":
			Data.Common.envConfigData = UtilityMethods.readPropertiesToMap("Config\\env_qa.properties");
			break;

		case "dev":
			Data.Common.envConfigData = UtilityMethods.readPropertiesToMap("Config\\env_dev.properties");
			break;

		default:
			Assert.fail("The environment : " + environment + " is invalid. Environment must be either qa or dev.");
			break;
		}

		Data.Common.executionConfigData = UtilityMethods.readPropertiesToMap("Config\\execution_config.properties");

	}
//*************************************************************************************************************************

	@BeforeMethod()
	public void closeBrowsers() {
		Events.set(new Events());
		UtilityMethods.set(new UtilityMethods());
		Pages.set(new Pages());
		Data.set(new Data());
		Validators.set(new Validators());
		Reporter.set(new Reporter());
		Common.set(new Common());

		try {
			Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
			Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
			Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
			Runtime.getRuntime().exec("taskkill /F /IM microsoftedge.exe");
			Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
			Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
			Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
			Runtime.getRuntime().exec("taskkill /F /IM edgedriver.exe");
		} catch (IOException e) {

			System.out.println("Exception while closing the browsers.");
		}
	}
}
