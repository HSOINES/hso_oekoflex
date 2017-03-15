package markets;

import java.util.List;

import bid.BidEOM;
import oekoflex.OekoflexAgent;

public interface IEnergyOnlyMarketOperator extends OekoflexAgent{

	public void addDemand(BidEOM demand);
	public void addSupply(BidEOM supply);
	
//	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.EOMInterval, priority = SequenceDefinition.EOMClearingPriority)
	public void clearMarket();
	public List<BidEOM> getLastEnergyDemands();
	public List<BidEOM> getLastEnergySupplies();
	
	
}
