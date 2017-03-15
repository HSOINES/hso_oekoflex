package bid;

import java.util.Comparator;
import agents.MarketOperatorListener;

public class BidEOM implements IBid{
	
	/** the type of the bid */
	public  BidType bidType;
	
	/** the tick the bid was made */
	private long tick;
	
	/** the quantity in [MWh] */
	private float quantity;
	
	/** the price in [Euro/MWh]*/
	private float price;

	/** the one (Agent) who made the bid */
	private final MarketOperatorListener marketOperatorListener;
	
	/**
	 * Constructor for BidBPM
	 * 
	 * @param quantity					the quantity in [MWh]
	 * @param price						the  price in [Euro/MWh]
	 * @param tick						the tick the bid was made
	 * @param btype						the type of the bid
	 * @param marketOperatorListener	the one (Agent) who made the bid 
	 */
	public BidEOM(float quantity, float price, long tick, BidType btype, MarketOperatorListener marketOperatorListener){
		this.quantity = quantity;
		this.price = price;
		this.tick = tick;
		this.bidType = btype;
		this.marketOperatorListener = marketOperatorListener;
		
	}
	
	/** @return the specific agent who made this bid */
	@Override
	public MarketOperatorListener getMarketOperatorListener() {
		return this.marketOperatorListener;
	}

	/** @return the specific type of this bid */
	@Override
	public BidType getBidType() {
		return this.bidType;
	}

	/** @return the quantity in [Euro/MWh] */
	public float getPrice() {
		return price;
	}

	/** @return the quantity in [MWh] */
	public float getQuantity() {
		return quantity;
	}
	
	/** @return the tick the bid was made  */
	public long getTick() {
		return tick;
	}

	@Override 
	public String toString(){
		return quantity + ";" + price + ";" + tick + ";" + bidType.toString() +";" + marketOperatorListener.getName() ;
		
	}
	
	
	
	
	/**  Ermöglicht die Aufsteigende Sortierung nach Preis
	 *   bei gleichem Preis wird absteigend nach Menge sortiert. 
	 */
	public static class SupplyComparatorEOM implements Comparator<BidEOM> {
		@Override
		public int compare(BidEOM o1, BidEOM o2) {
			final int compare = Float.compare( o1.getPrice(), o2.getPrice());
			if (compare == 0) {
				return Float.compare(o2.getQuantity(), o1.getQuantity());
			}
			return compare;
		}
	}
	
	
	/** Ermöglicht die Absteigende Sortierung nach Preis
	 *   bei gleichem Preis wird absteigend nach Menge sortiert. 
	 */
	public static class DemandComparatorEOM implements Comparator<BidEOM> {
		@Override
		public int compare(BidEOM o1, BidEOM o2) {
			final int compare = Float.compare(o2.getPrice(),o1.getPrice());
			if (compare == 0) {
				return Float.compare(o2.getQuantity(), o1.getQuantity());
			}
			return compare;
		}
	}
}
