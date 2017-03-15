package agents;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import markets.BPMTestOP2;
import markets.BalancingMarketOperator;
import markets.EOMTestOP;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;
//27.02.2017  	The Test are not running correctly due to all the changes done by Thomas Kuenzel
//				TODO Change and write Tests again	

public class FPP_EffiencyLoss_Test {
	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	PriceForwardCurve pfc;
	FlexPowerplant fp;
	FlexPowerplant fp2;
	FlexPowerplant fp3;
	FlexPowerplant fp4;
	
	String name 				= "NEURATH F";
	String description 			= "lignite";
	String description2 		= "hard coal";
	String description3			= "closed cycle gas turbine";
	String description4 		= "open cycle gas turbine";
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
	File priceForwardFile 		= new File(priceForwardOutDir, "price-forward.csv");
		
	@Before
	public void setUp() throws Exception {
		eom = new EOMTestOP( "EOMTestOP" , true, "run/test-logs/EnergyOnlyMarketOperatorTest");
		bpm = new BPMTestOP2("BPMTestOP2", true, "run/test-logs/BAlancingPowerMarketOperatorTest");
		pfc = new PriceForwardCurve(priceForwardFile);
		pfc.readData();
		fp   = new FlexPowerplant(name, description , powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);
		fp2  = new FlexPowerplant(name, description2, powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);
		fp3  = new FlexPowerplant(name, description3, powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);
		fp4  = new FlexPowerplant(name, description4, powerMax, powerMin, efficiency, powerRampUp, powerRampDown, variableCosts, shutdownCosts, startupCosts, specificFuelPrice, co2certicicateprice, emmissionfactor, bpm, eom, pfc, foresight);

	}
	
// Tests for "lignite"
	
		/** Test with 0.0 [MW] power Preceding / running 0 % of possible max output */ 
		@Test
		public void calculateEffiencyLossTest_COAL_01(){
			float expectedReturn = 0.174262f;
			float pwoerPreceding = 0.0f;			
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 1120.0 [MW] power Preceding / running 100 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_COAL_02(){
			float expectedReturn = 0.0f;
			float pwoerPreceding = 1120.0f	;
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 560.0 [MW] power Preceding / running 50 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_COAL_03(){
			float expectedReturn = 0.0454606875f;
			float pwoerPreceding = this.powerMax/2;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 840.0 [MW] power Preceding / running 75 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_COAL_04(){
			float expectedReturn = 0.018889667f;
			float pwoerPreceding = 840.0f;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with every combination */
		@Test
		public void calculateEffiencyLossTest_COAL_05(){
			for(float pPreceding = 0.0f ; pPreceding <= powerMax; pPreceding+=1.0f ){
				float ratio = pPreceding/this.powerMax;
				float retVal = this.fp.calculateEffiencyLoss(ratio);
				assertTrue("Returned Value is: " + retVal + " for the Preceding Power of: " + pPreceding,retVal <= 1.0f && retVal >= 0.0f);
			}
		}
	
// Tests for "hard coal" should be the same results as tests above for "lignite"
	
		/** Test with 0.0 [MW] power Preceding / running 0 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_COAL_021(){
			float expectedReturn = 0.174262f;
			float pwoerPreceding = 0.0f;			
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp2.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 1120.0 [MW] power Preceding / running 100 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_COAL_022(){
			float expectedReturn = 0.0f;
			float pwoerPreceding = 1120.0f	;
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp2.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 560.0 [MW] power Preceding / running 50 % of possible output */
		@Test
		public void calculateEffiencyLossTest_COAL_023(){
			float expectedReturn = 0.0454606875f;
			float pwoerPreceding = this.powerMax/2;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp2.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 840.0 [MW] power Preceding / running 75 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_COAL_024(){
			float expectedReturn = 0.018889667f;
			float pwoerPreceding = 840.0f;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp2.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with every combination */
		@Test
		public void calculateEffiencyLossTest_COAL_025(){
			for(float pPreceding = 0.0f ; pPreceding <= powerMax; pPreceding+=1.0f ){
				float ratio = pPreceding/this.powerMax;
				float retVal = this.fp2.calculateEffiencyLoss(ratio);
				assertTrue("Returned Value is: " + retVal + " for the Preceding Power of: " + pPreceding,retVal <= 1.0f && retVal >= 0.0f);
			}
		}
	
// Tests for "closed cycle gas turbine"
	
		/** Test with 0.0 [MW] power Preceding / running 0 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_CCGT_031(){
			float expectedReturn = 0.315584f;
			float pwoerPreceding = 0.0f;			
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp3.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal, retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 1120.0 [MW] power Preceding / running 100 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_CCGT_032(){
			float expectedReturn = 0.0f;
			float pwoerPreceding = 1120.0f	;
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp3.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 560.0 [MW] power Preceding / running 50 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_CCGT_033(){
			float expectedReturn = 0.083360314f;
			float pwoerPreceding = this.powerMax/2;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp3.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 840.0 [MW] power Preceding / running 75 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_CCGT_034(){
			float expectedReturn = 0.034838177f;
			float pwoerPreceding = 840.0f;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp3.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with every combination */
		@Test
		public void calculateEffiencyLossTest_CCGT_035(){
			for(float pPreceding = 0.0f ; pPreceding <= powerMax; pPreceding+=1.0f ){
				float ratio = pPreceding/this.powerMax;
				float retVal = this.fp3.calculateEffiencyLoss(ratio);
				assertTrue("Returned Value is: " + retVal + " for the Preceding Power of: " + pPreceding,retVal <= 1.0f && retVal >= 0.0f);
			}
		}
		
// Tests for "open cycle gas turbine" 
		
		/** Test with 0.0 [MW] power Preceding / running 0 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_OCGT_041(){
			float expectedReturn = 0.407569f;
			float pwoerPreceding = 0.0f;			
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp4.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal, retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 1120.0 [MW] power Preceding / running 100 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_OCGT_042(){
			float expectedReturn = 0.0f;
			float pwoerPreceding = 1120.0f	;
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp4.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 560.0 [MW] power Preceding / running 50 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_OCGT_043(){
			float expectedReturn = 0.09444494f;
			float pwoerPreceding = this.powerMax/2;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp4.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with 840.0 [MW] power Preceding / running 75 % of possible max output */
		@Test
		public void calculateEffiencyLossTest_OCGT_044(){
			float expectedReturn = 0.040951457f;
			float pwoerPreceding = 840.0f;	
			float ratio =pwoerPreceding/this.powerMax;
			float retVal = this.fp4.calculateEffiencyLoss(ratio);
			assertTrue("Returned Value is: " + retVal,retVal <= expectedReturn + 0.001 && retVal >= expectedReturn-0.001);
		}
		
		/** Test with every combination */
		@Test
		public void calculateEffiencyLossTest_OCGT_045(){
			for(float pPreceding = 0.0f ; pPreceding <= powerMax; pPreceding+=1.0f ){
				float ratio = pPreceding/this.powerMax;
				float retVal = this.fp4.calculateEffiencyLoss(ratio);
				assertTrue("Returned Value is: " + retVal + " for the Preceding Power of: " + pPreceding,retVal <= 1.0f && retVal >= 0.0f);
			}
		}
}
