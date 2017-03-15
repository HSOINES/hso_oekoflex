package markets;

import bid.BidBPM;
import oekoflex.OekoflexAgent;

/**
 * Clears the balancing power market
 * <ul>
 * 	<li> gets bids as supplies or demands from the market traders
 * 	<li> determines the bids that are accepted 
 * 	<li> notifies the market traders that their bids are accepted or denied
 * </ul>
 * <p>
 * <p>
 * Furthermore has getter functions for:
 * <ul>
 * 	<li> JUnit tests, and
 * 	<li> the diagram
 * </ul>
 */
public interface IBalancingMarketOperator extends OekoflexAgent {

	/** @param supply the positive power to add */
	void addPositiveSupply(BidBPM supply);
	
	/** @param negative the positive power to add */
	void addNegativeSupply(BidBPM supply);
	
	/** Every 16 ticks clearing for Leistungspreis with prio 90 */
//	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BPMClearingPriorityCapacityPrice)
	void clearMarketCapacityPrice();
	
	/** Every tick clearing for Arbeitspreis with prio 77	 */
//	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.EOMInterval, priority = SequenceDefinition.BPMClearingPriorityEnergyPrice)
	void clearMarketEnergyPrice();
	
	
	/** 
	 * Getter for Tests
	 * @return amount of positive power cleared
	 */
	float getTotalClearedPositiveQuantity();
	
	/** 
	 * Getter for Tests
	 * @return amount of negative power cleared
	 */
	float getTotalClearedNegativeQuantity();


	
	/** Getter for diagram  
	 * @return last cleared negative max price
	 */
	float getLastClearedNegativeMaxPrice();
	
	/** 
	 * Getter for diagram 
	 * @return last cleared positive max price
	 */
	float getLastClearedPositiveMaxPrice();

	

	

	// Warum gibt es beim SpotMarketOperator eine stop() Methode mit
	// Repast Scheduler und nicht hier??

}
