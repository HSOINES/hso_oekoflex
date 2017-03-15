package pfc;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import org.junit.Before;
import org.junit.Test;

//27.02.2017 Test works fine

public class PriceForwardCurveTest {

	@Before
	public void setUp() throws Exception {
	}

	@SuppressWarnings("unused")
	@Test
	public void test() throws IOException, ParseException {
		String scenario = "scenario1";
		String logDirName = "run/summary-logs/" + scenario;
		String configDirName = "run-config/" + scenario;
		String priceForwardOutDirName = configDirName + "/price-forward/";

		File priceForwardOutDir = new File(priceForwardOutDirName);

		File configDir = new File(configDirName);
		File priceForwardFile = new File(priceForwardOutDir, "price-forward.csv");

		PriceForwardCurve priceForwardCurve = new PriceForwardCurve(priceForwardFile);
		priceForwardCurve.readData();

	}
}