package util;

/**
 * Defines: 
 * <ul>
 * 	<li> priorities/the order in which Repast Simphony schedules the agents, operators etc,
 * 	<li> intervals of the markets, and
 * 	<li> the simulation starting point( tick where the simulation starts)
 * </ul>
 * Higher priorities are executed before the others with lower priority.
 */
public final class SequenceDefinition {
	
    /** priority of  price forward-curve-generator*/
    public static final int PriceForwardCurveGeneratorPriority = 1000;
    
    /** priority to clear BPM Operators lists */
    public static final int ClearListPriority = 150;
    
    /** priority of  balancing power market bid*/
    public static final int BPMBidPriority = 100;
    
    /** priority of  balancing power market clearing for the capacityPrice (Leistungspreis)*/
    public static final int BPMClearingPriorityCapacityPrice = 90;
    
    /** priority of  energy only market bid*/
    public static final int EOMBidPriority = 80;
    
    /** priority of  energy only market clearing*/
    public static final int EOMClearingPriority = 75;
    
    public static final int BPMACKPriority =  70;
    
    /** priority of  balancing power market clearing for the energyPrice (Arbeitspreis)*/
    public static final int BPMClearingPriorityEnergyPrice = 60;
    
    public static final int CALCULATERESULTINGPOWER = 50;
    
    /** priority of reporting */
    public static final int ReportingPriority = 1;
    
    /** interval of the balancing power market in ticks */
    public static final int BalancingMarketInterval = 16;
    
    /** interval of the balancing power market in ticks */
    public static final int BalancingMarketIntervalEnergzPriceInterval = 1;
    
    /** interval of the energy only market in ticks */
    public static final int EOMInterval = 1;
    
    /** number of ticks of one day */
    public static final int DayInterval = 96;
    
    
    

    /** starting point, Repast doesn't allow negative Ticks */
    public static final int SimulationStart = 0;
}
