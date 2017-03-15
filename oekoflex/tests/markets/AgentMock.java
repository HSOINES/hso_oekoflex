package markets;

import java.util.ArrayList;
import java.util.List;
import agents.MarketOperatorListener;
import bid.BidBPM;
import bid.BidEOM;
import bid.BidType;


/**Like the name says a mock which is used in different Tests throughout this package.
*					TODO	- Add List which Tests use this mock,	
*							- Maybe a description for this test
*/	

public class AgentMock implements MarketOperatorListener{

	protected List<BidEOM> suppliesList = new ArrayList<BidEOM>();
	protected List<BidEOM> demandsList  = new ArrayList<BidEOM>();
	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "TestAgentforBPM";
	}

	@Override
	public void notifyClearingDone(BidBPM bidAnswer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyClearingDone(BidEOM bidAnswer) {
		if(bidAnswer.bidType == BidType.ENERGY_DEMAND_CONFIRMED){
			demandsList.add(bidAnswer);
			
		}
		
		if(bidAnswer.bidType == BidType.ENERGY_SUPPLY_CONFIRMED){
			suppliesList.add(bidAnswer);
		}
	}
	
	public List<BidEOM> getDemandsList(){
		return demandsList;
	}
	
	public List<BidEOM> getSuppliesList(){
		return suppliesList;
	}

	@Override
	public String getDescription() {
		return "Agentmock";
	}

}
