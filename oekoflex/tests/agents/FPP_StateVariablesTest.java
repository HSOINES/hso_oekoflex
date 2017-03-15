package agents;

import static org.junit.Assert.*;

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

public class FPP_StateVariablesTest {
	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	
	String name 					= "NEURATH F" ;
		String description 			= "lignite";
		float powerMax 				= 1120.0f;
		float powerMin 				= 280.0f;
		float efficiency 			= 0.434f;
		float powerRampUp 			= 136.0f;
		float powerRampDown 		= 136.0f;
		float variableCosts 		= 20.75f;
		float shutdownCosts 		= 2.7f;
		float startupCosts 			= 45.0f;
		float specificFuelPrice 	= 5.4f;
		float co2certicicateprice 	= 7.05f;
		float emmissionfactor 		= 0.410f;
		long foresight 				= 96; 
		String priceForwardOutDir 	= "run-config/s1/";
		File priceForwardFile = 	new File(priceForwardOutDir, "pfc_fpp_state_variables_test_pfc.csv");
		
		
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

	/** 
	 * Test über den individuellen Deckungsbeitrag am EOM-Markt im Rahmen der Anfahrtskostendeckung [Euro/MW]
	 * <p> Fuer die Formel siehe Folie 54
	 * <p> Test mit den Parametern:
	 * <ul>
	 * 	<li> aktueller Tick:  0
	 * 	<li> marinale Kosten: 39.852535	(fixe Marginale Kosten)
	 *  <li> Foresight:		  96 (Modellexogene Vorgabe, 96 wurde für die Tests festgelegt)
	 *  <li> ticks_anfahrt :  3 
	 *  <li> ( ticks_anfahrt = Anzahl der Ticks zur Karfwerksanfahrt P_min / PRampUp ; mit PRampUp = 136 und Pmin = 280 )
	 * <ul>
	 */
	@Test
	public void calculateCMtest() {
		float expected_output = 59.147465f;
		float epsilon = 0.000001f;
		long currentTick = 0;
		float erg = fp.calculateCM(currentTick);
		assertTrue("expected: " + expected_output + " but was: " + erg , Math.abs(erg-expected_output) < epsilon);
		
	}
	
	@Test
	public void calculateCCtest() {
		boolean expected_output = false;
//		float zwErgCM = fp.calculateCM(currentTick);
		boolean erg = fp.calculateCC();
		assertTrue("expected: " + expected_output +" but was: " + erg, erg == false);
	}
	
	@Test
	public void calculateSUtest() {
		
	}
	
	@Test
	public void calculateSDtest() {
		
	}
	

	/** Tests the number of Ticks a fpp needs to start again, see Page 59 for the formula */
	@Test
	public void checkDFppSu() {
		int excepted_output = 3;
		assertTrue("Expected: " + excepted_output + " but was : " + fp.getdFppSU() ,fp.getdFppSU() == excepted_output );
	}
	
	
	@Test
	public void updateDurationTest(){
		
		assertEquals(fp.getForesightFPPSU(), 96);
		
		BidEOM bidNotZero = new BidEOM(100, 10, 0, BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED, fp);
		
		BidEOM bidZero = new BidEOM(0, 10, 0, BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED, fp);

		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		
		assertEquals(fp.getDurationRunning(), 6);

		fp.updateDuration(bidZero);
		assertEquals(fp.getDurationRunning(), 0);
		
		assertEquals(fp.calculateForesight(), 6);
				
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		fp.updateDuration(bidNotZero);
		assertEquals(fp.getDurationRunning(), 12);
		
		fp.updateDuration(bidZero);
		assertEquals(fp.getDurationRunning(), 0);
		assertEquals(fp.calculateForesight(), 9);
		
		for (int i = 0; i < 100; i++) {
			fp.updateDuration(bidNotZero);
		}
		assertEquals(fp.getDurationRunning(), 100);
		fp.updateDuration(bidZero);
		assertEquals(fp.getDurationRunning(), 0);
		assertEquals(fp.calculateForesight(), 39);
		
		
		for (int i = 0; i < 100; i++) {
			fp.updateDuration(bidNotZero);
		}
		
		assertEquals(fp.getDurationRunning(), 100);
		fp.updateDuration(bidZero);
		assertEquals(fp.getDurationRunning(), 0);
		assertEquals(fp.calculateForesight(), 54);
	}
}
