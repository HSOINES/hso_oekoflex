package bid;

/**
 * This enum specifies all types of bids to market operators EOM and BPM
 */
public enum BidType {
	
	// Bid-types for the Energy Only Market
    ENERGY_DEMAND(false), ENERGY_DEMAND_CONFIRMED(false),
    ENERGY_SUPPLY(true), ENERGY_SUPPLY_CONFIRMED(true), 
    ENERGY_SUPPLY_MUSTRUN(true), ENERGY_SUPPLY_MUSTRUN_CONFIRMED(true), 
    ENERGY_DEMAND_RESIDUAL_LOAD(false),
    
    // Bid-types for the Balancing Power Market
    // POWER_X_Y	X = Kind 	Y:	CONFIRMED is what the agent has to hold available ; CALL is what is called from the BPM Operator 
    POWER_POSITIVE(true),  POWER_POSITIVE_CONFIRMED(true) , POWER_POSITIVE_CALL(true),
    POWER_NEGATIVE(false), POWER_NEGATIVE_CONFIRMED(false), POWER_NEGATIVE_CALL(false),
    
    START_VALUE(true), 
    NULL_BID(false);

    private final boolean positive;

    BidType(final boolean positive) {
        this.positive = positive;
    }
    
    
    public boolean isPositiveAmount() {
        return positive;
    }
}
