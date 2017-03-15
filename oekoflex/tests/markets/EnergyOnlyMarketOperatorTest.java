package markets;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import agents.MarketOperatorListener;
import bid.BidEOM;
import bid.BidType;


//27.02.2017  The Test are not running correctly due to all the changes done by Thomas Kuenzel
//				TODO Change and write Tests again	

public class EnergyOnlyMarketOperatorTest {
	
	private EnergyOnlyMarketOperator eomOperator;
    private MarketOperatorListener testAgentlistener;
	
	
	@Before
	public void setUp() throws Exception {
		eomOperator = new EnergyOnlyMarketOperator("test", true, "run/test-logs/EnergyOnlyMarketOperatorTest");
        testAgentlistener = new AgentMock();

        RepastTestInitializer.init();
    }
	
	public boolean bidEquals(BidEOM b1 , BidEOM b2){
    	//boolean sameTick = b1.getTick() == b2.getTick() ;
		

    	boolean sameType = b1.getBidType()  == b2.getBidType();
    	boolean samePrice = b1.getPrice() == b2.getPrice();
    	boolean sameAgent = b1.getMarketOperatorListener() == b2.getMarketOperatorListener();
    	
//    	return sameTick && sameType && sameCapPrice && sameWorkPrice && sameAgent;
    	return sameType && samePrice  && sameAgent;
    }
	
	@Test
	public void test_01_toLittleSupplies_01() {
		BidEOM sup1 = new BidEOM(10, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup2 = new BidEOM(20, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup3 = new BidEOM(30, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup4 = new BidEOM(40, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		
		BidEOM sup1Answer = new BidEOM(10, 200, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup2Answer = new BidEOM(20, 200, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup3Answer = new BidEOM(30, 200, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup4Answer = new BidEOM(40, 200, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);

		
		BidEOM dem1 = new BidEOM(200, 200, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem2 = new BidEOM(30, 50, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem3 = new BidEOM(50, 100, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem4 = new BidEOM(50, 20, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		
		
		BidEOM dem1Answer = new BidEOM(100, 200, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem2Answer = new BidEOM(0, 200, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem3Answer = new BidEOM(0, 200, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem4Answer = new BidEOM(0, 200, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		
		eomOperator.addSupply(sup1);
		eomOperator.addSupply(sup2);
		eomOperator.addSupply(sup3);
		eomOperator.addSupply(sup4);
				
		eomOperator.addDemand(dem1);
		eomOperator.addDemand(dem2);
		eomOperator.addDemand(dem3);
		eomOperator.addDemand(dem4);
		
		
		eomOperator.clearMarket();
		
		List<BidEOM> suppliesList = ((AgentMock)testAgentlistener).getSuppliesList();
		List<BidEOM> demandsList  = ((AgentMock)testAgentlistener).getDemandsList();
		
//		System.out.println("Qunatity ; Price ; tick ; BidType");
//		suppliesList.sort(new BidEOM.SupplyComparatorEOM());
//		for(int i = 0; i < suppliesList.size(); i++){
//			BidEOM b = suppliesList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
//		
//		demandsList.sort(new BidEOM.DemandComparatorEOM());
//		for(int i = 0; i < demandsList.size(); i++){
//			BidEOM b = demandsList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
		
		assertTrue("Price expected: 200.0 price cleared: " + eomOperator.getClearedPrice(), 199.0f < eomOperator.getClearedPrice() && 201.0f > eomOperator.getClearedPrice());
		assertTrue(demandsList.size() == 4);
		assertTrue(suppliesList.size() == 4);
		
		assertTrue(bidEquals(suppliesList.get(0),sup4Answer));
		assertTrue(bidEquals(suppliesList.get(1),sup3Answer));
		assertTrue(bidEquals(suppliesList.get(2),sup2Answer));
		assertTrue(bidEquals(suppliesList.get(3),sup1Answer));
		
		assertTrue(bidEquals(demandsList.get(0),dem1Answer));
		assertTrue(bidEquals(demandsList.get(1),dem2Answer));
		assertTrue(bidEquals(demandsList.get(2),dem3Answer));
		assertTrue(bidEquals(demandsList.get(3),dem4Answer));
		
	}
	@Test
	public void test_02_toLittleSupplies_02() {
		BidEOM sup1 = new BidEOM(10, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup2 = new BidEOM(20, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup3 = new BidEOM(30, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup4 = new BidEOM(40, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		
		BidEOM sup1Answer = new BidEOM(10, 111, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup2Answer = new BidEOM(20, 111, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup3Answer = new BidEOM(30, 111, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup4Answer = new BidEOM(40, 111, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);

		
		BidEOM dem1 = new BidEOM(70, 200, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem2 = new BidEOM(25, 150, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem3 = new BidEOM(50, 111, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem4 = new BidEOM(50, 20, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		
		
		BidEOM dem1Answer = new BidEOM(70, 111, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem2Answer = new BidEOM(25, 111, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem3Answer = new BidEOM(5, 111, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem4Answer = new BidEOM(0, 111, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);

		
		eomOperator.addSupply(sup1);
		eomOperator.addSupply(sup2);
		eomOperator.addSupply(sup3);
		eomOperator.addSupply(sup4);
				
		eomOperator.addDemand(dem1);
		eomOperator.addDemand(dem2);
		eomOperator.addDemand(dem3);
		eomOperator.addDemand(dem4);
		
		
		eomOperator.clearMarket();
		
		List<BidEOM> suppliesList = ((AgentMock)testAgentlistener).getSuppliesList();
		List<BidEOM> demandsList  = ((AgentMock)testAgentlistener).getDemandsList();
		
////		System.out.println("Qunatity ; Price ; tick ; BidType");
////		suppliesList.sort(new BidEOM.SupplyComparatorEOM());
////		for(int i = 0; i < suppliesList.size(); i++){
////			BidEOM b = suppliesList.get(i);
////			
////			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
////		}
////		System.out.println("\n");
////		
////		demandsList.sort(new BidEOM.DemandComparatorEOM());
////		for(int i = 0; i < demandsList.size(); i++){
////			BidEOM b = demandsList.get(i);
////			
////			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
////		}
////		System.out.println("\n");
		
		
		
		assertTrue("Price expected: 111.0f price cleared: " + eomOperator.getClearedPrice(), 110.0f < eomOperator.getClearedPrice() && 112.0f > eomOperator.getClearedPrice());
		assertTrue(demandsList.size() == 4);
		assertTrue(suppliesList.size() == 4);
		
		assertTrue(bidEquals(suppliesList.get(0),sup4Answer));
		assertTrue(bidEquals(suppliesList.get(1),sup3Answer));
		assertTrue(bidEquals(suppliesList.get(2),sup2Answer));
		assertTrue(bidEquals(suppliesList.get(3),sup1Answer));
		
		assertTrue(bidEquals(demandsList.get(0),dem1Answer));
		assertTrue(bidEquals(demandsList.get(1),dem2Answer));
		assertTrue(bidEquals(demandsList.get(2),dem3Answer));
		assertTrue(bidEquals(demandsList.get(3),dem4Answer));
		
	}
	
	@Test
	public void test_03_toLittleDemands_01() {
		BidEOM sup1 = new BidEOM(10, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup2 = new BidEOM(20, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup3 = new BidEOM(30, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup4 = new BidEOM(40, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		
		BidEOM sup1Answer = new BidEOM(10, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup2Answer = new BidEOM(20, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup3Answer = new BidEOM(30, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup4Answer = new BidEOM(40, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);

		
		BidEOM dem1 = new BidEOM(70, 200, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem2 = new BidEOM(25, 150, 0, BidType.ENERGY_DEMAND, testAgentlistener);
	
		
		BidEOM dem1Answer = new BidEOM(70, 150, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem2Answer = new BidEOM(25, 150, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);

		
		eomOperator.addSupply(sup1);
		eomOperator.addSupply(sup2);
		eomOperator.addSupply(sup3);
		eomOperator.addSupply(sup4);
				
		eomOperator.addDemand(dem1);
		eomOperator.addDemand(dem2);

		
		
		eomOperator.clearMarket();
		
		List<BidEOM> suppliesList = ((AgentMock)testAgentlistener).getSuppliesList();
		List<BidEOM> demandsList  = ((AgentMock)testAgentlistener).getDemandsList();
		
//		System.out.println("Qunatity ; Price ; tick ; BidType");
//		suppliesList.sort(new BidEOM.SupplyComparatorEOM());
//		for(int i = 0; i < suppliesList.size(); i++){
//			BidEOM b = suppliesList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
//		
//		demandsList.sort(new BidEOM.DemandComparatorEOM());
//		for(int i = 0; i < demandsList.size(); i++){
//			BidEOM b = demandsList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
	
		assertTrue("Price expected: 150.0f price cleared: " + eomOperator.getClearedPrice(), 149.0f < eomOperator.getClearedPrice() && 151.0f > eomOperator.getClearedPrice());
		assertTrue(demandsList.size() == 2);
		assertTrue(suppliesList.size() == 4);
		
		assertTrue(bidEquals(suppliesList.get(0),sup4Answer));
		assertTrue(bidEquals(suppliesList.get(1),sup3Answer));
		assertTrue(bidEquals(suppliesList.get(2),sup2Answer));
		assertTrue(bidEquals(suppliesList.get(3),sup1Answer));
		
		assertTrue(bidEquals(demandsList.get(0),dem1Answer));
		assertTrue(bidEquals(demandsList.get(1),dem2Answer));

		
	}
	
	
	@Test
	public void test_04_toLittleDemands_02() {
		BidEOM sup1 = new BidEOM(10, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup2 = new BidEOM(20, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup3 = new BidEOM(25, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup4 = new BidEOM(40, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		
		BidEOM sup1Answer = new BidEOM(0, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup2Answer = new BidEOM(5, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup3Answer = new BidEOM(25, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);
		BidEOM sup4Answer = new BidEOM(40, 150, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);

		
		BidEOM dem1 = new BidEOM(70, 150, 0, BidType.ENERGY_DEMAND, testAgentlistener);
	
		
		BidEOM dem1Answer = new BidEOM(70, 150, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);

		
		eomOperator.addSupply(sup1);
		eomOperator.addSupply(sup2);
		eomOperator.addSupply(sup3);
		eomOperator.addSupply(sup4);
				
		eomOperator.addDemand(dem1);

		
		
		eomOperator.clearMarket();
		
		List<BidEOM> suppliesList = ((AgentMock)testAgentlistener).getSuppliesList();
		List<BidEOM> demandsList  = ((AgentMock)testAgentlistener).getDemandsList();
		
//		System.out.println("Qunatity ; Price ; tick ; BidType");
//		suppliesList.sort(new BidEOM.SupplyComparatorEOM());
//		for(int i = 0; i < suppliesList.size(); i++){
//			BidEOM b = suppliesList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
//		
//		demandsList.sort(new BidEOM.DemandComparatorEOM());
//		for(int i = 0; i < demandsList.size(); i++){
//			BidEOM b = demandsList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
	
		assertTrue("Price expected: 150.0f price cleared: " + eomOperator.getClearedPrice(), 149.0f < eomOperator.getClearedPrice() && 151.0f > eomOperator.getClearedPrice());
		assertTrue(demandsList.size() == 1);
		assertTrue(suppliesList.size() == 4);
		
		assertTrue(bidEquals(suppliesList.get(0),sup4Answer));
		assertTrue(bidEquals(suppliesList.get(1),sup3Answer));
		assertTrue(bidEquals(suppliesList.get(2),sup2Answer));
		assertTrue(bidEquals(suppliesList.get(3),sup1Answer));
		
		assertTrue(bidEquals(demandsList.get(0),dem1Answer));

		
	}
	
	
	// This test tests case b of the document for the EOM
	@Test
	public void test_05_supplyDemandPriceCrosses() {
		BidEOM sup1 = new BidEOM(10, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup2 = new BidEOM(20, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup3 = new BidEOM(25, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		BidEOM sup4 = new BidEOM(45, -1, 0, BidType.ENERGY_SUPPLY, testAgentlistener);
		
		BidEOM sup4Answer = new BidEOM(35, -1, 0, BidType.ENERGY_SUPPLY_CONFIRMED, testAgentlistener);

		
		BidEOM dem1 = new BidEOM(10,  200, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem2 = new BidEOM(25,  150, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem3 = new BidEOM(50, -111, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		BidEOM dem4 = new BidEOM(50, -100, 0, BidType.ENERGY_DEMAND, testAgentlistener);
		
		
		BidEOM dem1Answer = new BidEOM(10, -1, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem2Answer = new BidEOM(25, -1, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem3Answer = new BidEOM(0, -1, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);
		BidEOM dem4Answer = new BidEOM(0, -1, 0, BidType.ENERGY_DEMAND_CONFIRMED, testAgentlistener);

		
		eomOperator.addSupply(sup1);
		eomOperator.addSupply(sup2);
		eomOperator.addSupply(sup3);
		eomOperator.addSupply(sup4);
				
		eomOperator.addDemand(dem1);
		eomOperator.addDemand(dem2);
		eomOperator.addDemand(dem3);
		eomOperator.addDemand(dem4);
		
		
		eomOperator.clearMarket();
		
		List<BidEOM> suppliesList = ((AgentMock)testAgentlistener).getSuppliesList();
		List<BidEOM> demandsList  = ((AgentMock)testAgentlistener).getDemandsList();
		
//		System.out.println("Qunatity ; Price ; tick ; BidType");
//		suppliesList.sort(new BidEOM.SupplyComparatorEOM());
//		for(int i = 0; i < suppliesList.size(); i++){
//			BidEOM b = suppliesList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
//		
//		demandsList.sort(new BidEOM.DemandComparatorEOM());
//		for(int i = 0; i < demandsList.size(); i++){
//			BidEOM b = demandsList.get(i);
//			
//			System.out.println(b.getQuantity() +"     ; "+ b.getPrice() +" ; "+b.getTick() +"   ; "+b.getBidType());
//		}
//		System.out.println("\n");
		
		
		
		assertTrue("Price expected: 150.0f price cleared: " + eomOperator.getClearedPrice(), 149.0f < eomOperator.getClearedPrice() && 151.0f > eomOperator.getClearedPrice());
		assertTrue(demandsList.size() == 4);
		assertTrue(suppliesList.size() == 4);
		
		assertTrue(""+ suppliesList.get(0).getPrice(), bidEquals(suppliesList.get(0),sup4Answer));

		
		assertTrue(bidEquals(demandsList.get(0),dem1Answer));
		assertTrue(bidEquals(demandsList.get(1),dem2Answer));
		assertTrue(bidEquals(demandsList.get(2),dem3Answer));
		assertTrue(bidEquals(demandsList.get(3),dem4Answer));

	}
	
	
	
	

}
