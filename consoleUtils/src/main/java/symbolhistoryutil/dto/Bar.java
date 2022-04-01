package symbolhistoryutil.dto;

import lombok.Value;

import java.util.Date;

@Value
public class Bar {
    Date date;
    double open;
    double high;
    double low;
    double close;
    double volume;

    public long getDatetime() {
        return getDate().getTime();
    }

    public Bar withDatetime(long l) {
        return new Bar(new Date(l), getOpen(), getHigh(), getLow(), getClose(), 0);
    }
}
