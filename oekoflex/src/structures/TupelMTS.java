package structures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;




public class TupelMTS extends TupelMT{

	public Status status;

	public TupelMTS(){
		super(0,0);
		this.status = Status.UNDEFINED;
	}
	
	public TupelMTS(long tick, float price) {
		super(tick, price);
		this.status = Status.UNDEFINED;
	}
	
	public TupelMTS(long tick, float price, Status status) {
		super(tick, price);
		this.status = status;
	}
	
	public TupelMTS(TupelMT t) {
		super(t.tick, t.price);
		this.status = Status.UNDEFINED;
	}
	
	@Override
	public String toString(){
		return super.toString() + status + " ";
	}
	
	
	public enum Status {
		DISCHARGE("discharge"), 
		CHARGE("charge"), 
		REST("rest"), 
		UNDEFINED("undefined");
		
		String description;
		
		private Status(String desc){
			this.description = desc;
		}
		
		public String toString(){
			return this.description;
		}
	}
	
	/**
	 * @param listMT	a List of TupelMT elements 
	 * @return			converted List of TupelMTS all with the status UNDEFINED
	 */
	public static List<TupelMTS> convertToTupelMT_To_MTS(List<TupelMT> listMT) {
		List<TupelMTS> list = new ArrayList<TupelMTS>();
		
		for (Iterator<TupelMT> iterator = listMT.iterator(); iterator.hasNext();) {
			TupelMT tupel = (TupelMT) iterator.next();
			TupelMTS t = new TupelMTS(tupel);
			list.add(t);
		}
		return list;
	}
	
	
	/** Aufsteigende Sortierung nach Preis danach absteigende Sortierung nach Tick */
	public static class ascPriceDescTickComparator implements Comparator<TupelMTS> {
		@Override
		public int compare(TupelMTS o1, TupelMTS o2) {
			final int compare = Float.compare( o1.price, o2.price);
			if (compare == 0) {
				return Long.compare(o2.tick, o1.tick);
			}
			return compare;
		}
	}
	
	/** Aufsteigende Sortierung nach Preis danach aufsteigende Sortierung nach Tick */
	public static class ascPriceAscTickComparator implements Comparator<TupelMTS> {
		@Override
		public int compare(TupelMTS o1, TupelMTS o2) {
			final int compare = Float.compare( o1.price, o2.price);
			if (compare == 0) {
				return Long.compare(o1.tick , o2.tick);
			}
			return compare;
		}
	}
	
	/** Absteigende Sortierung nach Preis danach aufsteigende Sortierung nach Tick */
	public static class descPriceAscTickComparator implements Comparator<TupelMTS> {
		@Override
		public int compare(TupelMTS o1, TupelMTS o2) {
			final int compare = Float.compare( o2.price, o1.price);
			if (compare == 0) {
				return Long.compare(o1.tick , o2.tick);
			}
			return compare;
		}
	}
	
	
	/** Aufsteigende Sortierung nach Tick danach absteigende Sortierung nach Preis */
	public static class ascTickDescPriceComparator implements Comparator<TupelMTS> {
		@Override
		public int compare(TupelMTS o1, TupelMTS o2) {
			final int compare =  Long.compare(o2.tick, o1.tick);
			if (compare == 0) {
				return Float.compare( o1.price, o2.price);
			}
			return compare;
		}
	}
	
	/** Aufsteigende Sortierung nach Tick danach aufsteigende Sortierung nach Preis */
	public static class ascTickAscPriceComparator implements Comparator<TupelMTS> {
		@Override
		public int compare(TupelMTS o1, TupelMTS o2) {
			final int compare = Long.compare(o1.tick , o2.tick);
			if (compare == 0) {
				return Float.compare( o1.price, o2.price);
			}
			return compare;
		}
	}
	
	/** Ermöglicht die Aufsteigende Sortierung nach Tick, Preis muss nicht berücksichtigt werden da keine zwei gleichen Ticks existieren können*/	 
	public static class ascTimeComparator implements Comparator<TupelMTS> {
		@Override
		public int compare(TupelMTS o1, TupelMTS o2) {
			return Long.compare(o1.tick, o2.tick);
		}
	}
	
	/** Ermöglicht die absteigende Sortierung nach Tick, Preis muss nicht berücksichtigt werden da keine zwei gleichen Ticks existieren können */
	public static class desTimeComparator implements Comparator<TupelMTS> {
		@Override
		public int compare(TupelMTS o1, TupelMTS o2) {
			return Long.compare(o2.tick, o1.tick);
		}
	}

	
	
}
