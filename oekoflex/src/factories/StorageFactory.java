package factories;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import agents.Storage;
import builder.OekoFlexContextBuilder;
import markets.BalancingMarketOperator;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;
import util.CSVParameter;

public class StorageFactory {

	private static int numberOfInstances;
	
	public static Set<Storage> build(File configDir, final Properties globalProperties,
			final EnergyOnlyMarketOperator energytMarketOperator,
			final BalancingMarketOperator balancingMarketOperator, final PriceForwardCurve priceForwardCurve)
			throws IOException {
		
		File configFile = new File(configDir + "/" + "Storage.cfg.csv");
		FileReader reader = new FileReader(configFile);
		CSVParser format = CSVParameter.getCSVFormat().parse(reader);
		Set<Storage> storages = new HashSet<>();
		for (CSVRecord parameters : format) {
			try {
				String name = parameters.get("name");
				String description = parameters.get("description");
				int chargePowerMax = Integer.parseInt(parameters.get("ChargePowerMax [MW]"));
				int dischargePowerMax = Integer.parseInt(parameters.get("DischargePowerMax [MW]"));
				float chargingEfficiency = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("chargingEfficiency [%]")).floatValue();
				float dischargingEfficiency = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("dischargingEfficiency [%]")).floatValue();
				float capacityMin = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("capacityMin [MWh]")).floatValue();
				float capacityMax = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("capacityMax [MWh]")).floatValue();
				float variableCostsCharging = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("variableCostsCharging [EUR/MWh]")).floatValue();
				float variableCostsDischarging = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("variableCostsDischarging [EUR/MWh]")).floatValue();

				// Construct
				Storage storage = new Storage(name, description, chargePowerMax, dischargePowerMax, chargingEfficiency,dischargingEfficiency, capacityMin, capacityMax, variableCostsCharging,variableCostsDischarging, priceForwardCurve, energytMarketOperator, balancingMarketOperator);
				storages.add(storage);

			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw e;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return storages;
	}
	
	 public static int getNumberOfInstances(){
			
			return numberOfInstances;
		}

}
