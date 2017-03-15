package agents;

import oekoflex.OekoflexAgent;
import bid.BidBPM;
import bid.BidEOM;

/**
 * Callback für die bearbeiteten Angebote der trader
 */
public interface MarketOperatorListener extends OekoflexAgent {
   
	/** returns the name of this MarketOperatorListener	 */
    String getName();
    
    /** returns the description of this MarketOperatorListener	 */
    String getDescription();

    /**  @param bidAnswer  The answer of the market to one specific BPM bid */
	void notifyClearingDone(BidBPM bidAnswer);

	/**  @param bidAnswer  The answer of the market to one specific EOM bid */
	void notifyClearingDone(BidEOM bidAnswer);
	
}

