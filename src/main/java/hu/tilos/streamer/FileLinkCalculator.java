package hu.tilos.streamer;

import hu.tilos.streamer.Mp3File;
import hu.tilos.streamer.ResourceCollection;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileLinkCalculator {

  private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("'/'yyyy'/'MM'/'dd'/tilosradio-'yyyMMdd'-'HHmm'.mp3'");

  public ResourceCollection getMp3Links(Date start, int duration) {
    ResourceCollection collection = new ResourceCollection();
    Date from = getPrevHalfHour(start);

    Date end = new Date();
    end.setTime(start.getTime() + 1000 * duration);

    Date i = new Date();
    Date lastStart = new Date();
    i.setTime(from.getTime());
    while (i.compareTo(end) < 0) {


      collection.add(new Mp3File(FILE_NAME_FORMAT.format(i)));
      lastStart.setTime(i.getTime());
      i.setTime(i.getTime() + 60 * 30 * 1000);
    }

    int startOffset = (int) ((start.getTime() - from.getTime()) / 1000);
    collection.getCollection().get(0).setStartOffset((int) Math.round(startOffset * 38.28125 * 836));
    int endOffset = (int) ((end.getTime() - lastStart.getTime())) / 1000;
    collection.getCollection().get(collection.getCollection().size() - 1).setEndOffset((int) Math.round(endOffset * 38.28125 * 836));
    return collection;
  }

  public Date getPrevHalfHour(Date date) {
    Date result = new Date();
    result.setTime(date.getTime() / 1000 * 1000);
    result.setSeconds(0);
    if (result.getMinutes() >= 30) {
      result.setMinutes(30);
    } else {
      result.setMinutes(0);
    }
    return result;
  }

}
