package prerun;

import agents.FlexPowerplant;
import builder.OekoFlexContextBuilder;
import demand.ResidualLoad;
import factories.FlexPowerplantFactory2;
import markets.EnergyOnlyMarketOperator;
import util.CSVParameter;
import util.TimeUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class PriceForwardCurveGenarator {
	 private static final Log log = LogFactory.getLog(PriceForwardCurveGenarator.class);

	    private int ticksToRun;
	    private final long prerunTicks;

	    private final Set<FlexPowerplant> flexPowerplants;
	    private final EnergyOnlyMarketOperator eomOpeartor;
	    ResidualLoad residualLoad;

	    private CSVPrinter csvPrinter;

	    public PriceForwardCurveGenarator(String configDirectory, File configDir, int ticksToRun, final File priceForwardFile, long prerunTicks, final Properties globalProperties) throws IOException {
	        this.ticksToRun  = ticksToRun;
	        this.prerunTicks = prerunTicks;
	        eomOpeartor = new EnergyOnlyMarketOperator("pfc-spotmarketoperator", false,"");
	        
	        
	        flexPowerplants = FlexPowerplantFactory2.build(configDir, globalProperties, eomOpeartor, null, null);  	
	        																												
	        for (FlexPowerplant flexPowerplant : flexPowerplants) {
	            flexPowerplant.setSpotMarketOperator(eomOpeartor);
	        }

	        // Residualload 
	        String fileNameGridload   = "TotalLoadProfile.cfg.csv" ;		// Later change to dynamic Filenames
            String fileNameRenewables = "RenewablesProfile.cfg.csv" ;		// Later change to dynamic Filenames
            residualLoad = new ResidualLoad(configDir.getAbsolutePath(), fileNameGridload, fileNameRenewables);
	        residualLoad.setSpotMarketOperator(eomOpeartor);
	        if (!priceForwardFile.getParentFile().exists()) {
	            if (!priceForwardFile.getParentFile().mkdirs()) {
	                throw new IllegalStateException("couldn't create directories.");
	            }
	        }
	        
	        final Appendable out;
	        try {
	            out = new FileWriter(priceForwardFile);
	            csvPrinter = CSVParameter.getCSVFormatDefault().withHeader("tick", "price").print(out);
	        } catch (IOException e) {
	            log.error(e.getLocalizedMessage(), e);
	        }
	    }

	    public void generate() {
	    	
	        TimeUtil.startAt(-prerunTicks);
	        
	        for (long tick = -prerunTicks; tick <ticksToRun ; tick++) {
	            log.debug("Building pfc for tick: " + tick);
	            
	            residualLoad.makeBidEOM(tick);
	            
	            for (FlexPowerplant flexPowerplant : flexPowerplants) {
	                flexPowerplant.makeBidEOM(tick);
	            }
	            
	            eomOpeartor.clearMarket();
	            
	            for (FlexPowerplant flexPowerplant : flexPowerplants) {
	                flexPowerplant.updatePowerPreceding();
	            }
	            
	            logPriceForward(tick, csvPrinter);
	            TimeUtil.nextTick();
	        }
	        
	        TimeUtil.reset();

	        try {
	            csvPrinter.close();
	        } catch (IOException e) {
	            log.error(e.getMessage(), e);
	        }
	    }

	    public void logPriceForward(long tick, CSVPrinter csvPrinter) {
	        try {
	            csvPrinter.printRecord(tick, OekoFlexContextBuilder.defaultNumberFormat.format(eomOpeartor.getLastClearedPrice()));
	        } catch (IOException e) {
	            log.error(e.toString(), e);
	        }
	    }

	}
