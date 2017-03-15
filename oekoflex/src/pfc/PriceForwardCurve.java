package pfc;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import builder.OekoFlexContextBuilder;
import structures.TupelMT;
import util.CSVParameter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/** verwaltet PFC, stellt verschiedene Hilfsfunktionen im Umgang mit der PFC zur Verfügung  */
public final class PriceForwardCurve implements IPriceForwardCurve {

    private final Map<Long, Float> priceOnTick;
    private final File priceForwardOutFile;

    public PriceForwardCurve(final File priceForwardOutFile) {
        this.priceForwardOutFile = priceForwardOutFile;
        priceOnTick = new HashMap<>();
    }

    @Override
    public void readData() throws IOException, ParseException {
        priceOnTick.clear();
        final FileReader reader = new FileReader(priceForwardOutFile);
        final CSVFormat csvFormat = CSVParameter.getCSVFormatDefault();
        final CSVParser csvParser = csvFormat.parse(reader);
        for (CSVRecord values : csvParser.getRecords()) {
            final long tick = Long.parseLong(values.get("tick"));
            final float price = OekoFlexContextBuilder.defaultNumberFormat.parse(values.get("price")).floatValue();
            priceOnTick.put(tick, price);
        }
        reader.close();
    }

    @Override
    public float getPriceSummation(final long currentTick, final int ticks) {
        float sum = 0;
        for (long i = currentTick; i < currentTick + ticks; i++) {
            sum += getPriceOnTick(i);
        }
        return sum;
    }
    
    /**
     * calculates the average price of an interval within the pfc tarting at currentTick and ending at currentick + ticks
     * 
     * @param currentTick Tick to start the interval
     * @param ticks		  amount of ticks to look into the future
     * @return			  the average price over the given interval
     */
    public float avgPriceOverTicks(final long currentTick, final int ticks) {
        float sum = this.getPriceSummation( currentTick,  ticks);
        float avg = sum/ticks;
        return avg;
    }
    

    @Override
    public float getSpread(final long currentTick, final int ticks) {
        float min = getMinimum(currentTick, ticks);
        float max = getMaximum(currentTick, ticks);
        return max - min;
    }

    @Override
    public float getMaximum(final long currentTick, final int ticks) {
        float max = -Float.MAX_VALUE;
        for (long i = currentTick; i < currentTick + ticks; i++) {
            float v = getPriceOnTick(i);
            if (max < v) max = v;
        }
        return max;
    }

    @Override
    public float getNegativePriceSummation(long currentTick, int ticks) {
        float sum = 0;
        for (long i = currentTick; i < currentTick + ticks; i++) {
            float priceOnTick = getPriceOnTick(i);
            if (priceOnTick < 0) {
                sum += -priceOnTick;
            }
        }
        return sum;
    }

    @Override
    public List<Long> getTicksWithLowestPrices(int nTicks, long fromTick, int intervalTicks) {
        List<Long> ticksSortedByPriceAscending = getTicksSortedByPriceAscending(fromTick, intervalTicks);
        if (nTicks > ticksSortedByPriceAscending.size()) nTicks = ticksSortedByPriceAscending.size();
        return ticksSortedByPriceAscending.subList(0, nTicks);
    }

    private List<Long> getTicksSortedByPriceAscending(long fromTick, int intervalTicks) {
        List<Long> ticksSortedByPriceAscending = new ArrayList<>();
        for (long tick = fromTick; tick < fromTick + intervalTicks; tick++) {
            float v = getPriceOnTick(tick);
            long insertionIndex = ticksSortedByPriceAscending.size();
            for (Long compareTick : ticksSortedByPriceAscending) {
                float priceOnTick = getPriceOnTick(compareTick);
                if (v <= priceOnTick){
                    insertionIndex = ticksSortedByPriceAscending.indexOf(compareTick);
                    break;
                }
            }
            ticksSortedByPriceAscending.add((int) insertionIndex, tick);
        }
        return ticksSortedByPriceAscending;
    }
    

    @Override
    public float getMinimum(final long currentTick, final int ticks) {
        float min = Float.MAX_VALUE;
        for (long i = currentTick; i < currentTick + ticks; i++) {
            float v = getPriceOnTick(i);
            if (min > v) min = v;
        }
        return min;
    }

    @Override
    public float getPriceOnTick(long tick) {
//    	if(tick < 0){
//    		tick = 35040 + tick % 35041;
//    		
//    	}
//    	final Float price = priceOnTick.get(tick % 35041);
        final Float price = priceOnTick.get(tick);
        return price == null ? 0 : price;
    }

    @Override
    public List<Long> getTicksWithHighestPrices(int nTicks, long fromTick, int intervalTicks) {
        List<Long> ticksSortedByPriceAscending = getTicksSortedByPriceAscending(fromTick, intervalTicks);
        Collections.reverse(ticksSortedByPriceAscending);
        if (nTicks > ticksSortedByPriceAscending.size()) nTicks = ticksSortedByPriceAscending.size();
        return ticksSortedByPriceAscending.subList(0, nTicks);
    }
    
    public List<TupelMT> getTupelSet(long fromTick, int intervalTicks) {
        List<TupelMT> set = new ArrayList<>();
        for (long tick = fromTick; tick < fromTick + intervalTicks; tick++) {
            float priceForTick = getPriceOnTick(tick);
            set.add(new TupelMT(tick, priceForTick));
        }
        return set;
    }
    
    
    public List<TupelMT> getSetSortedByPriceAscending(long fromTick, int intervalTicks){
    	List<TupelMT> set = getTupelSet( fromTick, intervalTicks);
    	set.sort(new TupelMT.ascPriceDescTickComparator());
    	return set;
    }
    
    public List<TupelMT> getSetSortedByPriceDescending(long fromTick, int intervalTicks){
    	List<TupelMT> set = getTupelSet( fromTick, intervalTicks);
    	set.sort(new TupelMT.ascPriceDescTickComparator());
    	Collections.reverse(set);
    	return set;
    }
    
    public List<TupelMT> getSetWithHighestPrices(int nTicks, long fromTick, int intervalTicks) {
        List<TupelMT> set = getSetSortedByPriceAscending(fromTick, intervalTicks);
        Collections.reverse(set);
        if (nTicks > set.size()) nTicks = set.size();
        return set.subList(0, nTicks);
    }
    
    public List<TupelMT> getSetWithLowestPrices(int nTicks, long fromTick, int intervalTicks) {
        List<TupelMT> set = getSetSortedByPriceAscending(fromTick, intervalTicks);
        if (nTicks > set.size()) nTicks = set.size();
        return set.subList(0, nTicks);
    }
}