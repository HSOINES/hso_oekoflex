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
//27.02.2017 	Test works fine
//TODO 				instead of just looking at the printed output add some  assertEquals() Methods


public class BPMLogInfoTest {

	BPMLogInfo a;
	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	FlexPowerplant fp2;
	FlexPowerplant fp3;
	FlexPowerplant fp4;

	String name = "NEURATH F";
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

	@Before
	public void setUp() throws Exception {

		fp = new FlexPowerplant(new String( name), new String(description), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp2 = new FlexPowerplant(new String( name), new String(description2), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp3 = new FlexPowerplant(new String( name), new String(description3), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);
		fp4 = new FlexPowerplant(new String( name), new String(description4), powerMax, powerMin, efficiency, powerRampUp, powerRampDown,
				variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor,
				bpm, eom, pfc, foresight);

		BidBPM bn = new BidBPM(999.12f, 87.323f, 10.020100f, 0, BidType.POWER_NEGATIVE, fp);
		a = new BPMLogInfo(bn, 500, true );
	}

	@Test
	public void test() {
		System.out.println(a.toString());
	}
	
	

}
