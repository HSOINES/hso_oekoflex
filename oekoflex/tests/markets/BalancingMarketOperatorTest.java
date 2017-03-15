package markets;

import static org.junit.Assert.*;
//import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;

import agents.MarketOperatorListener;
import bid.BidBPM;
import bid.BidType;

import java.util.List;



// 27.02.2017  The Test are not running correctly due to all the changes done by Thomas Kuenzel
public class BalancingMarketOperatorTest {

	private BPMTestOP bpmOperator;
    private MarketOperatorListener testAgentlistener;
    
    @Before
    public void setUp() throws Exception {
        bpmOperator = new BPMTestOP("test", true, "run/test-logs/BalancingMarketOperatorTest");
        testAgentlistener = new AgentMock();

        RepastTestInitializer.init();
    }
    
    // Pseudo equals function to comapre bids
    public boolean bidEquals(BidBPM b1 , BidBPM b2){
    	
    	//boolean sameTick = b1.getTick() == b2.getTick() ;
    	boolean sameType = b1.getBidType() == b2.getBidType();
    	boolean sameCapPrice = b1.getPriceCapacity() == b2.getPriceCapacity();
    	boolean sameWorkPrice = b1.getPriceWork() == b2.getPriceWork();
    	boolean sameAgent = b1.getMarketOperatorListener() == b2.getMarketOperatorListener();
    	
//    	return sameTick && sameType && sameCapPrice && sameWorkPrice && sameAgent;
    	return sameType && sameCapPrice && sameWorkPrice && sameAgent;
    }
    
	@Test
	public void test_01_BalancingMarketOperator() {
		assertEquals("test",bpmOperator.getName());
		// Logging Activated könnte man noch testen
		// Logging Directory könnte man irgendwie noch testen
	}

    
    @Test
	public void test_02_AddPositiveSupply() {
    	BidBPM bp1 = new BidBPM(100, 10 ,10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	bpmOperator.addPositiveSupply(bp1);
    	List<BidBPM> listPos = bpmOperator.getPositiveSupplies();
    	
    	assertEquals(true, bidEquals(bp1, listPos.get(0)));
	}

	@Test
	public void test_03_AddNegativeSupply() {
		BidBPM bn1 = new BidBPM(100, 10 ,10,0,BidType.POWER_NEGATIVE, testAgentlistener);
    	bpmOperator.addNegativeSupply(bn1);
    	List<BidBPM> listNeg = bpmOperator.getNegativeSupplies();
    	assertEquals(true, bidEquals(bn1, listNeg.get(0)));
	}
    
    @Test
    public void test_04_ListAddBeforeMarketClearing() throws Exception {
    	BidBPM bn1 = new BidBPM(100, 10 ,10 , 0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn2 = new BidBPM(100, 20 ,20 , 0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn3 = new BidBPM(90, 20  ,20 , 0, BidType.POWER_NEGATIVE, testAgentlistener);
    	
    	BidBPM bp1 = new BidBPM(100, 10 ,10,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp2 = new BidBPM(100, 20 ,20,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp3 = new BidBPM(90, 20 ,20,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	
    	bpmOperator.addNegativeSupply(bn1);
    	bpmOperator.addNegativeSupply(bn2);
    	bpmOperator.addNegativeSupply(bn3);
    	
    	bpmOperator.addPositiveSupply(bp1);
    	bpmOperator.addPositiveSupply(bp2);
    	bpmOperator.addPositiveSupply(bp3);
    	
    	List<BidBPM> listNeg = bpmOperator.getNegativeSupplies();
    	List<BidBPM> listPos = bpmOperator.getPositiveSupplies();
    	
    	assertEquals(true, bidEquals(bn1, listNeg.get(0)));
    	assertEquals(true, bidEquals(bn2, listNeg.get(1)));
    	assertEquals(true, bidEquals(bn3, listNeg.get(2)));
    	
    	assertEquals(true, bidEquals(bp1, listPos.get(0)));
    	assertEquals(true, bidEquals(bp2, listPos.get(1)));
    	assertEquals(true, bidEquals(bp3, listPos.get(2)));
    }
	
    @Test
    public void test_05_ListAfterMarketClearing_01() throws Exception {
    	BidBPM bn1 = new BidBPM(100, 10 ,10,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn2 = new BidBPM(10, 20 ,20,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn3 = new BidBPM(90, 20 ,20,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	
    	BidBPM bp1 = new BidBPM(100, 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp2 = new BidBPM(50 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp3 = new BidBPM(30 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp4 = new BidBPM(20 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp5 = new BidBPM(90 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	
    	BidBPM bn1_answer = new BidBPM(100, 10 ,10 ,0, BidType.POWER_NEGATIVE_CONFIRMED, testAgentlistener);
    	BidBPM bn2_answer = new BidBPM(10, 20  ,20 ,0, BidType.POWER_NEGATIVE_CONFIRMED, testAgentlistener);
    	BidBPM bn3_answer = new BidBPM(90, 20  ,20 ,0, BidType.POWER_NEGATIVE_CONFIRMED, testAgentlistener);
    	
    	BidBPM bp1_answer = new BidBPM(100, 10 , 10 ,0 ,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp2_answer = new BidBPM(50 , 10 , 10 ,0 ,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp3_answer = new BidBPM(30 , 10 , 10 ,0 ,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp4_answer = new BidBPM(20 , 10 , 10 ,0 ,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp5_answer = new BidBPM(90 , 10 , 10 ,0 ,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	
    	bpmOperator.addNegativeSupply(bn1);
    	bpmOperator.addNegativeSupply(bn2);
    	bpmOperator.addNegativeSupply(bn3);
    	
    	bpmOperator.addPositiveSupply(bp1);
    	bpmOperator.addPositiveSupply(bp2);
    	bpmOperator.addPositiveSupply(bp3);
    	bpmOperator.addPositiveSupply(bp4);
    	bpmOperator.addPositiveSupply(bp5);
    	
    	bpmOperator.clearMarketCapacityPrice();
    	
    	List<BidBPM> listNeg = bpmOperator.getNegativeSupplies();
    	List<BidBPM> listPos = bpmOperator.getPositiveSupplies();
  
    	assertEquals(true, bidEquals(bn1_answer, listNeg.get(0)));
    	assertEquals(true, bidEquals(bn3_answer, listNeg.get(1)));
    	assertEquals(true, bidEquals(bn2_answer, listNeg.get(2)));
    	
    	
    	assertEquals(true, bidEquals(bp1_answer, listPos.get(0)));
    	assertEquals(true, bidEquals(bp5_answer, listPos.get(1)));
    	assertEquals(true, bidEquals(bp2_answer, listPos.get(2)));
    	assertEquals(true, bidEquals(bp3_answer, listPos.get(3)));
    	assertEquals(true, bidEquals(bp4_answer, listPos.get(4)));
    }
	
    @Test
    public void test_06_ListAfterMarketClearing_02() throws Exception {
    	
    	BidBPM bp1 = new BidBPM(10 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp2 = new BidBPM(20 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp3 = new BidBPM(30 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp4 = new BidBPM(40 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	
    	BidBPM bp11 = new BidBPM(10 , 15 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp21 = new BidBPM(20 , 15 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp31 = new BidBPM(30 , 15 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp41 = new BidBPM(40 , 15 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	
    	BidBPM bp1_answer = new BidBPM(10 , 10 , 10 , 0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp2_answer = new BidBPM(20 , 10 , 10 , 0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp3_answer = new BidBPM(30 , 10 , 10 , 0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp4_answer = new BidBPM(40 , 10 , 10 , 0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	
    	BidBPM bp11_answer = new BidBPM(10 , 15 , 10 ,0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp21_answer = new BidBPM(20 , 15 , 10 ,0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp31_answer = new BidBPM(30 , 15 , 10 ,0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp41_answer = new BidBPM(40 , 15 , 10 ,0, BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	
    	
    	bpmOperator.addPositiveSupply(bp11);
    	bpmOperator.addPositiveSupply(bp21);
    	bpmOperator.addPositiveSupply(bp31);
    	bpmOperator.addPositiveSupply(bp41);
    	
    	bpmOperator.addPositiveSupply(bp1);
    	bpmOperator.addPositiveSupply(bp2);
    	bpmOperator.addPositiveSupply(bp3);
    	bpmOperator.addPositiveSupply(bp4);
    	
    	bpmOperator.clearMarketCapacityPrice();

    	
    	List<BidBPM> listPos = bpmOperator.getPositiveSupplies();
    	
    	assertEquals(true, bidEquals(bp4_answer, listPos.get(0)));
    	assertEquals(true, bidEquals(bp3_answer, listPos.get(1)));
    	assertEquals(true, bidEquals(bp2_answer, listPos.get(2)));
    	assertEquals(true, bidEquals(bp1_answer, listPos.get(3)));

    	assertEquals(true, bidEquals(bp41_answer, listPos.get(4)));
    	assertEquals(true, bidEquals(bp31_answer, listPos.get(5)));
    	assertEquals(true, bidEquals(bp21_answer, listPos.get(6)));
    	assertEquals(true, bidEquals(bp11_answer, listPos.get(7)));
    	
    	
    }
	


	@Test
	public void test_07_ClearLists() {
		BidBPM bp1 = new BidBPM(10 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp2 = new BidBPM(20 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp3 = new BidBPM(30 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp4 = new BidBPM(40 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	
    	BidBPM bp11 = new BidBPM(10 , 15 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp21 = new BidBPM(20 , 15 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp31 = new BidBPM(30 , 15 , 10 , 0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp41 = new BidBPM(40 , 15 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	
    	BidBPM bp1_answer = new BidBPM(10 , 10 , 10 ,0 ,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp2_answer = new BidBPM(20 , 10 , 10 ,0 ,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp3_answer = new BidBPM(30 , 10 , 10 ,0,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp4_answer = new BidBPM(40 , 10 , 10 ,0,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	
    	BidBPM bp11_answer = new BidBPM(10 , 15 , 10 ,0,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp21_answer = new BidBPM(20 , 15 , 10 ,0,BidType.POWER_POSITIVE_CONFIRMED , testAgentlistener);
    	BidBPM bp31_answer = new BidBPM(30 , 15 , 10 ,0,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);
    	BidBPM bp41_answer = new BidBPM(40 , 15 , 10 ,0,BidType.POWER_POSITIVE_CONFIRMED, testAgentlistener);

    	
    	bpmOperator.addPositiveSupply(bp11);
    	bpmOperator.addPositiveSupply(bp21);
    	bpmOperator.addPositiveSupply(bp31);
    	bpmOperator.addPositiveSupply(bp41);
    	
    	bpmOperator.addPositiveSupply(bp1);
    	bpmOperator.addPositiveSupply(bp2);
    	bpmOperator.addPositiveSupply(bp3);
    	bpmOperator.addPositiveSupply(bp4);
    	
    	bpmOperator.clearMarketCapacityPrice();
    	
    	List<BidBPM> listPos = bpmOperator.getPositiveSupplies();

    	assertEquals(true, bidEquals(bp4_answer, listPos.get(0)));
    	assertEquals(true, bidEquals(bp3_answer, listPos.get(1)));
    	assertEquals(true, bidEquals(bp2_answer, listPos.get(2)));
    	assertEquals(true, bidEquals(bp1_answer, listPos.get(3)));

    	assertEquals(true, bidEquals(bp41_answer, listPos.get(4)));
    	assertEquals(true, bidEquals(bp31_answer, listPos.get(5)));
    	assertEquals(true, bidEquals(bp21_answer, listPos.get(6)));
    	assertEquals(true, bidEquals(bp11_answer, listPos.get(7)));
    	
    	
    	BidBPM bn1 = new BidBPM(10 , 10 , 10 ,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn2 = new BidBPM(20 , 10 , 10 ,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn3 = new BidBPM(30 , 10 , 10 ,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn4 = new BidBPM(40 , 10 , 10 ,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	
    	BidBPM bn11 = new BidBPM(10 , 10 , 15 ,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn21 = new BidBPM(20 , 10 , 15 ,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn31 = new BidBPM(30 , 10 , 15 ,0, BidType.POWER_NEGATIVE, testAgentlistener);
    	BidBPM bn41 = new BidBPM(40 , 10 , 15 ,0, BidType.POWER_NEGATIVE, testAgentlistener);

    	
    	bpmOperator.addNegativeSupply(bn11);
    	bpmOperator.addNegativeSupply(bn21);
    	bpmOperator.addNegativeSupply(bn31);
    	bpmOperator.addNegativeSupply(bn41);
    	
    	bpmOperator.addNegativeSupply(bn1);
    	bpmOperator.addNegativeSupply(bn2);
    	bpmOperator.addNegativeSupply(bn3);
    	bpmOperator.addNegativeSupply(bn4);
    	
    	bpmOperator.clearMarketEnergyPrice();;

    	
    	List<BidBPM> listNeg = bpmOperator.getNegativeSupplies();
    	
    	assertEquals(bn4, listNeg.get(0));
    	assertEquals(bn3, listNeg.get(1));
    	assertEquals(bn2, listNeg.get(2));
    	assertEquals(bn1, listNeg.get(3));

    	assertEquals(bn41, listNeg.get(4));
    	assertEquals(bn31, listNeg.get(5));
    	assertEquals(bn21, listNeg.get(6));
    	assertEquals(bn11, listNeg.get(7));
    	    	
    	listNeg = bpmOperator.getNegativeSupplies();
    	listPos = bpmOperator.getPositiveSupplies();
    	
    	assertEquals(true, listNeg.isEmpty());
    	assertEquals(true, listPos.isEmpty());
	}
	
	
	// nenne marinal bid
	@Test
	public void test_08_ClearListsPositive_with_partially_fullfilled_Bid() {
		BidBPM bp1 = new BidBPM(100 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp2 = new BidBPM(180  ,20 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp3 = new BidBPM(100 , 30 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp4 = new BidBPM(100 , 30 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	bpmOperator.addPositiveSupply(bp3);
    	bpmOperator.addPositiveSupply(bp2);
    	bpmOperator.addPositiveSupply(bp1);
    	bpmOperator.addPositiveSupply(bp4);
    	
    	bpmOperator.clearMarketCapacityPrice();
    	
    	List<BidBPM> listPos = bpmOperator.getPositiveSupplies();
//    	for (int i = 0; i < listPos.size(); i++) {
//    		BidBPM bid = listPos.get(i);
//    		System.out.println( bid.getQuantity()+ " " + bid.getPriceCapacity() +" "+ bid.getPriceWork());
//			
//		}
    	assertEquals(3, listPos.size());
    	
    	assertEquals(true, listPos.get(0).getQuantity() < 100.1f && listPos.get(0).getQuantity() > 99.9f  );
    	assertEquals(true, listPos.get(1).getQuantity() < 180.1f && listPos.get(1).getQuantity() > 179.9f );
    	assertEquals(true, listPos.get(2).getQuantity() <  20.1f && listPos.get(2).getQuantity() > 19.9f  );
	}
	
	@Test
	public void test_09_ClearListsNegative_with_partially_fullfilled_Bid() {
		BidBPM bp1 = new BidBPM(100 , 10 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp2 = new BidBPM(80  , 20 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	BidBPM bp3 = new BidBPM(100 , 30 , 10 ,0 ,BidType.POWER_POSITIVE, testAgentlistener);
    	
    	bpmOperator.addNegativeSupply(bp3);
    	bpmOperator.addNegativeSupply(bp2);
    	bpmOperator.addNegativeSupply(bp1);
    	
    	bpmOperator.clearMarketCapacityPrice();
    	
    	List<BidBPM> listNeg = bpmOperator.getNegativeSupplies();
    	
    	assertEquals(3, listNeg.size());
    	
    	assertEquals(true, listNeg.get(0).getQuantity() < 100.1f && listNeg.get(0).getQuantity() > 99.9f );
    	assertEquals(true, listNeg.get(1).getQuantity() < 80.1f && listNeg.get(1).getQuantity() > 79.9f );
    	assertEquals(true, listNeg.get(2).getQuantity() < 20.1f && listNeg.get(2).getQuantity() > 19.9f );
	}

}
