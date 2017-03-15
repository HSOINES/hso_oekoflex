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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import agents.FlexPowerplant;
import builder.OekoFlexContextBuilder;
import markets.BalancingMarketOperator;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;
import util.CSVParameter;

/**
 * Factory class for the flexible power plant 3 class
 * reads values of the csv files and creates fpp3 agents based on this information
 */
public class FlexPowerplantFactory2 {
	 	private static final Log log = LogFactory.getLog(FlexPowerplantFactory2.class);
	 	private static int numberOfInstances;
	 /**
	 * 
	 * @param configDir the config directory
	 * @param context the Repast context
	 * @param spotMarketOperator operator for the energy only market
	 * @param balancingMarketOperator operator for the balancing power market
	 * @param priceForwardCurve the specific price forward curve for this scenario
	 * @param globalProperties global properties for scenario
	 * @return 
	 * @throws IOException
	 */
	 public static Set<FlexPowerplant> build(File configDir, final Properties globalProperties, final EnergyOnlyMarketOperator spotMarketMarketOperator, final BalancingMarketOperator balancingMarketOperator,final PriceForwardCurve priceForwardCurve) throws IOException {
	        File configFile = new File(configDir + "/" + "FlexiblePowerplant_new.cfg.csv");
	        FileReader reader = new FileReader(configFile);
	        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
	        Set<FlexPowerplant> flexPowerplants = new HashSet<>();
	        numberOfInstances = 0;									// Number needed for the header of the csv Files
	        for (CSVRecord parameters : format) {
	            try {
	                String name = parameters.get("name");
	                String description = parameters.get("description");
	                int powerMax = Integer.parseInt(parameters.get("powerMax [MW]"));
	                int powerMin = Integer.parseInt(parameters.get("powerMin [MW]"));
	                float efficiency = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("efficiency [%]")).floatValue();
	                int rampUp = Integer.parseInt(parameters.get("rampUp [MW/15min]"));
	                int rampDown = Integer.parseInt(parameters.get("rampDown [MW/15min]"));
	                float variablelCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("variablelCosts [EUR/MWh]")).floatValue();
	                float shutdownCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("shutdownCosts [EUR/MW]")).floatValue();
	                float startupCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("startupCosts  [EUR/MW]")).floatValue();
	                float co2CertificateCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("CO2CertificatesCosts")).floatValue();
	                float specificFuelPrice = getFuelCosts(globalProperties, description);
					float emmissionfactor = getEmissionFactor(globalProperties, description);
					
					long foresight = 96; // TODO change to exogene Variable as soon as these are provided
					
					
					FlexPowerplant flexPowerplant = new FlexPowerplant(name, description, powerMax, powerMin, efficiency, rampUp, rampDown, variablelCosts, shutdownCosts, startupCosts, specificFuelPrice, co2CertificateCosts, emmissionfactor, balancingMarketOperator, spotMarketMarketOperator,  priceForwardCurve, foresight);
	                flexPowerplants.add(flexPowerplant);
	                numberOfInstances++;
	                
	                log.info("FlexPowerplant2 Build done for <" + name + ">.");
	            } catch (NumberFormatException e) {
	                log.error(e.getMessage(), e);
	                throw e;
	            } catch (ParseException e) {
	                log.error(e.toString(), e);
	            }
	        }
	        return flexPowerplants;
	    }

	    static float getFuelCosts(final Properties globalProperties, final String description) {
	        float variableCosts = 0;
	        try {
	        switch (description) {
	            case "lignite":
				
					variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("FuelCosts_Lignite")).floatValue();
				

	                break;
	            case "hard coal":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("FuelCosts_HardCoal")).floatValue();
	                break;
	            case "open cycle gas turbine":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("FuelCosts_NaturalGasOpenCycle")).floatValue();
	                break;
	            case "closed cycle gas turbine":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("FuelCosts_NaturalGasCombinedCycle")).floatValue();
	                break;
	            case "oil":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("FuelCosts_Oil")).floatValue();
	                break;
	            default:
	                throw new IllegalArgumentException("not supported type:" + description);

	        }
	        } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return variableCosts;
	    }

	    static float getEmissionFactor(final Properties globalProperties, final String description) {
	        float variableCosts = 0;
	        try {
	        switch (description) {
	            case "lignite":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("EmissionFactor_Lignite")).floatValue();
	                break;
	            case "hard coal":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("EmissionFactor_HardCoal")).floatValue();
	                break;
	            case "open cycle gas turbine":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("EmissionFactor_NaturalGasOpenCycle")).floatValue();
	                break;
	            case "closed cycle gas turbine":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("EmissionFactor_NaturalGasCombinedCycle")).floatValue();
	                break;
	            case "oil":
	                variableCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(globalProperties.getProperty("EmissionFactor_Oil")).floatValue();
	                break;
	            default:
	                throw new IllegalArgumentException("not supported type:" + description);

	        }
	        } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return variableCosts;
	    }
	    
	    public static int getNumberOfInstances(){
			return numberOfInstances;
		}
	}
