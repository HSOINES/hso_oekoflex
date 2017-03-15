package agents;


import markets.BalancingMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;
import util.SequenceDefinition;

/**
 * Gibt Angebote an den Regelenergiemarkt
 */
public interface BPMTrader extends MarketTrader, MarketOperatorListener {
	
    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BPMBidPriority)
    void makeBidBalancingMarket();

    /**
     * This method is invoked from makeBidEOM(). This is necessary, to make it possible to invoke this method from the prerunner.
     *  Negative Ticks are not supported by Repast.
   */
    void makeBidBalancingMarket(long currentTick);

    void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator);
    
    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = 1, priority = SequenceDefinition.BPMACKPriority)
    void acknowledgeBidBPM();

}
