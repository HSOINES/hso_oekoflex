package loggers;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import agents.FlexPowerplant;
import bid.BidBPM;
import bid.BidType;
import markets.BalancingMarketOperator;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;


/** 27.02.2017 	Test works fine
*				TODO instead of just looking at the csv File and see if the file is sorted accordingly ,
*						if we have the time we could add a method to read the csv and use assertEquals() Methods
**/

public class LogWriterBPMTest {

	BPMLogInfo a,a2,a3,b;
	
	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	FlexPowerplant fp2;
	FlexPowerplant fp3;
	FlexPowerplant fp4;
	FlexPowerplant fp5;
	String name = "NEURATH F";
	String name2 ="HC FPP";
	String description = "lignite";
	String description2 = "hard coal";
	String description3 = "closed cycle gas turbine";
	String description4 = "open cycle gas turbine";
	float powerMax = 1120.0f;
	float powerMin = 280.0f;
	float efficiency = 0.434f;
	float powerRampUp = 336.0f;
	float powerRampDown = 336.0f;
	float variableCosts = 20.75f;
	float shutdownCosts = 2.7f;
	float startupCosts = 45.0f;
	float specificFuelPrice = 5.4f;
	float co2certicicateprice = 7.05f;
	float emmissionfactor = 0.410f;
	long foresight = 96;
	String priceForwardOutDir = "run-config/s1/";
	File priceForwardFile = new File(priceForwardOutDir, "price-forward.csv");

	LogWriterBPM logger ;
	
	@Before
	public void setUp() throws Exception {

		fp = new FlexPowerplant(new String("Lig_Test_FPP_1"), new String("lignite"), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp2 = new FlexPowerplant(new String("Lig_Test_FPP_2"), new String("lignite"), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp3 = new FlexPowerplant(new String("Lig_Test_FPP_3"), new String("lignite"), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp4 = new FlexPowerplant(new String("HC_Test_FPP_4"), new String("hard coal"), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp5 = new FlexPowerplant(new String("HC_Test_FPP_5"), new String("hard coal"), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		
		
		
		
	
		logger = new LogWriterBPM("run-config/testLogger/","myTestlogBPM4.csv");
	}
	
	@Test
	public void test() {
		logger.writeHeader(3);
		for(int i = 0; i<50;i++){
			BidBPM bid1 = new BidBPM(1.0f, 10.0f, 100.0f, i, BidType.POWER_NEGATIVE, fp);
			BidBPM bid2 = new BidBPM(2.0f, 20.0f, 200.0f, i, BidType.POWER_NEGATIVE, fp2);
			BidBPM bid3 = new BidBPM(3.0f, 30.0f, 300.0f, i, BidType.POWER_NEGATIVE, fp3);
			BidBPM bid4 = new BidBPM(4.0f, 40.0f, 400.0f, i, BidType.POWER_NEGATIVE, fp4);
			BidBPM bid5 = new BidBPM(5.0f, 50.0f, 500.0f, i, BidType.POWER_NEGATIVE, fp5);
			
			System.out.println(bid1.getTick());
			BPMLogInfo b1  = new BPMLogInfo(bid1, 100 , true  );
			BPMLogInfo b2  = new BPMLogInfo(bid2, 200 , true  );
			BPMLogInfo b3  = new BPMLogInfo(bid3, 300 , true  );
			BPMLogInfo b4  = new BPMLogInfo(bid4, 400 , true  );
			BPMLogInfo b5  = new BPMLogInfo(bid5, 500 , true  );
			logger.addToList(b5);
			logger.addToList(b1);
			logger.addToList(b2);
			logger.addToList(b3);
			logger.addToList(b4);
			logger.listToFile();

		}
		
		
		
	}

}
