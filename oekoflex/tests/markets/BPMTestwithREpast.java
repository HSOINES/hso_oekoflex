package markets;

import org.junit.Before;


//27.02.2017  The Test are not running correctly due to all the changes done by Thomas Kuenzel
// 				TODO Change and write Tests again	
import org.junit.Test;

import agents.MarketOperatorListener;
import bid.BidBPM;
import bid.BidType;
import util.TimeUtil;

import java.util.List;
public class BPMTestwithREpast {

	
	private BPMTestOP operator;
    private MarketOperatorListener testAgent;
    
    @SuppressWarnings("unused")
	private void printListofBids( BPMTestOP operator ){
    	List<BidBPM> list = operator.getPositiveSupplies();
	 	for (BidBPM bid: list){
	 		System.out.println(bid.getQuantity()+" ; "+bid.getPriceCapacity() + " ; " + bid.getPriceWork() + " ; " + bid.getTick() + " ; " +bid.getBidType() + " ; " + bid.getMarketOperatorListener().getName());
	 	}
    }
    
    @Before
    public void setUp() throws Exception {
        operator = new BPMTestOP("test", true, "run/summary-logs/test");
        testAgent = new AgentMock();

        RepastTestInitializer.init();
    }
    
    
    @Test
    public void test_01(){
    	 	
    		BidBPM bpos1 = new BidBPM(1, 1, 1, TimeUtil.getCurrentTick(), BidType.POWER_POSITIVE, testAgent);
    		BidBPM bneg1 = new BidBPM(1, 1, 1, TimeUtil.getCurrentTick(), BidType.POWER_NEGATIVE, testAgent);
    		BidBPM bpos2 = new BidBPM(2, 2, 2, TimeUtil.getCurrentTick(), BidType.POWER_POSITIVE, testAgent);
    		BidBPM bneg2 = new BidBPM(2, 2, 2, TimeUtil.getCurrentTick(), BidType.POWER_NEGATIVE, testAgent);
    		BidBPM bpos3 = new BidBPM(3, 3, 3, TimeUtil.getCurrentTick(), BidType.POWER_POSITIVE, testAgent);
    		BidBPM bneg3 = new BidBPM(3, 3, 3, TimeUtil.getCurrentTick(), BidType.POWER_NEGATIVE, testAgent);
    		
    		operator.addPositiveSupply(bpos1);
    	 	operator.addNegativeSupply(bneg1);
    	 	operator.addPositiveSupply(bpos2);
    	 	operator.addNegativeSupply(bneg2);
    	 	operator.addPositiveSupply(bpos3);
    	 	operator.addNegativeSupply(bneg3);
    		
    		TimeUtil.startAt(0);
    	 	operator.clearMarketCapacityPrice();
    		
	        
    	 	TimeUtil.startAt(1);
	        
    	 
    }
}
