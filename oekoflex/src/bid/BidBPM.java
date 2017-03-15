package bid;

import java.util.Comparator;

import agents.MarketOperatorListener;

/**
 * Der Regelenergieabruf( also bei jedem Tick erfolgt in [MWh]
 * Das Angebot ist jedoch Leistung also [MW]
 * Zurückgemeldet wird alle 16 Ticks die vorzuhaltende Leistung [MW]
 * und danach in jedem (auch im Tick mit Vorhaltung) Tick der Regelenergieabruf in [MWh]
 * 
 */
public class BidBPM implements IBid{
	
	/** the type of the bid */
	public  BidType bidType;
	
	/** the tick the bid was made */
	private long tick;
	
	/** the quantity in [MW] */
	private float quantity;
	
	/** the capacity price in [Euro/MW]*/
	private float priceCapacity;
	
	/** the work price in [Euro/MWh] */
	private float priceWork;
	
	/** the one (Agent) who made the bid */
	private final MarketOperatorListener marketOperatorListener;
	
	
	/**
	 * Constructor for BidSupport2
	 * 
	 * @param quantity					the quantity in [MW]
	 * @param priceCapacity				the capacity price in [Euro/MW]
	 * @param priceWork					the work price in [Euro/MWh]
	 * @param tick						the tick the bid was made
	 * @param btype						the type of the bid
	 * @param marketOperatorListener	the one (Agent) who made the bid 
	 */
	public BidBPM(float quantity, float priceCapacity, float priceWork, long tick, BidType btype, MarketOperatorListener marketOperatorListener){
		this.quantity = quantity;
		this.priceWork = priceWork;
		this.priceCapacity = priceCapacity;
		this.tick = tick;
		this.bidType = btype;
		this.marketOperatorListener = marketOperatorListener;
	}
	
	
	/**@return the tick the bid was made */
	 public long getTick() { return this.tick; }
		
	/** @return the capacity price in [Euro/MW] */
	public float getPriceCapacity() { return this.priceCapacity; }

	/** @return the work price in [Euro/MWh]  */
	public float getPriceWork() { return this.priceWork; }

	/** @return quantity of this bin in [MW] */
	public float getQuantity() { return this.quantity; }
	
	/**  @param newQuantity the adjusted quantity of this bid*/
	public void setQuantity(float newQuantity) { this.quantity = newQuantity; }

	/**@return the specific bid type of this bid */
	@Override
	public BidType getBidType() { return this.bidType; }
	
	 /**@return the specific market listener (the Agent) who made the bid */
    @Override
    public MarketOperatorListener getMarketOperatorListener() { return this.marketOperatorListener; }
    
    
    
    
    /**
     *  Is the comparator for sorting capacity in BPM
     *  Sorts by price, in case prices are the same sorts by quantity
     *  smaller capacity prices before bigger ones, if both capacity prices are the same the bigger quantity is first
     */
    public static class SupplyComparatorCapacity implements Comparator<BidBPM> {
        @Override
        public int compare(BidBPM o1, BidBPM o2) {
            final int compare = Float.compare(o1.getPriceCapacity(), o2.getPriceCapacity());
            if (compare == 0) {
                return Float.compare(o2.getQuantity(), o1.getQuantity());  	
            }
            return compare;
        }
    }
    
    /**
    *  Is the comparator for sorting work in BPM
    *  Sorts by price, in case prices are the same sorts by quantity
    *  smaller work prices before bigger ones, if both work prices are the same the bigger quantity is first
    */
    public static class SupplyComparatorWork implements Comparator<BidBPM> {
        @Override
        public int compare(BidBPM o1, BidBPM o2) {
            final int compare = Float.compare(o1.getPriceWork(), o2.getPriceWork());
            if (compare == 0) {
                return Float.compare(o2.getQuantity(), o1.getQuantity());  	
            }
            return compare;
        }
    }  
}
