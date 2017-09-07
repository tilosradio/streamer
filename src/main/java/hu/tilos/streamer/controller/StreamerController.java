package hu.tilos.streamer.controller;

import hu.tilos.streamer.CombinedInputStream;
import hu.tilos.streamer.LimitedInputStream;
import hu.tilos.streamer.ThrottledInputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StreamerController {

  private static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmm");
  private static final Logger LOG = LoggerFactory.getLogger(StreamerController.class);

  @Value("${archive.dir}")
  private File root;

  @Value("${cache.dir}")
  private File cacheDir;

  @Value("${cache.url}")
  private String cacheUrl;

  private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

  @Value("${throttle}")
  private long throttle;

  @Autowired
  private RequestParser parser;


  @RequestMapping(value = "/{type:mp3|download}/tilos-{date:.*}.mp3", produces = "audio/mpeg")
  @ResponseBody
  public Collection<ResourceRegion> streamFile(HttpServletRequest request, HttpServletResponse response) {

    String uri = request.getRequestURI();
    uri = uri.substring(uri.lastIndexOf("/") + 1);

    File cachefile = new File(cacheDir, uri);
    if (cachefile.exists() && cachefile.length() > 0) {
      try {
        response.sendRedirect(cacheUrl + "/" + uri);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return null;
    }


    MDC.put("requestId", "" + Math.round(Math.random() * 10000));

    RequestParser.CollectionWithSize cws = parser.processRequest(request.getRequestURI());

    response.addHeader("Content-Type", "audio/mpeg");
    response.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
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

    InputStream combinedInputStream = new CombinedInputStream(inputStreams);
    InputStream throttledCombinedInputStream = new ThrottledInputStream(new CombinedInputStream(inputStreams), throttle);

    String range = request.getHeader(HttpHeaders.RANGE);
    List<HttpRange> httpRanges;
    if (range != null) {
      httpRanges = HttpRange.parseRanges(range);
    } else {
      httpRanges = new ArrayList<>();
      httpRanges.add(HttpRange.createByteRange(0));
    }

    try {
      OutputStream os = new FileOutputStream(cachefile);
      IOUtils.copy(combinedInputStream, os);
      response.sendRedirect(cacheUrl + "/" + uri);
      return null;
    } catch (IOException e) {
      LOG.error("error saving the file to cache" + e);
      InputStream inputStream = (request.getRequestURI().contains("download")) ? combinedInputStream : throttledCombinedInputStream;
      return HttpRange.toResourceRegions(httpRanges, new InputStreamResource(inputStream) {
        @Override
        public long contentLength() throws IOException {
          return cws.size;
        }
      });
    }
  }

}
