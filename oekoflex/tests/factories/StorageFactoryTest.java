package factories;

import static org.junit.Assert.*;

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
import agents.Storage;


//27.02.2017 		Test works fine
//					TODO instead of just looking at the printed output add some  assertEquals() Methods
public class StorageFactoryTest {
	
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
				Set<Storage>storages = null;
				try {
					storages = StorageFactory.build(configDir, globalProperties,null,null,null);
				} catch (IOException e) {
					System.out.println("Exception");
					e.printStackTrace();
				}
				if (storages != null) {
					int i = 1;
					for (Storage storage : storages) {
						System.out.println(storage.getName() + " " + storage.getDescription());
						i++;
					}
					System.out.println("\nAnzahl Zeilen in Excel: " + i);
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
		//System.out.println(configDir.getPath());
		File globalPropertiesFile = new File(configDir, "Global.properties");
		FileInputStream is = new FileInputStream(globalPropertiesFile);
		globalProperties.load(is);
		return globalProperties;
	}
}
