package builder;


import repast.simphony.context.Context;
import repast.simphony.util.collections.IndexedIterable;
import util.SequenceDefinition;
import util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import agents.BPMTrader;
import agents.EOMTrader;
import agents.MarketTrader;
import loggers.LogWriterEOM;
import markets.BalancingMarketOperator;
import markets.EnergyOnlyMarketOperator;
import oekoflex.OekoflexAgent;

/** 
 * 
 */
public final class PreRunner {
    private final Context<OekoflexAgent> context;

    public LogWriterEOM loggerEOM; 
    
    public PreRunner(final Context<OekoflexAgent> context) {
        this.context = context;
    }

    public void run(final int prerunTicks) {
        final IndexedIterable<OekoflexAgent> balancingMarketOperatorIterator = context.getObjects(BalancingMarketOperator.class);
        final IndexedIterable<OekoflexAgent> spotMarketOperatorIterator = context.getObjects(EnergyOnlyMarketOperator.class);
        final IndexedIterable<OekoflexAgent> balancingMarketTraderIterator = context.getObjects(BPMTrader.class);
        final IndexedIterable<OekoflexAgent> eomTraderIterator = context.getObjects(EOMTrader.class);

        final BalancingMarketOperator balancingMarketOperator = (BalancingMarketOperator) balancingMarketOperatorIterator.get(0);
        final EnergyOnlyMarketOperator spotMarketOperator = (EnergyOnlyMarketOperator) spotMarketOperatorIterator.get(0);
        final List<BPMTrader> balancingMarketTraders = extract(balancingMarketTraderIterator);
        final List<EOMTrader> eomTraders = extract(eomTraderIterator);

        run_i(balancingMarketOperator, spotMarketOperator, balancingMarketTraders, eomTraders, prerunTicks);
    }

    private void run_i(final BalancingMarketOperator balancingMarketOperator, final EnergyOnlyMarketOperator spotMarketOperator, final List<BPMTrader> balancingMarketTraders, final List<EOMTrader> eomTraders, final int prerunTicks) {
        TimeUtil.startAt(-prerunTicks);
        long currentTick = TimeUtil.getCurrentTick();
        while (currentTick < 0) {
        	
            if (currentTick % SequenceDefinition.BalancingMarketInterval == 0) {
                for (BPMTrader balancingMarketTrader : balancingMarketTraders) {
                    balancingMarketTrader.makeBidBalancingMarket(currentTick);
                }
                balancingMarketOperator.clearMarketCapacityPrice();
                
                
            }
            
            for (EOMTrader eomTrader : eomTraders) {
                eomTrader.makeBidEOM(currentTick);
            }
            
            spotMarketOperator.clearMarket();
            
            for (BPMTrader balancingMarketTrader : balancingMarketTraders) {
                balancingMarketTrader.acknowledgeBidBPM();
            }
            
            balancingMarketOperator.clearMarketEnergyPrice();
            
            for (EOMTrader eomTrader : eomTraders) {
                ((MarketTrader)eomTrader).updatePowerPreceding();
            }
            
            for (EOMTrader eomTrader : eomTraders) {
                eomTrader.logEOM();
            }
            
            loggerEOM.listToFile();
            
            TimeUtil.nextTick();
            currentTick = TimeUtil.getCurrentTick();
        }
        TimeUtil.reset();
    }

    @SuppressWarnings("unchecked")
	private <T extends OekoflexAgent> List<T> extract(final IndexedIterable<OekoflexAgent> iterator) {
        List<T> list = new ArrayList<>();
        for (OekoflexAgent oekoflexAgent : iterator) {
            list.add((T) oekoflexAgent);
        }
        return list;
    }


}
