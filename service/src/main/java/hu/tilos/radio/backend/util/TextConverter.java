package hu.tilos.radio.backend.util;

import hu.tilos.radio.backend.converters.FairEnoughHtmlSanitizer;
import hu.tilos.radio.backend.converters.HTMLSanitizer;
import hu.tilos.radio.backend.converters.TagUtil;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import javax.inject.Inject;

public class TextConverter {

    @Inject
    HTMLSanitizer liberalSanitizer;

    @Inject
    TagUtil tagUtil;

    @Inject
    FairEnoughHtmlSanitizer fairSanitizer;

    private PegDownProcessor pegdown;

    public TextConverter() {
        this.pegdown = new PegDownProcessor(Extensions.HARDWRAPS | Extensions.AUTOLINKS);
    }

    public String format(String type, String content) {
        if (type == null || content == null) {
            return content;
        }
        if (type.equals("default") || type.equals("legacy")) {
            return tagUtil.replaceToHtml(liberalSanitizer.clean(content));
        } else if (type.equals("markdown")) {
            content = fairSanitizer.clean(content);
            String tagged = tagUtil.htmlize(content);
            return pegdown.markdownToHtml(tagged);
        }
        throw new IllegalArgumentException("Unkown content type: " + type);
    }


}
