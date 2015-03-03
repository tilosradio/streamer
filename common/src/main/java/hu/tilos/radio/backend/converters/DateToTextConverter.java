package hu.tilos.radio.backend.converters;

import org.dozer.DozerConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToTextConverter extends DozerConverter<Date, String> {

    public DateToTextConverter() {
        super(Date.class, String.class);
    }

    @Override
    public String convertTo(Date source, String destination) {
        if (source == null) {
            return "";
        }
        return new SimpleDateFormat(getParameter()).format(source);
    }

    @Override
    public Date convertFrom(String source, Date destination) {
        try {
            if (source == null) {
                return null;
            } else {
                return new SimpleDateFormat(getParameter()).parse(source);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
