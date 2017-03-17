package hu.tilos.streamer.controller;

import hu.tilos.streamer.*;
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
public class M3uController {

  private static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmm");

  @Autowired
  private RequestParser parser;


  @RequestMapping(value = "/{type:mp3|download}/tilos-{date:.*}.m3u")
  @ResponseBody
  public String generateM3u(HttpServletRequest request, HttpServletResponse response) {
    RequestParser.CollectionWithSize cws = parser.processRequest(request.getRequestURI());


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

}
