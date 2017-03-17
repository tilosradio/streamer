package hu.tilos.streamer.controller;

import hu.tilos.streamer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RequestParser {

  private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

  @Autowired
  private FileLinkCalculator fileLinkCalculator;

  @Autowired
  private Mp3Joiner joiner;

  @Value("${archive.dir}")
  private File root;


  public CollectionWithSize processRequest(String uri) {
    Segment segment = parse(uri);
    if (segment.duration > 360 * 60 /* 6 hours */) {
      throw new IllegalArgumentException("Duration is too long");
    }

    ResourceCollection collection = fileLinkCalculator.getMp3Links(segment.start, segment.duration);
    for (Mp3File file : collection.getCollection()) {
      File realFile = new File(root, file.getName());
      if (!realFile.exists() || realFile.length() < 35000000) {
        throw new IllegalArgumentException("One or more archive segment is missing or corrupt: " + realFile.getAbsolutePath());
      }
    }
    adjustSizes(root, collection);
    collection.setDescriptor(segment);
    joiner.detectJoins(root, collection);
    joiner.adjustFirstFrame(root, collection);
    int size = collection.getCollection().stream().mapToInt(mp3File -> size(mp3File)).sum();
    CollectionWithSize cws = new CollectionWithSize();
    cws.collection = collection;
    cws.size = size;
    return cws;
  }

  public int size(Mp3File file) {
    int size = (int) new File(root, file.getName()).length();
    if (file.getEndOffset() < size) {
      size = file.getEndOffset();
    }
    return size - file.getStartOffset();
  }

  private void adjustSizes(File root, ResourceCollection collection) {
    for (Mp3File file : collection.getCollection()) {
      int size = (int) new File(root, file.getName()).length();
      if (file.getEndOffset() < size) {
        size = file.getEndOffset();
      }
      file.setEndOffset(size);
    }
  }


  protected Segment parse(String requestURI) {
    try {
      Segment s = new Segment();
      requestURI = requestURI.replaceAll("download\\?=\\w+", "");
      Matcher m = Pattern.compile("^/(?:mp3|download)/tilos-(\\d+)-(\\d+)-(\\d+).*$").matcher(requestURI);
      if (m.matches()) {

        s.start = SDF.parse(m.group(1) + m.group(2));
        Date end = SDF.parse(m.group(1) + m.group(3));
        s.duration = Math.round((end.getTime() - s.start.getTime()) / 1000);
        if (s.duration < 0) {
          s.duration += 24 * 60 * 60;
        }
        return s;

      } else {
        m = Pattern.compile("^/(?:mp3|download)/(\\d+)/(\\d+)/(\\d+).*$").matcher(requestURI);
        if (m.matches()) {
          s.start = SDF.parse(m.group(1) + m.group(2));
          Date end = SDF.parse(m.group(1) + m.group(3));
          s.duration = Math.round((end.getTime() - s.start.getTime()) / 1000);
          return s;
        } else {
          m = Pattern.compile("^/(?:mp3|download)/(\\d+)-(\\d+).*$").matcher(requestURI);
          if (m.matches()) {
            s.start = new Date();
            s.start.setTime(Long.valueOf(m.group(1)) * 1000);
            s.duration = Integer.valueOf(m.group(2));
            return s;
          }
        }
      }
    } catch (Exception ex) {
      throw new IllegalArgumentException("Invalid request format", ex);
    }
    return null;
  }

  public static class CollectionWithSize {
    ResourceCollection collection;
    int size;
  }

}
