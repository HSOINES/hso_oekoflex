package loggers;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import agents.FlexPowerplant;
import bid.BidEOM;
import bid.BidType;
import markets.BalancingMarketOperator;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;

public class LogWriterEOMTest {
	EOMLogInfo a, a2, a3, b;

	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	FlexPowerplant fp2;
	FlexPowerplant fp3;
	FlexPowerplant fp4;
	FlexPowerplant fp5;
	String name = "NEURATH F";
	String name2 = "HC FPP";
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

	LogWriterEOM logger;

	@Before
	public void setUp() throws Exception {

		fp = new FlexPowerplant("Lig_Test_FPP_1", "lignite", powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp2 = new FlexPowerplant("Lig_Test_FPP_2", "lignite", powerMax, powerMin, efficiency, powerRampUp,
				powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice,
				emmissionfactor, bpm, eom, pfc, foresight);
		fp3 = new FlexPowerplant("Lig_Test_FPP_3", "lignite", powerMax, powerMin, efficiency, powerRampUp,
				powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice,
				emmissionfactor, bpm, eom, pfc, foresight);
		fp4 = new FlexPowerplant("HC_Test_FPP_4", "hard coal", powerMax, powerMin, efficiency, powerRampUp,
				powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice,
				emmissionfactor, bpm, eom, pfc, foresight);
		fp5 = new FlexPowerplant("HC_Test_FPP_5", "hard coal", powerMax, powerMin, efficiency, powerRampUp,
				powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice,
				emmissionfactor, bpm, eom, pfc, foresight);

		logger = new LogWriterEOM("run-config/testLogger/", "myTestlogEOM.csv");
	}

	@Test
	public void test() {
		logger.writeHeader(3);
		for (int i = 0; i < 50; i++) {
			BidEOM b01 = new BidEOM(1, 1, i, BidType.ENERGY_DEMAND, fp);
			BidEOM b01c = new BidEOM(1, 1, i, BidType.ENERGY_DEMAND_CONFIRMED, fp);
			BidEOM b11 = new BidEOM(1, 1, i, BidType.ENERGY_SUPPLY_MUSTRUN, fp);
			BidEOM b11c = new BidEOM(1, 1, i, BidType.ENERGY_SUPPLY_MUSTRUN, fp);
			BidEOM b02 = new BidEOM(2, 2, i, BidType.ENERGY_DEMAND, fp2);
			BidEOM b02c = new BidEOM(2, 2, i, BidType.ENERGY_DEMAND_CONFIRMED, fp2);
			BidEOM b22 = new BidEOM(2, 2, i, BidType.ENERGY_SUPPLY_MUSTRUN, fp2);
			BidEOM b22c = new BidEOM(2, 2, i, BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED, fp2);
			
			EOMLogInfo e1 = new EOMLogInfo(b01, b01c);
			EOMLogInfo e2 = new EOMLogInfo(b11, b11c);
			EOMLogInfo e3 = new EOMLogInfo(b02, b02c);
			EOMLogInfo e4 = new EOMLogInfo(b22, b22c);
			logger.addToList(e3);
			logger.addToList(e1);
			logger.addToList(e4);
			logger.addToList(e2);
			logger.listToFile();
		}

	}

}
