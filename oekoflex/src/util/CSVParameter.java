package util;

import org.apache.commons.csv.CSVFormat;

/**
 * 
 */
public final class CSVParameter {
    public static CSVFormat getCSVFormat() {
        return CSVFormat.EXCEL.withHeader().withDelimiter(';');
    }
    
    public static CSVFormat getCSVFormatDefault() {
        return CSVFormat.DEFAULT.withHeader().withDelimiter(';');
    }
}
