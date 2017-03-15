package factories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import agents.FlexPowerplant;


//27.02.2017 	Test works fine
//				TODO instead of just looking at the printed output add some  assertEquals() Methods
public class FlexPowerplantFactory2Test {
	
	
	
	File configDir;
	Properties globalProperties;
	Locale defaultlocale = Locale.GERMAN;
	NumberFormat defaultNumberFormat;

	@Before
	public void setUp() throws Exception {
		String scenario = "s1";
		String configDirName = "run-config/" + scenario;
		configDir = new File(configDirName);
		globalProperties = loadProperties(configDir);

		Locale.setDefault(defaultlocale);
		defaultNumberFormat = DecimalFormat.getNumberInstance();
	}

	@Test
	public void test() {
		// Producers
		Set<FlexPowerplant> flexPowerplants = null;
		try {
			flexPowerplants = FlexPowerplantFactory2.build(configDir, globalProperties, null, null, null);
		} catch (IOException e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		if (flexPowerplants != null) {
			int i = 1;
			for (FlexPowerplant flexPowerplant : flexPowerplants) {
				System.out.println(flexPowerplant.getName() + " " + flexPowerplant.getDescription());
				i++;
			}
			System.out.println("\nAnzahl Zeilen in Excel(letzte beschriebene Zeilennummer muss sein): " + i);
		} else {
			System.out.println("FPPS sind NULL");
		}

	}

	/**
	 * @param configDir
	 * @return Properties with globally used variables etc.
	 * @throws IOException
	 */
	public static Properties loadProperties(final File configDir) throws IOException {
		Properties globalProperties = new Properties();
		System.out.println(configDir.getPath());
		File globalPropertiesFile = new File(configDir, "Global.properties");
		FileInputStream is = new FileInputStream(globalPropertiesFile);
		globalProperties.load(is);
		return globalProperties;
	}
	
	
}