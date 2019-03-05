package framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;

public class UtilityMethods {

	private static final ThreadLocal<UtilityMethods> T = new ThreadLocal<UtilityMethods>();

	public static UtilityMethods get() {
		return T.get();
	}

	public static void set(UtilityMethods utilityMethods) {
		T.set(utilityMethods);
	}

//*****************************************************************************************************************************************

	public static String[] getObjectIdentification(String objectName) {
		String[] propCol = new String[2];
		NodeList propVal = Data.Common.objectRepository.getElementsByTagName(objectName);

		if (propVal.getLength() == 0) {
			System.out.println(
					"ERROR : The properties are not defined for object : " + objectName + " in Object Repository file");
			System.exit(0);
		} else if (propVal.getLength() > 1) {
			System.out.println("WARNING : there are multiple objects listed in Repostiroy file with same name : "
					+ objectName + " Please keep the object name (Element Name) as unque name.");
			System.exit(0);
		} else {
			Element TOElem = (Element) propVal.item(0);

			if (TOElem.hasAttribute("locator")) {
				propCol[0] = TOElem.getAttribute("locator");
			} else {
				System.out.println("ERROR : the TO Element :" + objectName
						+ " does not have locator attribute in Object Repository File");
				System.exit(0);
			}

			if (TOElem.hasAttribute("value")) {
				propCol[1] = TOElem.getAttribute("value");
			} else {
				System.out.println("ERROR : the TO Element :" + objectName
						+ " does not have value for given locator in Object Repository File");
				System.exit(0);
			}
		}

		return propCol;
	}
//**************************************************************************************************************************************************************

	public static void loadRepository(String repFilePath) {

		File repFile = new File(repFilePath);
		if (repFile.exists()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;

			try {
				db = dbf.newDocumentBuilder();
				Data.Common.objectRepository = db.parse(repFile);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("ERROR : Object repository file  : " + repFilePath + " is not found.");
			System.exit(0);
		}
	}
//*********************************************************************************************************************************************

	public static By getBy_from_Repository(String objectName) {

		By by = null;
		String[] propertyCol = getObjectIdentification(objectName);

		switch (propertyCol[0].toLowerCase()) {

		case "id":
			by = By.id(propertyCol[1]);
			break;

		case "name":
			by = By.name(propertyCol[1]);
			break;

		case "tagname":
			by = By.tagName(propertyCol[1]);
			break;

		case "css":
			by = By.cssSelector(propertyCol[1]);

		case "linktext":
			by = By.linkText(propertyCol[1]);

		case "xpath":
			by = By.xpath(propertyCol[1]);
			break;

		case "partiallinktext":
			by = By.partialLinkText(propertyCol[1]);
			break;

		case "class":
			by = By.className(propertyCol[1]);
			break;
		default:
			System.out.println("The locator : " + propertyCol[0] + " is invalid for the object : " + objectName
					+ " in object repository file.");
			break;
		}

		return by;

	}
//*****************************************************************************************************************************************************************

	public static void enableProtectedMode() {
		final HKEYByReference key = Advapi32Util.registryGetKey(WinReg.HKEY_CURRENT_USER,
				"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\1",
				WinNT.KEY_READ | WinNT.KEY_WRITE);
		Advapi32Util.registrySetIntValue(key.getValue(), "2500", 0);

		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER,
				"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\1", "2500", 0);
		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER,
				"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\2", "2500", 0);
		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER,
				"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3", "2500", 0);
		Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER,
				"Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\4", "2500", 0);
	}

//*************************************************************************************************************************************************************************

	public void createFolder(String path) {
		File f = new File(path);

		if (!f.exists()) {
			f.mkdir();
		}
	}

//*************************************************************************************************************************************************************

	public void makePath(String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

//***********************************************************************************************************************************

	public static HashMap<String, String> readPropertiesToMap(String strPropertyFile) {
		HashMap<String, String> propertyData = new HashMap<>();

		try {
			FileInputStream fi = new FileInputStream(strPropertyFile);
			Properties propFile = new Properties();
			propFile.load(fi);
			Set<Object> allKeys = propFile.keySet();
			for (Object key : allKeys) {
				String propVal = propFile.getProperty(key.toString());
				propertyData.put(key.toString(), propVal);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propertyData;
	}

//***********************************************************************************************************************************************

	public void deleteAllFiles(String foldPath) {

		File f = new File(foldPath);
		if (f.exists() && f.isDirectory()) {
			File[] allFiles = f.listFiles();
			for (File file : allFiles) {
				try {
					file.delete();
				} catch (Exception E) {
					System.out.println("Exception while deleting the file : " + file.getName());
				}
			}
		}
	}
}
