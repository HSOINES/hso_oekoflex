package agents;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import agents.Storage.Intervals;
import markets.BPMTestOP2;
import markets.BalancingMarketOperator;
import markets.EOMTestOP;
import markets.EnergyOnlyMarketOperator;
import pfc.PriceForwardCurve;
import structures.TupelMT;
import structures.TupelMTS;
import structures.TupelMTS.Status;
/**
 * 		From the CSV this storage as an example/test storage
 * 
 *		name					| description	| ChargePowerMax [MW]	| DischargePowerMax [MW]	| chargingEfficiency [%]	| dischargingEfficiency [%]	| capacityMin [MWh]	| capacityMax [MWh]	| variableCostsCharging [EUR/MWh]	| variableCostsDischarging [EUR/MWh]
 *		--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 *		Tanzmühle - Rabenleite	| PSPP			| 25					| 35						| 0,81						| 0,88						| 0					| 404				| 0,28								| 0,28
**/


//27.02.2017  The Test are not running correctly due to all the changes done by Thomas Kuenzel
//TODO Change and write Tests again	

public class StorageTest {
	String name = "Tanzmühle - Rabenleite";
	String description = "PSPP";
	float chargePowerMax = 25;
	float dischargePowerMax = 35;
	float chargingEfficiency = 0.81f ;
	float dischargingEfficiency = 0.88f;
	float capacityMin = 0;
	float capacityMax = 404;
	float variableCostsCharging = 0.28f;
	float variableCostsDischarging = 0.28f;;
	
	PriceForwardCurve pfc;
	EnergyOnlyMarketOperator eom;
	BalancingMarketOperator bpm;
	
	Storage tStorage;
	
	String priceForwardOutDir = "run-config/s1/";
	File priceForwardFile;

	
	@Before
	public void setUp() throws Exception {
		
		eom = new EOMTestOP( "EOMTestOP" , true, "run/test-logs/EnergyOnlyMarketOperatorTest");
		bpm = new BPMTestOP2("BPMTestOP2", true, "run/test-logs/BAlancingPowerMarketOperatorTest");
		
		priceForwardFile = new File(priceForwardOutDir, "price-forward.csv");
		
		pfc = new PriceForwardCurve(priceForwardFile);
		pfc.readData();
		
		tStorage = new Storage(name, description, chargePowerMax, dischargePowerMax, chargingEfficiency, dischargingEfficiency, capacityMin, capacityMax, variableCostsCharging, variableCostsDischarging, pfc, eom, bpm);
		
		tStorage.setStateOfCharge(200.0f);
	}

	@Test
	public void testSetStateOfCharge() {
		
		assertTrue(tStorage.setStateOfCharge(capacityMax/2));
		assertTrue(tStorage.setStateOfCharge(capacityMin));
		assertTrue(tStorage.setStateOfCharge(capacityMax));
		assertFalse(tStorage.setStateOfCharge(-1*capacityMax));
		
	}

	@Test
	public void testCalculateIntervals_normal() {
		Intervals inter = tStorage.calculateIntervals(200,capacityMin,capacityMax,dischargePowerMax, chargePowerMax);
		assertEquals("Expected total number: 54 vs " + inter.total() + " returned",54,inter.total());
		assertEquals("Expected chargeIntervals number: 32 vs " + inter.chargeIntervals + " returned",32, inter.chargeIntervals);
		assertEquals("Expected dischargeIntervals number: 22 vs " + inter.dischargeIntervals + " returned",22, inter.dischargeIntervals);
	}
	
	@Test
	public void testCalculateIntervals_socZero_capMinZero() {
		// Zero CapacityMin
		// Soc is 0 -> Only charge Intervals should appear
		Intervals inter = tStorage.calculateIntervals(0,capacityMin,capacityMax,dischargePowerMax, chargePowerMax);
		assertEquals("Expected total number: 64 vs " + inter.total() + " returned",64,inter.total());
		assertEquals("Expected chargeIntervals number: 64 vs " + inter.chargeIntervals + " returned",64, inter.chargeIntervals);
		assertEquals("Expected dischargeIntervals number: 0 vs " + inter.dischargeIntervals + " returned",0, inter.dischargeIntervals);
	}
	
	@Test
	public void testCalculateIntervals_socZero_capMinPos() {
		// positive CapacityMin
		// Soc is 0 -> Only charge Intervals should appear
		Intervals inter = tStorage.calculateIntervals(0,10,capacityMax,dischargePowerMax, chargePowerMax);
		assertEquals("Expected total number: 64 vs " + inter.total() + " returned",64,inter.total());
		assertEquals("Expected chargeIntervals number: 64 vs " + inter.chargeIntervals + " returned",64, inter.chargeIntervals);
		assertEquals("Expected dischargeIntervals number: 0 vs " + inter.dischargeIntervals + " returned",0, inter.dischargeIntervals);
	}
	

//	@Test
//	public void testNotifyClearingDoneDateMarketBidEOMFloatFloat() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMakeBidBalancingMarket() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMakeBidBalancingMarketLong() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testSpreadBPM_10_20() {
		float marketPriceLow = 10;
		float marketPricehigh = 20;
		boolean spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertTrue(spread );
		
		
	}
	@Test
	public void testSpreadBPM_minus20_minus10() {
		
		float marketPriceLow =  -20;
		float marketPricehigh = -10;
		boolean spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertTrue(spread );
		
		marketPriceLow =   20;
		marketPricehigh =  20;
		spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertTrue(spread);
		
	}
	
	@Test
	public void testSpreadBPM_20_20() {
		
		float marketPriceLow =  20;
		float marketPricehigh = 20;
		boolean spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertTrue(spread );
		
		
	}
	
	@Test
	public void testSpreadBPM_30_20() {
		
		float marketPriceLow =  30;
		float marketPricehigh = 20;
		boolean spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertFalse(spread );
		
		
	}
	
	@Test
	public void testSpreadBPM_minus10_minus20() {
		
		float marketPriceLow =  -10;
		float marketPricehigh = -20;
		boolean spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertFalse(spread );
		
		
	}
	
	@Test
	public void testSpreadBPM_0_minus10() {
		
		float marketPriceLow =   0;
		float marketPricehigh = -10;
		boolean spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertFalse(spread );
		
		marketPriceLow =   -10;
		marketPricehigh =  0;
		spread = tStorage.calculateSpread( marketPricehigh, marketPriceLow);
		assertTrue(spread);
		
	}
	
	@Test
	public void testCalculateRevenue() {
		int n = 10;
		List<TupelMT> chargePrices = new ArrayList<TupelMT>();
		List<TupelMT> dischargePrices  = new ArrayList<TupelMT>();
		
		for(int i =0; i<10; i++){
			chargePrices.add(new TupelMT(0 , -1 * (float) 5));	// low prices of ticks at which we charge cheap
			dischargePrices.add(new TupelMT(0 ,  (float) 5));	// high prices of ticks at which we discharge to mae money
		}


		float revenue = tStorage.calculateRevenue( n,  chargePrices,  dischargePrices);
		float expected = 100.0f * 0.25f;	// Revenue summed up times dt as [h]
		assertTrue("Expected " + expected + " vs " + revenue +" returned", (revenue >= expected-0.001) && (revenue <= expected+0.001));
	}
	
	@Test
	public void testCalculateRevenue_all_neg() {
		int n = 10;
		List<TupelMT> chargePrices = new ArrayList<TupelMT>();
		List<TupelMT> dischargePrices  = new ArrayList<TupelMT>();
		
		for(int i =0; i<10; i++){
			chargePrices.add(new TupelMT(0 , -1 * (float) 10));	// low prices of ticks at which we charge cheap
			dischargePrices.add(new TupelMT(0 , -1 * (float) 5));	// high prices of ticks at which we discharge to mae money
		}


		float revenue = tStorage.calculateRevenue( n,  chargePrices,  dischargePrices);
		float expected = 50.0f * 0.25f;	// Revenue summed up times dt as [h]
		assertTrue("Expected " + expected + " vs " + revenue +" returned", (revenue >= expected-0.001) && (revenue <= expected+0.001));
	}
	
	@Test
	public void testCalculateRevenue_all_pos() {
		int n = 10;
		List<TupelMT> chargePrices = new ArrayList<TupelMT>();
		List<TupelMT> dischargePrices  = new ArrayList<TupelMT>();
		
		for(int i =0; i<10; i++){
			chargePrices.add(new TupelMT(0 ,  (float) 0));	// low prices of ticks at which we charge cheap
			dischargePrices.add(new TupelMT(0 ,  (float) 10));	// high prices of ticks at which we discharge to mae money
		}


		float revenue = tStorage.calculateRevenue( n,  chargePrices,  dischargePrices);
		float expected = 100.0f * 0.25f;	// Revenue summed up times dt as [h]
		assertTrue("Expected " + expected + " vs " + revenue +" returned", (revenue >= expected-0.001) && (revenue <= expected+0.001));
	}
	
	@Test
	public void testCalculateRevenue_minus_zero() {
		int n = 10;
		List<TupelMT> chargePrices = new ArrayList<TupelMT>();
		List<TupelMT> dischargePrices  = new ArrayList<TupelMT>();
		
		for(int i =0; i<10; i++){
			chargePrices.add(new TupelMT(0 ,  (float) -5));	// low prices of ticks at which we charge cheap
			dischargePrices.add(new TupelMT(0 ,  (float) 0));	// high prices of ticks at which we discharge to mae money
		}


		float revenue = tStorage.calculateRevenue( n,  chargePrices,  dischargePrices);
		float expected = 50.0f * 0.25f;	// Revenue summed up times dt as [h]
		assertTrue("Expected " + expected + " vs " + revenue +" returned", (revenue >= expected-0.001) && (revenue <= expected+0.001));
	}
	
	@Test 
	public void sortTupelMTS_Test(){
		List<TupelMTS> low = new ArrayList<TupelMTS>();
		
		
		TupelMTS lowPos2 = new TupelMTS(4,25);
		TupelMTS lowPos3 = new TupelMTS(5,25);
		TupelMTS lowPos1 = new TupelMTS(14,30);
		TupelMTS lowPos4 = new TupelMTS(15,25);
		
		low.add(lowPos2);
		low.add(lowPos3);
		low.add(lowPos1);
		low.add(lowPos4);
		
		low.sort(new TupelMTS.descPriceAscTickComparator());
		
		// Recheck if list is sorted according to logic
		assertEquals(low.get(0),lowPos1 );	
		assertEquals(low.get(1),lowPos2 );
		assertEquals(low.get(2),lowPos3 );
		assertEquals(low.get(3),lowPos4 );
		
		
		List<TupelMTS> high = new ArrayList<TupelMTS>();
		
		TupelMTS highPos9  = new TupelMTS( 0,50);
		TupelMTS highPos7  = new TupelMTS( 1,45);
		TupelMTS highPos3  = new TupelMTS( 2,35);
		TupelMTS highPos1  = new TupelMTS( 3,30);
		TupelMTS highPos2  = new TupelMTS( 6,30);
		TupelMTS highPos4  = new TupelMTS( 7,35);
		TupelMTS highPos5  = new TupelMTS( 8,35);
		TupelMTS highPos6  = new TupelMTS( 9,40);
		TupelMTS highPos10 = new TupelMTS(10,50);
		TupelMTS highPos12 = new TupelMTS(11,55);
		TupelMTS highPos11 = new TupelMTS(12,50);
		TupelMTS highPos8  = new TupelMTS(13,45);
		
		high.add(highPos9);
		high.add(highPos7);
		high.add(highPos3);
		high.add(highPos1);
		high.add(highPos2);
		high.add(highPos4);
		high.add(highPos5);
		high.add(highPos6);
		high.add(highPos10);
		high.add(highPos12);
		high.add(highPos11);
		high.add(highPos8);
		high.sort(new TupelMTS.ascPriceAscTickComparator());		
		
		// Recheck if list is sorted according to logic
		assertEquals(high.get(0), highPos1  );
		assertEquals(high.get(1), highPos2  );
		assertEquals(high.get(2), highPos3  );
		assertEquals(high.get(3), highPos4  );
		assertEquals(high.get(4), highPos5  );
		assertEquals(high.get(5), highPos6  );
		assertEquals(high.get(6), highPos7  );
		assertEquals(high.get(7), highPos8  );
		assertEquals(high.get(8), highPos9  );
		assertEquals(high.get(9), highPos10 );
		assertEquals(high.get(10),highPos11 );
		assertEquals(high.get(11),highPos12 );
		
		
		List<TupelMTS> combinedTrade = tStorage.spreadBPM(low, high);
/**	  
 *    [ 30.0 , 3 discharge ,  30.0 , 14 charge ,  
 *		30.0 , 6 discharge ,  25.0 , 4 charge ,  
 *		35.0 , 2 discharge ,  25.0 , 5 charge ,  
 *		35.0 , 7 discharge ,  25.0 , 15 charge ,  
 *		
 *		35.0 , 8 discharge ,  
 *      40.0 , 9 discharge ,  
 *		45.0 , 1 discharge ,  
 *      45.0 , 13 discharge ,  
 *		50.0 , 0 discharge ,  
 *      50.0 , 10 discharge ,  
 *		50.0 , 12 discharge ,  
 *      55.0 , 11 discharge ]
**/
		
		assertTrue(  combinedTrade.get(0).price <=30.01f && combinedTrade.get(0).price >= 29.99f);
		assertEquals(combinedTrade.get(0).tick,3 );
		assertEquals(combinedTrade.get(0).status,Status.DISCHARGE );
		assertTrue(  combinedTrade.get(1).price <=30.01f && combinedTrade.get(1).price >= 29.99f);
		assertEquals(combinedTrade.get(1).tick,14 );
		assertEquals(combinedTrade.get(1).status,Status.CHARGE );
		
		assertTrue(  combinedTrade.get(2).price <=30.01f && combinedTrade.get(0).price >= 29.99f);
		assertEquals(combinedTrade.get(2).tick,6 );
		assertEquals(combinedTrade.get(2).status,Status.DISCHARGE );
		assertTrue(  combinedTrade.get(3).price <=25.01f && combinedTrade.get(1).price >= 24.99f);
		assertEquals(combinedTrade.get(3).tick,4 );
		assertEquals(combinedTrade.get(3).status,Status.CHARGE );
		
		//TupelMTS t = combinedTrade.get(4);
		
		assertTrue(  combinedTrade.get(4).price <=35.01f && combinedTrade.get(4).price >= 34.99f);
		assertEquals(combinedTrade.get(4).tick,2 );
		assertEquals(combinedTrade.get(4).status,Status.DISCHARGE );
		assertTrue(  combinedTrade.get(5).price <=25.01f && combinedTrade.get(5).price >= 24.99f);
		assertEquals(combinedTrade.get(5).tick,5 );
		assertEquals(combinedTrade.get(5).status,Status.CHARGE );
		
		
	}

	@Test
	public void testMakeBidEOMLong() {
		tStorage.setStateOfCharge(202.0f); 	// 202 = 50% SoC
		tStorage.makeBidEOM(0);
	}
//
//	@Test
//	public void testCheckPositiveSpread() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetCurrentPower() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetCurrentEnergy() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNotifyClearingDoneBidBPM() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNotifyClearingDoneBidEOM() {
//		fail("Not yet implemented");
//	}

}
/** Part of the used PFC
tick	price
0	-63,781
1	21,871
2	23,128
3	23,89
4	23,128
5	23,525
6	23,817
7	23,467
8	21,97
9	22,745
10	23,017
11	23,017
12	21,627
13	21,97
14	21,97
15	22,02
16	21,97
17	21,97
18	21,97
19	21,97
20	21,627
21	21,871
22	21,871
23	21,871
24	21,296
25	21,627
26	21,627
27	21,627
28	21,627
29	21,627
30	21,627
31	21,627
32	21,627
33	21,627
34	21,627
35	21,627
36	21,97
37	21,627
38	21,871
39	21,871
40	23,467
41	21,97
42	21,871
43	21,871
44	23,017
45	21,97
46	21,627
47	21,627
48	22,02
49	21,97
50	21,97
51	21,627
52	21,871
53	21,97
54	21,97
55	22,02
56	22,02
57	23,467
58	23,128
59	23,525
60	24,619
61	24,426
62	24,426
63	24,489
64	34,442
65	26,251
66	24,619
67	25,069
68	37,307
69	35,245
70	34,442
71	34,875
72	35,245
73	34,875
74	34,442
75	34,442
76	28,534
77	26,251
78	26,251
79	26,251
80	23,467
81	24,057
82	24,489
83	24,426
84	23,525
85	23,89
86	23,89
87	23,467
88	24,426
89	23,936
90	23,89
91	23,936
92	21,627
93	21,97
94	23,017
95	23,017
96	-43,625
97	21,296
98	21,627
99	21,579
100	2,922

*/
