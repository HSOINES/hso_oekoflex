package bid;

import agents.MarketOperatorListener;

public interface IBid {
	
	MarketOperatorListener getMarketOperatorListener();

	BidType getBidType();

}
