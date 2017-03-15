package agents;


import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import bid.BidBPM;
import bid.BidEOM;
import bid.BidType;
import markets.BPMTestOP2;
import markets.BalancingMarketOperator;
import markets.EOMTestOP;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;
//name		description		powerMax [MW]	powerMin [MW]	efficiency [%]	rampUp [MW/15min]	rampDown [MW/15min]	variablelCosts [EUR/MWh]	shutdownCosts [EUR/MW]	startupCosts  [EUR/MW]
//NEURATH F	lignite			1.120			280				0,434			336					336					20,75						2,7						45



/**
 * Tests for the flexible power plant: some may be in other Test classes
 *  for the tests of the efficiency loss see separate JUnit Test in FPP_EffiencyLoss_Test.java
 * @author andre
 *
 */

//27.02.2017  	The Test are not running correctly due to all the changes done by Thomas Kuenzel
//				TODO Change and write Tests again	

public class FPP_FlexPowerplantTest {
	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	
	String name 					= "NEURATH F" ;
		String description 			= "lignite";
		float powerMax 				= 1120.0f;
		float powerMin 				= 280.0f;
		float efficiency 			= 0.434f;
		float powerRampUp 			= 336.0f;
		float powerRampDown 		= 336.0f;
		float variableCosts 		= 20.75f;
		float shutdownCosts 		= 2.7f;
		float startupCosts 			= 45.0f;
		float specificFuelPrice 	= 5.4f;
		float co2certicicateprice 	= 7.05f;
		float emmissionfactor 		= 0.410f;
		long foresight 				= 96; 
		String priceForwardOutDir 	= "run-config/s1/";
		File priceForwardFile = 	new File(priceForwardOutDir, "price-forward.csv");
		
		
	public void printBPMList(String headerText, List<BidBPM> list){
		System.out.println(headerText);
		for (Iterator<BidBPM> iterator = list.iterator(); iterator.hasNext();) {
			BidBPM bidBPM = (BidBPM) iterator.next();
			System.out.println(bidBPM.getQuantity() + " " +  bidBPM.getPriceCapacity() + " " + bidBPM.getPriceWork());
			
		}
		System.out.println("\n\n");
	}
	
	public void printEOMList(String headerText, List<BidEOM> list){
		System.out.println(headerText);
		for (Iterator<BidEOM> iterator = list.iterator(); iterator.hasNext();) {
			BidEOM bid = (BidEOM) iterator.next();
			System.out.println(bid.getQuantity() + " " +  bid.getPrice());
			
		}
		System.out.println("\n\n");
	}
		
		
	@Before
	public void setUp() throws Exception {
		eom = new EOMTestOP( "EOMTestOP" , true, "run/test-logs/EnergyOnlyMarketOperatorTest");
		bpm = new BPMTestOP2("BPMTestOP2", true, "run/test-logs/BAlancingPowerMarketOperatorTest");
		pfc = new PriceForwardCurve(priceForwardFile);
		pfc.readData();
		fp  = new FlexPowerplant(name, description, powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);
	}

	@SuppressWarnings("unused")
	@Test
	public void testMakeBidEOMLong() {
//		fp.makeBidBalancingMarket();
//		List<BidBPM> lpos = ((BPMTestOP2)bpm).getPositiveSupplies();
//		List<BidBPM> lneg = ((BPMTestOP2)bpm).getNegativeSupplies();
		
		
//		bpm.clearMarketCapacityPrice();
		
//		System.out.println("Current power: " + fp.getCurrentPower());

		//fp.makeBidEOM();

		FlexPowerplant fpMock =  new FlexPowerplant("MOCK", "lignite", powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);
		
//		eom.addDemand(new BidEOM(1000, 3000, 0, BidType.ENERGY_DEMAND, fpMock));
		eom.addDemand(new BidEOM(1000, 3000, 0, BidType.ENERGY_DEMAND, fpMock));
		
		eom.addSupply(new BidEOM(800, -100, 0, BidType.ENERGY_SUPPLY, fpMock));
		
		List<BidEOM> demands = eom.getLastEnergyDemands();
		List<BidEOM> supplies = eom.getLastEnergySupplies();
		
//		printEOMList("Demands: ", demands);
//		printEOMList("supplies: ", supplies);
		
//		bpm.clearLists();
		demands  = eom.getLastEnergyDemands();
		supplies = eom.getLastEnergySupplies();
//		printEOMList("Demands: ", demands);
//		printEOMList("supplies: ", supplies);
		
		eom.clearMarket();
		demands  = eom.getLastEnergyDemands();
		supplies = eom.getLastEnergySupplies();
		
//		System.out.println("Vor bpm abruf " + fp.getCurrentPower());

//		
//		printEOMList("Demands: ", demands);
//		printEOMList("supplies: ", supplies);
//		
//		
//		printEOMList("Demands: ", demands);
//		printEOMList("supplies: ", supplies);
		
		bpm.clearMarketEnergyPrice();
		
//		System.out.println("e_ti: " + fp.getCurrentPower());
		
	}
	

	// Leistungspreis am Regelenergiemarkt – negative Regelleistung [Euro/MW] 
	// Arbeitspreis am Regelenergiemarkt – negative Regelleistung [Euro/MW] 
	// Gebotsmenge am Regelenergiemarkt – negative Regelleistung [MW]
	
}
