package tests;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import framework.Common;
import framework.Data;
import framework.Reporter;

public class PIM extends Reporter {

	@Parameters("browser")
	@Test
	public void createEmployee(@Optional String browser) {
		
		Data.Common.test=Data.Common.reports.createTest("createEmployee");
		browser=(browser==null)?"chrome":"browser";
		Common.get().launchApplication(browser,Data.Common.envConfigData.get("url"));
		Common.get().Login(Data.Common.envConfigData.get("username"),Data.Common.envConfigData.get("password"));
	}
}
