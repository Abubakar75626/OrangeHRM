package framework;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Events {

	private static final ThreadLocal<Events> T = new ThreadLocal<Events>();

	public static Events get() {
		return T.get();
	}

	public static void set(Events events) {
		T.set(events);
	}

//****************************************************************************************************************

	public static boolean verifyElementExists(By by) {
		List<WebElement> elemCol = Driver.get().findElements(by);

		if (elemCol.size() == 0) {
			return false;
		} else {
			return true;
		}

	}

//****************************************************************************************************************
	public void closeCurrentWindow() {
		try {
			Driver.get().close();
		} catch (Exception e) {
			System.out.println("Exception generated while closing the current browser.");
		}
	}
//*********************************************************************************************************************

	public static boolean verifyElementNotExists(By by, int maxTime) {
		try {
			WebDriverWait wait = new WebDriverWait(Driver.get(), maxTime);
			wait.pollingEvery(Duration.ofMillis(300));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(by));

		} catch (Exception e) {
			System.out.println("Exception while waiting for element to be invisible.");
		}

		List<WebElement> elemCol = Driver.get().findElements(by);

		if (elemCol.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

//*************************************************************************************************************************************************************************************************	

	public static void enterValue(By by, String strValue, String stepName) {

		try {
			WebElement element = Driver.get().findElement(by);
			element.clear();
			element.sendKeys(strValue);

		} catch (NoSuchElementException nse) {
			System.out
					.println(stepName + " ; No Element found in the application with given Locator :" + by.toString());
		}
	}

// ******************************************************************************************************************************
	public String getTextFromElement(By by, String stepName) {
		WebElement element = waitForElementToDisplay(by, stepName, 20);
		String strElementText = "";
		try {
			strElementText = element.getText();
		} catch (NullPointerException nse) {
			nse.printStackTrace();
		}

		return strElementText;
	}

//*********************************************************************************************************************
	public void clickElement(By by, String stepName) {
		WebElement element = waitForElementToDisplay(by, stepName, 20);
		try {
			if (element.isEnabled()) {
				element.click();
			} else {
				System.out
						.println("FAILED : " + stepName + " ; Unable to click on the element is Element is disabled.");
			}
		} catch (NullPointerException nse) {
			// throw new SkipException("Execution stopped as there is not element found.");
			Assert.fail("Test has been failed");
		}
	}
//*********************************************************************************************

	public static void selectByVisisbleText(By by, String valueToSelect, String stepName) {

		try {
			WebElement element = Driver.get().findElement(by);
			Select listbox = new Select(element);
			int itemIndex = getItemIndexFromList(element, valueToSelect);

			if (itemIndex >= 0) {
				listbox.selectByIndex(itemIndex);
			} else {
				System.out.println(
						"FAIL : " + stepName + "; the value " + valueToSelect + " is not present in the listbox.");
				System.exit(0);
			}

		} catch (NoSuchElementException ne) {
			System.out
					.println("FAIL : " + stepName + "; unable to select the given value as the list box with locator  "
							+ by.toString() + " is not found in the application.");
		}
	}

//**********************************************************************************************************
	private static int getItemIndexFromList(WebElement element, String strValue) {

		List<WebElement> allOptions = element.findElements(By.tagName("option"));
		int itemIndex = -1;

		for (int i = 0; i < allOptions.size(); i++) {
			String optionText = allOptions.get(i).getText();

			if (optionText.equalsIgnoreCase(strValue)) {
				itemIndex = i;
				break;
			}
		}
		return itemIndex;
	}

//**************************************************************************************************************
	public boolean verifyElementDisplayed(By by, String stepName, int maxTime) {
		WebElement element = waitForElementToDisplay(by, stepName, maxTime);

		if (element != null) {
			return true;
		} else {
			return false;
		}
	}
//*************************************************************************************************************

	public WebElement waitForElementToDisplay(By by, String stepName, int maxTime) {
		WebElement element = null;
		try {
			WebDriverWait wait = new WebDriverWait(Driver.get(), maxTime);
			wait.pollingEvery(Duration.ofMillis(300));
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

		} catch (Exception e) {
			System.out.println("Element with locator : " + by.toString()
					+ " is not displayed in the application even after waiting for " + maxTime + " seconds.; Browser - "
					+ Data.Common.curTestName);
		}

		return element;
	}
//******************************************************************************************************************

	public void verifyAlertMessage(String strMessage) {

		Alert a = Data.Common.driver.switchTo().alert();
		String alertText = a.getText();
		if (alertText.equalsIgnoreCase(strMessage)) {
			System.out.println("SUCCESS : The text in alert is matched with expected message : " + strMessage);
		} else {
			System.out.println("FAIL : The text in alert message not matched with expected." + '\n'
					+ "Expected Message : " + strMessage + '\n' + "Actual Message : " + alertText);
		}
	}

//*********************************************************************************************************************

	public void acceptAlert() {
		Alert a = Data.Common.driver.switchTo().alert();

		if (a != null) {
			a.accept();
		} else {
			System.out.println("FAIL : No Alert is found in the application.");
		}
	}

//*****************************************************************************************************************

	public void declineAlert() {
		Alert a = Data.Common.driver.switchTo().alert();

		if (a != null) {
			a.dismiss();
		} else {
			System.out.println("FAIL : No Alert is found in the application.");
		}
	}

//**********************************************************************************************************************

	public void moveMouseOnToElement(WebElement element, String stepName) {
		Actions action = new Actions(Data.Common.driver);

		if (element != null) {
			action.moveToElement(element);
		} else {
			System.out.println("FAIL : " + '\n' + "STEP NAME : " + stepName + '\n'
					+ " Cause for Fail :  Unable to perform mouse move operation as element is null.");
		}

	}
//***********************************************************************************************************

	public void moveMouseOnToElement(By by, String stepName) {
		Actions action = new Actions(Data.Common.driver);

		try {
			WebElement element = Data.Common.driver.findElement(by);
			action.moveToElement(element);
		} catch (NoSuchElementException nse) {
			System.out.println("ERROR : " + '\n' + "STEP NAME : " + stepName + '\n'
					+ "Reason for Fail : No Element is found with given locator : " + by.toString());
		}
	}

//***********************************************************************************************************

	public void doubleClickOnElemenent(By by, String stepName) {
		Actions action = new Actions(Data.Common.driver);

		try {
			WebElement element = Data.Common.driver.findElement(by);
			action.doubleClick(element);
		} catch (NoSuchElementException nse) {
			System.out.println("ERROR : " + '\n' + "STEP NAME : " + stepName + '\n'
					+ "Reason for Fail : No Element is found with given locator : " + by.toString());
		}
	}

//***************************************************************************************************************
	public void doubleClickOnElemenent(WebElement element, String stepName) {
		Actions action = new Actions(Data.Common.driver);

		if (element != null) {
			action.doubleClick(element);
		} else {
			System.out.println(
					"FAIL : " + '\n' + "STEP NAME : " + stepName + '\n' + " Reason for Fail : Given element is null.");
		}
	}

//*******************************************************************************************************
	public void rightClickOnElement(WebElement element, String stepName) {
		Actions action = new Actions(Data.Common.driver);

		if (element != null) {
			action.contextClick(element);
		} else {
			System.out.println(
					"FAIL : " + '\n' + "STEP NAME : " + stepName + '\n' + " Reason for Fail : Given element is null.");
		}
	}

//***********************************************************************************************************
	public void rightClickOnElement(By by, String stepName) {
		Actions action = new Actions(Data.Common.driver);

		try {
			WebElement element = Data.Common.driver.findElement(by);
			action.contextClick(element);
		} catch (NoSuchElementException nse) {
			System.out.println("ERROR : " + '\n' + "STEP NAME : " + stepName + '\n'
					+ "Reason for Fail : No Element is found with given locator : " + by.toString());
		}
	}
//*********************************************************************************************************

	public void checkAlertPresent(int maxTime) {

		try {
			WebDriverWait wait = new WebDriverWait(Data.Common.driver, maxTime);
			wait.pollingEvery(Duration.ofMillis(200));
			wait.until(ExpectedConditions.alertIsPresent());
		} catch (Exception ex) {

		}
	}
}
