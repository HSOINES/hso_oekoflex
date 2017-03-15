package agents;


import markets.EnergyOnlyMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;
import util.SequenceDefinition;

/**
 * Gibt Angebote an den Spotmarkt

 */
public interface EOMTrader extends MarketTrader, MarketOperatorListener {

    void setSpotMarketOperator(EnergyOnlyMarketOperator spotMarketOperator);

    float getLastClearedPrice();

    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.EOMInterval, priority = SequenceDefinition.EOMBidPriority)
    void makeBidEOM();

    /**
     * This method is invoked from makeBidEOM(). This is necessary, to make it possible to invoke this method from the prerunner.
     * Negative Ticks are not supported by Repast.
     **/
    void makeBidEOM(long currentTick);


    float getCurrentPower();
}
