package agents;


import oekoflex.OekoflexAgent;


/**
 * Standard methods for all MarketTraders, necessary interface because Repast has to know which agents act within the simulation
 */
public interface MarketTrader extends OekoflexAgent {
	String getDescription();
    float getLastAssignments();		
    void updatePowerPreceding();
    void logEOM();
    void logBPM();
}
