package hu.tilos.streamer;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class StreamerController {

  private static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmm");

  private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

  @Autowired
  private FileLinkCalculator fileLinkCalculator;

  @Autowired
  private Mp3Joiner joiner;

  @Value("${archive.dir}")
  private File root;


  @RequestMapping(value = "/{type:mp3|download}/tilos-{date:.*}.m3u", produces = "audio/mpeg")
  @ResponseBody
  public String generateM3u(HttpServletRequest request, HttpServletResponse response) {
    CollectionWithSize cws = processRequest(request.getRequestURI());


    StringBuilder b = new StringBuilder();
    b.append("#EXTM3U\n");
    b.append(("#EXTINF:" + cws.size + ", Tilos Rádió - " + FILE_NAME_FORMAT.format(cws.collection.getDescriptor().start) + "\n"));
    //workaround for the WP7Application: use the unsplitted version

    b.append("https://archive.tilos.hu/" + request.getRequestURI().toString().replaceAll("\\.m3u", ".mp3"));

    String filename = "tilos-" + FILE_NAME_FORMAT.format(cws.collection.getDescriptor().start) + "-" + cws.collection.getDescriptor().duration;
    response.addHeader("Content-Type", "audio/x-mpegurl; charset=utf-8");
    response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".m3u\"");
    response.addHeader("Content-Length", "" + b.toString().getBytes().length);
    return b.toString();

  }

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

  private static class CollectionWithSize {
    ResourceCollection collection;
    int size;
  }

  @RequestMapping(value = "/{type:mp3|download}/tilos-{date:.*}.mp3", produces = "audio/mpeg")
  @ResponseBody
  public Collection<ResourceRegion> streamFile(HttpServletRequest request, HttpServletResponse response) {
    MDC.put("requestId", "" + Math.round(Math.random() * 10000));

    CollectionWithSize cws = processRequest(request.getRequestURI());

    response.addHeader("Content-Type", "audio/mpeg");
    response.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes")
    ;
    String filename = "tilos-" + FILE_NAME_FORMAT.format(cws.collection.getDescriptor().start) + "-" + cws.collection.getDescriptor().duration;
    if (request.getRequestURI().contains("download")) {
      response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".mp3\"");
    } else {
      response.addHeader("Content-Disposition", "inline; filename=\"" + filename + ".mp3\"");
    }

    InputStream[] inputStreams = cws.collection.getCollection().stream().map(mp3File -> {
      try {
        FileInputStream fileInputStream = new FileInputStream(new File(root, mp3File.getName()));
        return new LimitedInputStream(fileInputStream, mp3File.getStartOffset(), mp3File.getEndOffset());
      } catch (Exception e) {
        throw new RuntimeException("Can't stream the files", e);
      }
    }).collect(Collectors.toList()).toArray(new InputStream[0]);
    CombinedInputStream combinedInputStream = new CombinedInputStream(inputStreams);

    Mp3File mp3File = cws.collection.getCollection().get(0);

    Resource result = new InputStreamResource(combinedInputStream);
    String range = request.getHeader(HttpHeaders.RANGE);
    List<HttpRange> httpRanges;
    if (range != null) {
      httpRanges = HttpRange.parseRanges(range);

    } else {
      httpRanges = new ArrayList<>();
      httpRanges.add(HttpRange.createByteRange(0));
    }
    return HttpRange.toResourceRegions(httpRanges, new InputStreamResource(combinedInputStream) {
      @Override
      public long contentLength() throws IOException {
        return cws.size;
      }
    });

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

}
