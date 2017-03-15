package structures;

import java.util.Comparator;

/**
 * Represents a tupel of Marketprice and tick
 */
public class TupelMT {
	public long tick;
	public float price;
	
	/** Default constructor will set the values to 0 , 0*/
	public TupelMT(){
		this.tick = 0;
		this.price = 0;
	}
	
	public TupelMT(long tick, float price){
		this.tick = tick;
		this.price = price;
	}
	
	@Override
	public String toString(){
		return " " + price + " , " + tick + " ";
	}
	
	/** Aufsteigende Sortierung nach Preis danach absteigende Sortierung nach Tick */
	public static class ascPriceDescTickComparator implements Comparator<TupelMT> {
		@Override
		public int compare(TupelMT o1, TupelMT o2) {
			final int compare = Float.compare( o1.price, o2.price);
			if (compare == 0) {
				return Long.compare(o2.tick, o1.tick);
			}
			return compare;
		}
	}
	
	/** Aufsteigende Sortierung nach Preis danach aufsteigende Sortierung nach Tick */
	public static class ascPriceAscTickComparator implements Comparator<TupelMT> {
		@Override
		public int compare(TupelMT o1, TupelMT o2) {
			final int compare = Float.compare( o1.price, o2.price);
			if (compare == 0) {
				return Long.compare(o1.tick , o2.tick);
			}
			return compare;
		}
	}
	
	/** Absteigende Sortierung nach Preis danach aufsteigende  Sortierung nach Tick */
	public static class dscPriceAscTickComparator implements Comparator<TupelMT> {
		@Override
		public int compare(TupelMT o1, TupelMT o2) {
			final int compare = Float.compare( o2.price, o1.price);
			if (compare == 0) {
				return Long.compare(o1.tick , o2.tick);
			}
			return compare;
		}
	}
	
	/** Absteigende Sortierung nach Preis danach aufsteigende  Sortierung nach Tick */
	public static class dscPriceDscTickComparator implements Comparator<TupelMT> {
		@Override
		public int compare(TupelMT o2, TupelMT o1) {
			final int compare = Float.compare( o2.price, o1.price);
			if (compare == 0) {
				return Long.compare(o2.tick, o1.tick);
			}
			return compare;
		}
	}
	
	/** Aufsteigende Sortierung nach Tick danach absteigende Sortierung nach Preis */
	public static class ascTickDescPriceComparator implements Comparator<TupelMT> {
		@Override
		public int compare(TupelMT o1, TupelMT o2) {
			final int compare =  Long.compare(o2.tick, o1.tick);
			if (compare == 0) {
				return Float.compare( o1.price, o2.price);
			}
			return compare;
		}
	}
	
	/** Aufsteigende Sortierung nach Tick danach aufsteigende Sortierung nach Preis */
	public static class ascTickAscPriceComparator implements Comparator<TupelMT> {
		@Override
		public int compare(TupelMT o1, TupelMT o2) {
			final int compare = Long.compare(o1.tick , o2.tick);
			if (compare == 0) {
				return Float.compare( o1.price, o2.price);
			}
			return compare;
		}
	}
}
