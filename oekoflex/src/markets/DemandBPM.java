package markets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import builder.OekoFlexContextBuilder;
import util.CSVParameter;

// Rename to something else, this is the external in csv defined demand a bpm has 
// there are two kinds of demands for the bpm (Quantity, Capacity price) and (Quantity,and work/call price)

// Evtl. Interface und dann mit Interface implementieren
// Liest die csv dateien für Leistungs oder Arbeitspreistupel zu Tick ein und kann dann abgefragt werden

public class DemandBPM {
	
	  private final Map<Long,Float> positiveDemandOnTick;
	  private final Map<Long,Float> negativeDemandOnTick;
	  private final File forwardOutFile;
	
	public DemandBPM(final File priceForwardOutFile) {
        this.forwardOutFile = priceForwardOutFile;
        positiveDemandOnTick = new HashMap<>();
        negativeDemandOnTick = new HashMap<>();
       
        try {
			this.readData();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
    }

    
    public void readData() throws IOException, ParseException {
    	positiveDemandOnTick.clear();
    	negativeDemandOnTick.clear();
        final FileReader reader = new FileReader(forwardOutFile);
        final CSVFormat csvFormat = CSVParameter.getCSVFormat();
        final CSVParser csvParser = csvFormat.parse(reader);
        for (CSVRecord values : csvParser.getRecords()) {
            final long tick = Long.parseLong(values.get("tick"));
            final float demPos = OekoFlexContextBuilder.defaultNumberFormat.parse(values.get("positiveVolume [MW]")).floatValue();
            final float demNeg = OekoFlexContextBuilder.defaultNumberFormat.parse(values.get("negativeVolume [MW]")).floatValue();

            positiveDemandOnTick.put(tick, demPos);
            negativeDemandOnTick.put(tick, demNeg);
        }
        reader.close();
    }
	public Float getPositiveQuantity(long tick){
		if(tick < 0)
			return positiveDemandOnTick.get(positiveDemandOnTick.size() + tick) ;
		else
			return positiveDemandOnTick.get(tick);
	}

	public Float getNegativeQuantity(long tick){
		if(tick < 0)
			return negativeDemandOnTick.get(negativeDemandOnTick.size() + tick);
		else 
			return negativeDemandOnTick.get(tick);
	}
}
