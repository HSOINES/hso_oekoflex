package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Datum-Format
 */
@SuppressWarnings("serial")
public final class OekoflexDateFormat extends SimpleDateFormat {
    public OekoflexDateFormat() {
        super("yyyy-MM-dd HH:mm:ss z");
    }

    @Override
    public Date parse(final String source) throws ParseException {
        return super.parse(source + " GMT");
    }
}
