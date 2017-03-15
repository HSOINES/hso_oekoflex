package agents;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bid.BidBPM;
import bid.BidEOM;
import markets.BPMTestOP2;
import markets.BalancingMarketOperator;
import markets.EOMTestOP;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;

//27.02.2017  	The Test are not running correctly due to all the changes done by Thomas Kuenzel
//				TODO Change and write Tests again	

public class FPP_Marginal_Costs_Test {

	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	
		String name 				= "NEURATH F" ;
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
	
	@Test
	public void testCalculateMarginalCosts() {
		float mc_expected = 39.852535f;
		float mc = FlexPowerplant.calculateMarginalCosts(specificFuelPrice, efficiency, co2certicicateprice, emmissionfactor, variableCosts);
		assertTrue(mc_expected-1 < mc && mc_expected+1 > mc);
	}
	
	
	// The function for the marginal costs BPM and EOM are the same so there is neither a seperate method nor seperate tests for both 
	
	/** Test with 0.0 [MW] power Preceding / running 50 % of possible max output */
	@Test
	public void marginalCostsBPM_Test_Lignite_01(){
		float expectedReturn = 52.66871f;
		float powerPreceding = 0.0f;			
		float retVal = this.fp.calculateMariginalCostBPM(powerPreceding);
		assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
	}
	
	
	/** Test with 560.0 [MW] power Preceding / running 50 % of possible max output */
	@Test
	public void marginalCostsBPM_Test_Lignite_02(){
		float expectedReturn = 42.087543f;
		float powerPreceding = 560.0f;			
		float retVal = this.fp.calculateMariginalCostBPM(powerPreceding);
		assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
	}

	/** Test with 840.0 [MW] power Preceding / running 75 % of possible max output */
	@Test
	public void marginalCostsBPM_Test_Lignite_03(){
		float expectedReturn = 40.7218f;
		float powerPreceding = 840.0f;			
		float retVal = this.fp.calculateMariginalCostBPM(powerPreceding);
		assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
	}
	
	/** Test with 1120.0 [MW] power Preceding / running 100 % of possible max output */
	@Test
	public void marginalCostsBPM_Test_Lignite_04(){
		float expectedReturn = 39.85235f;
		float powerPreceding = 1120.0f;			
		float retVal = this.fp.calculateMariginalCostBPM(powerPreceding);
		assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
	}
	
	
	/** Test with every combination of x from 0 to max [MW] power Preceding / running 0 to 100 % of possible max output 
	 *  Checking if every Marginal Cost return is greater or equals to 0
	 */
	@Test
	public void marginalCostsBPM_Test_Lignite_05(){
		for(float powerPreceding = 0.0f; powerPreceding <= powerMax; powerPreceding += 1.0){
			float retVal = this.fp.calculateMariginalCostBPM(powerPreceding);
			assertTrue("Returned Value is: " + retVal, retVal >= 0.0f);
		}
			
		
		
	}

}
