package builder;

import oekoflex.OekoflexAgent;
import pfc.PriceForwardCurve;
import prerun.PriceForwardCurveGenarator;
import org.apache.commons.io.FileUtils;

import agents.FlexPowerplant;
import agents.Storage;
import demand.ResidualLoad;
import factories.FlexPowerplantFactory2;
import factories.StorageFactory;
import loggers.LogWriterBPM;
import loggers.LogWriterEOM;
import markets.BalancingMarketOperator;
import markets.DemandBPM;
import markets.EnergyOnlyMarketOperator;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import util.SequenceDefinition;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;


/**
 * Builds the entire context for Repast simphony.
 * <uL>
 * 	<li> simulates tick > 0
 * 	<li> builds the price forward curve
 * 	<li> instances all agents
 * </ul>
 * @implements ContextBuilder<OekoflexAgent>
 */
public class OekoFlexContextBuilder implements ContextBuilder<OekoflexAgent> {

    
    /** */ 
    public static final Locale defaultlocale = Locale.GERMAN;
    
    /** */
    public static NumberFormat defaultNumberFormat;
    
    
    static {
        Locale.setDefault(OekoFlexContextBuilder.defaultlocale);
        OekoFlexContextBuilder.defaultNumberFormat = DecimalFormat.getNumberInstance();
    }
    
    /**
     * 
     */
	@SuppressWarnings("rawtypes")
	@Override
    public Context build(Context<OekoflexAgent> context) {
    	
		List<Integer> maxLogVal = new ArrayList<Integer>();
        context.setId("oekoFlex");

        RunEnvironment re = RunEnvironment.getInstance();
        Parameters p = re.getParameters();
        
        int daysToRun   = (int) p.getValue("daysToRun");
        int prerunDays  = (int) p.getValue("preRunDays");
        int prerunTicks = SequenceDefinition.DayInterval * prerunDays;

        boolean loggingActivated = true;
        
        re.endAt(daysToRun * SequenceDefinition.DayInterval - 1);


        String scenario      = (String) p.getValue("scenario");
        String logDirName    = "run/summary-logs/" + scenario;
        String configDirName = "run-config/" + scenario;
        String priceForwardOutDirName = configDirName + "/price-forward/";

        File configDir = new File(configDirName);
        if (!configDir.exists()) {
            re.endRun();
        }

        try {
            
        	File priceForwardOutDir = new File(priceForwardOutDirName);
            if (priceForwardOutDir.exists()) {
                FileUtils.deleteDirectory(priceForwardOutDir);
                priceForwardOutDir.mkdir();
            }
            
            // Delete if not useful
//            if (loggingActivated) {
//                File logDir = new File(logDirName);
//                FileUtils.deleteDirectory(logDir);
//
//            }

            Properties globalProperties = loadProperties(configDir);
            // Hier kann ich abfragen was ich alles in den globalProperties gespeichert habe
            
            // Create the loggers
            String bpmPosLogName = "bpmPosLog.csv";
            String bpmNegLogName = "bpmnegLog.csv";
            LogWriterBPM bpmPosLogger = new LogWriterBPM(configDirName, bpmPosLogName);
            LogWriterBPM bpmNegLogger = new LogWriterBPM(configDirName, bpmNegLogName);
            
            String eomFilename = "eomLog.csv";
            LogWriterEOM eomLogger = new LogWriterEOM(configDirName, eomFilename);
            
            // Demand for the  BalancingMarketOperator
            File demandfile = new File(configDirName + "/BalancingVolume.cfg.csv");
            DemandBPM demandCapacity = new DemandBPM(demandfile);						
            DemandBPM demandWork     = new DemandBPM(demandfile);					

            // Market Operators
            EnergyOnlyMarketOperator energytMarketOperator = new EnergyOnlyMarketOperator("EOM_Operator",loggingActivated , logDirName );
            context.add(energytMarketOperator);
            
            BalancingMarketOperator balancingMarketOperator = new BalancingMarketOperator("BalancingMarketOperator", loggingActivated, logDirName);
            context.add(balancingMarketOperator);
            balancingMarketOperator.addDemand(demandCapacity, demandWork);
            
            // PriceForwardCurve
            File priceForwardFile = new File(priceForwardOutDir, "price-forward.csv");
            PriceForwardCurveGenarator priceForwardCurveGenerator = new PriceForwardCurveGenarator("",configDir, daysToRun * SequenceDefinition.DayInterval, priceForwardFile, prerunDays * SequenceDefinition.DayInterval, globalProperties);
            PriceForwardCurve priceForwardCurve = new PriceForwardCurve(priceForwardFile);
     
            
            // Consumers
            String fileNameGridload   = "TotalLoadProfile.cfg.csv" ;
            String fileNameRenewables = "RenewablesProfile.cfg.csv" ;
            ResidualLoad residLoad = new ResidualLoad(configDirName, fileNameGridload, fileNameRenewables);
            residLoad.setSpotMarketOperator(energytMarketOperator);
            context.add(residLoad);
            
            //Producers
            Set<FlexPowerplant> flexPowerplants =  FlexPowerplantFactory2.build(configDir, globalProperties, energytMarketOperator, balancingMarketOperator, priceForwardCurve);
            for (FlexPowerplant flexPowerplant : flexPowerplants) {
            	flexPowerplant.setLoggers(bpmPosLogger, bpmNegLogger, eomLogger);
	            context.add(flexPowerplant);
	        }
            
            maxLogVal.add(FlexPowerplantFactory2.getNumberOfInstances());
            
            Set<Storage> storages = StorageFactory.build(configDir, globalProperties, energytMarketOperator, balancingMarketOperator, priceForwardCurve);
            for (Storage storage : storages) {
            	if(storage != null){
            		storage.setLoggers(bpmPosLogger, bpmNegLogger, eomLogger);
	            context.add(storage);
            	}
            	else{
            		System.out.println("Storage Null");
            	}
	        }
            
            maxLogVal.add(StorageFactory.getNumberOfInstances());
                        
            // build pfc
            priceForwardCurveGenerator.generate();
            priceForwardCurve.readData();

            
          
            // Write dynamic Header for the logfiles
            eomLogger.writeHeader(FlexPowerplantFactory2.getNumberOfInstances());
            
            // prerun
            final PreRunner preRunner = new PreRunner(context);
            preRunner.loggerEOM = eomLogger;
            preRunner.run(prerunTicks);
            
        } catch (IOException | ParseException e) {
            re.endRun();
            System.exit(-1);
        }

        return context;
    }
    
    /**
     * @param configDir
     * @return Properties with globally used variables etc.
     * @throws IOException
     */
    public static Properties loadProperties(final File configDir) throws IOException {
        Properties globalProperties = new Properties();
        File globalPropertiesFile = new File(configDir, "Global.properties");
        FileInputStream is = new FileInputStream(globalPropertiesFile);
        globalProperties.load(is);
        
        return globalProperties;
    }
    
    public static int getMaxLog(){
		return 0;		// TODO add real Thingy
		// Oder ich geb den ganzen shit später runter zu den Loggern per Konstruktor??!!!
    
    }

}
