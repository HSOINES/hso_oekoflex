package markets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import bid.BidEOM;
import bid.BidType;
import oekoflex.OekoflexAgent;
import util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import agents.MarketOperatorListener;

public class EnergyOnlyMarketOperator implements IEnergyOnlyMarketOperator, OekoflexAgent {
	
	private static final Log log = LogFactory.getLog(EnergyOnlyMarketOperator.class);
	
	private String name;
	protected List<BidEOM> suppliesList;
	protected List<BidEOM> demandsList;
	
	
	private float clearedQuantity;
	private float clearedPrice;
	private float lastClearedQuantity;
	private AssignmentType lastAssignmentType;
	
	private BidEOM.DemandComparatorEOM demandComparator = new BidEOM.DemandComparatorEOM();
	private BidEOM.SupplyComparatorEOM supplyComparator = new BidEOM.SupplyComparatorEOM();
	

	
	public EnergyOnlyMarketOperator(String name,  final boolean loggingActivated, final String logDirName) throws IOException{
		this.name = name;
		this.suppliesList = new ArrayList<BidEOM>();
		this.demandsList = new ArrayList<BidEOM>();
	
	}
	

	
	
	
	@Override
	public void addDemand(BidEOM demand) {
		demandsList.add(demand);
	}

	@Override
	public void addSupply(BidEOM supply) {
		suppliesList.add(supply);
	}

	
	// Hier muss RepastSchedule Methode hin
	@Override
	public void clearMarket() {
		
		if (demandsList.size() < 1 || this.suppliesList.size() < 1) {
	          throw new IllegalStateException("Sizes unsufficient! Supply Size: " + suppliesList.size() + ", DemandSize: " + demandsList.size());
	      }
		
		
		// sort both lists according to specification
		demandsList.sort(demandComparator);
		suppliesList.sort(supplyComparator);
		
		// Generate Itarators for the demand and supply lists
		Iterator<BidEOM> demandIterator = demandsList.iterator();
		Iterator<BidEOM> supplyIterator = suppliesList.iterator();

		// increments until prices match
		float totalSupplyQuantity = 0;
		float totalDemandQuantity = 0;

		clearedQuantity = 0;
		
		// Indicates next element:
		// balance < 0 -> energyDemands  are fetched
		// balance > 0 -> energySupplies are fetched
		float balance = 0;
		
		// Stops clearing if false
		boolean moreSupplies = true;
		boolean moreDemands  = true;

		// current bids to clear
		BidEOM demand = demandIterator.next();
		BidEOM supply = supplyIterator.next();
		balance = demand.getQuantity() - supply.getQuantity();		
		
		boolean case_b = false;
		
		do {
			String logString = "";
			if (balance > 0) {
				if (supplyIterator.hasNext()) {
					BidEOM supplyPrevious = supply;
					supply = supplyIterator.next();
					if (demand.getPrice() <= supply.getPrice()) {		
						supply = supplyPrevious;
						break;
					}
					
					balance -= supply.getQuantity();
					
					totalSupplyQuantity += supply.getQuantity();
					logString = "Supply assigned. Price: " + supply.getPrice() + ", Quantity: "  + supply.getQuantity();
							
				} else {
					moreSupplies = false;
				}
				
			} else if (balance < 0) {
				if (demandIterator.hasNext()) {
					BidEOM demandPrevious = demand;
					demand = demandIterator.next();
					if (demand.getPrice() <= supply.getPrice()) {
						case_b = true;
						demand = demandPrevious;
						break;
					}
					balance += demand.getQuantity();
					
					
					totalDemandQuantity += demand.getQuantity();
					logString = "Demand assigned. Price: " + demand.getPrice() + ", Quantity: " + demand.getQuantity();
							
				} else {
					moreDemands = false;
				}
				
			} else {
				if (demandIterator.hasNext()) {
					BidEOM demandPrevious = demand;
					demand = demandIterator.next();
					if (!(supply == null) && demand.getPrice() <= supply.getPrice()) {
						case_b = true;
						demand = demandPrevious;
						break;
					}
					balance += demand.getQuantity();
					totalDemandQuantity += demand.getQuantity();
					logString = "Demand assigned. Price: " + demand.getPrice() + ", Quantity: "	+ demand.getQuantity();
						
				} else {
					moreDemands = false;
					if (balance == 0) {
						log.trace("market stops. balance is 0, no more demands.");
						break;
					}
				}
			}
			log.trace("                                                      " + logString + ", " + "Balance: "+ balance);
		} while (supply == null || (moreDemands && balance <= 0) || (moreSupplies && balance > 0)); // Demand +  Supply  immer quantity > 0!!!


		clearedQuantity = Math.min(totalDemandQuantity, totalSupplyQuantity);
		
		
		
		if (balance < 0) { // Supply cut
			 lastClearedQuantity = supply.getQuantity() + balance;						
			 lastAssignmentType = AssignmentType.PartialSupply;
			 notify(supply,  demand , clearedQuantity, balance, case_b);  
			 
		} else if (balance > 0) { // Demand cut
			 lastClearedQuantity = demand.getQuantity() - balance;						
			 lastAssignmentType = AssignmentType.PartialDemand;

			 notify(supply,  demand , clearedQuantity, balance, case_b);  	
			 
		} else {
			 lastAssignmentType = AssignmentType.Full;
			 notify(supply, demand , clearedQuantity , balance, case_b);  				
		}
		
	}
	
	
  private void notify(final BidEOM lastSupply, final BidEOM lastDemand, float clearedQuantity , float balance,boolean case_b  ) {
		
	  	float marketClearingPrice = lastSupply.getPrice();
	  	
	  
	  	
	  	clearedPrice = marketClearingPrice;
	  	
		long tick = TimeUtil.getCurrentTick();

		BidEOM supply = suppliesList.get(0);
		BidEOM demand = demandsList.get(0);
		
		int i = 1;
		
		while (supply != lastSupply) {
			BidType b;
			if(supply.getBidType() == BidType.ENERGY_SUPPLY_MUSTRUN){
				b = BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED;
			}else{
				b = BidType.ENERGY_SUPPLY_CONFIRMED;
			} 
			BidEOM bidAnswer = new BidEOM(supply.getQuantity(), marketClearingPrice, tick, b, supply.getMarketOperatorListener());
			doNotify(bidAnswer);
			supply = suppliesList.get(i);
			i++;
		} 
		
		int j = 1;
		while (demand != lastDemand) {
			BidEOM bidAnswer = new BidEOM(demand.getQuantity(), marketClearingPrice, tick, BidType.ENERGY_DEMAND_CONFIRMED, demand.getMarketOperatorListener());
			doNotify(bidAnswer);
			demand = demandsList.get(j);
			j++;
		} 
		
		
		
		
		if (balance > 0) {
			BidType b;
			if(supply.getBidType() == BidType.ENERGY_SUPPLY_MUSTRUN){
				b = BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED;
			}else{
				b = BidType.ENERGY_SUPPLY_CONFIRMED;
			} 
			BidEOM bidAnswer = new BidEOM((supply.getQuantity()), marketClearingPrice, tick, b, supply.getMarketOperatorListener());
			doNotify(bidAnswer);
			BidEOM bidAnswer2 = new BidEOM(demand.getQuantity()-balance , marketClearingPrice, tick, BidType.ENERGY_DEMAND_CONFIRMED, demand.getMarketOperatorListener());
			doNotify(bidAnswer2);
			
		} else if(balance < 0) {
			BidType b;
			if(supply.getBidType() == BidType.ENERGY_SUPPLY_MUSTRUN){
				b = BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED;
			}else{
				b = BidType.ENERGY_SUPPLY_CONFIRMED;
			} 
			BidEOM bidAnswer = new BidEOM(supply.getQuantity()+balance, marketClearingPrice, tick, b, supply.getMarketOperatorListener());
			doNotify(bidAnswer);
			BidEOM bidAnswer2 = new BidEOM( (demand.getQuantity()) , marketClearingPrice, tick, BidType.ENERGY_DEMAND_CONFIRMED, demand.getMarketOperatorListener());
			doNotify(bidAnswer2);
			
		}else{
			BidType b;
			if(supply.getBidType() == BidType.ENERGY_SUPPLY_MUSTRUN){
				b = BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED;
			}else{
				b = BidType.ENERGY_SUPPLY_CONFIRMED;
			} 
			BidEOM bidAnswer = new BidEOM(supply.getQuantity(), marketClearingPrice, tick, b, supply.getMarketOperatorListener());
			doNotify(bidAnswer);
			BidEOM bidAnswer2 = new BidEOM(demand.getQuantity() , marketClearingPrice, tick, BidType.ENERGY_DEMAND_CONFIRMED, demand.getMarketOperatorListener());
			doNotify(bidAnswer2);
		}

		// For the supply list: Iterate over the rest and confirm them with quantity 0
		for (; i < suppliesList.size(); i++) {
			supply = suppliesList.get(i);
			BidType b;
			if(supply.getBidType() == BidType.ENERGY_SUPPLY_MUSTRUN){
				b = BidType.ENERGY_SUPPLY_MUSTRUN_CONFIRMED;
			}else{
				b = BidType.ENERGY_SUPPLY_CONFIRMED;
			} 
			BidEOM bidAnswer = new BidEOM(0.0f, marketClearingPrice, tick, b, supply.getMarketOperatorListener());
			doNotify(bidAnswer);
		}
		
		// For the demand list: Iterate over the rest and confirm them with quantity 0
		for (; j < demandsList.size(); j++){
			demand = demandsList.get(j);
			BidEOM bidAnswer2 = new BidEOM(0 , marketClearingPrice, tick, BidType.ENERGY_DEMAND_CONFIRMED, demand.getMarketOperatorListener());
			doNotify(bidAnswer2);
		}
		
		suppliesList.clear();
		demandsList.clear();
}
	
	
	// ein Gebot zurückgeben, Logging
	private void doNotify(BidEOM bid) {
		MarketOperatorListener agent = bid.getMarketOperatorListener();
		
        agent.notifyClearingDone(bid);				
     
	}


	@Override
	public List<BidEOM> getLastEnergyDemands() {
		return this.demandsList;
	}

	@Override
	public List<BidEOM> getLastEnergySupplies() {
		return this.suppliesList;
	}
	
	public String getName(){
		return this.name;
	}
	
	public float getClearedPrice(){
		return clearedPrice;
	}
	
	public AssignmentType getLastAssignmentType(){
		return lastAssignmentType;
	}
	public float getLastClearedQuantity(){
		return lastClearedQuantity;
	}
	
	public float getLastClearedPrice(){
		return this.clearedPrice;
	}
	
	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
	
}
