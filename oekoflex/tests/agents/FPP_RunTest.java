package agents;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bid.BidBPM;
import bid.BidEOM;
import bid.BidType;
import markets.BPMTestOP3;
import markets.BalancingMarketOperator;
import markets.EOMTestOP;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;


//27.02.2017  	The Test are not running correctly due to all the changes done by Thomas Kuenzel
//				TODO Change and write Tests again	

public class FPP_RunTest {
	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	
	String name 					= "NEURATH F" ;
		String description 			= "lignite";
		float powerMax 				= 1120.0f;
		float powerMin 				= 560.0f;
		float efficiency 			= 0.434f;
		float powerRampUp 			= 504.0f;
		float powerRampDown 		= 504.0f;
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
		bpm = new BPMTestOP3("BPMTestOP3", true, "run/test-logs/BalancingPowerMarketOperatorTest_FPP_RunTest");
		pfc = new PriceForwardCurve(priceForwardFile);
		pfc.readData();
		fp  = new FlexPowerplant(name, description, powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);
		fp.setCurrentPower(powerMin);
		
//		((BPMTestOP3) bpm).setDemandCapacity(new DemandBPM());
//		((BPMTestOP3) bpm).setDemandWork(new DemandBPM());
	}

	@Test
	public void test() {
		fp.makeBidBalancingMarket();
		List<BidBPM> posSupplies1 = ((BPMTestOP3) bpm).getPositiveSupplies();
		List<BidBPM> negSupplies1 = ((BPMTestOP3) bpm).getNegativeSupplies();
		
		printBPMList("pos1 Supplies", posSupplies1);
		printBPMList("neg1 Supplies", negSupplies1);
		
		((BPMTestOP3) bpm).clearMarketCapacityPrice();
		List<BidBPM> posSupplies2 = ((BPMTestOP3) bpm).getPositiveSupplies();
		List<BidBPM> negSupplies2 = ((BPMTestOP3) bpm).getNegativeSupplies();
		
		printBPMList("pos2 Supplies", posSupplies2);
		printBPMList("neg2 Supplies", negSupplies2);
		
		System.out.println("Current Power: " + fp.getCurrentPower() + "\n");
		
		fp.makeBidEOM();
		// More Bids otherwise EOM throws exception
		
		FlexPowerplant fpX  = new FlexPowerplant(name, description, powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);

		BidEOM demand = new BidEOM(5000, 3000, 0, BidType.ENERGY_DEMAND, fpX);
		eom.addDemand(demand);
		
		//BidEOM supply = new BidEOM(2000, price, tick, btype, marketOperatorListener);
		//eom.addSupply(supply);
		
		
		
		
		eom.clearMarket();
		System.out.println(fp.getLastMustRun().toString());
		System.out.println(fp.getLastMustRunConfirmed().toString());
		
		
		fp.acknowledgeBidBPM();
		List<BidBPM> posSupplies3 = ((BPMTestOP3) bpm).getPositiveSupplies();
		List<BidBPM> negSupplies3 = ((BPMTestOP3) bpm).getNegativeSupplies();
		
		printBPMList("pos3 Supplies", posSupplies3);
		printBPMList("neg3 Supplies", negSupplies3);
		((BPMTestOP3) bpm).clearMarketEnergyPrice();
		List<BidBPM> posSupplies4 = ((BPMTestOP3) bpm).getPositiveSupplies();
		List<BidBPM> negSupplies4 = ((BPMTestOP3) bpm).getNegativeSupplies();
		
		printBPMList("pos4 Supplies", posSupplies4);
		printBPMList("neg4 Supplies", negSupplies4);
		
		System.out.println(fp.getPositiveWork().getQuantity());
		
		
		fp.updatePowerPreceding();
		System.out.println(fp.getPowerPreceding());

		
	}

}
