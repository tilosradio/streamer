package hu.tilos.radio.backend.converters;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import org.owasp.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class FairEnoughHtmlSanitizer {

    private static final Logger LOG = LoggerFactory.getLogger(FairEnoughHtmlSanitizer.class);

    private static final Pattern PARAGRAPH = Pattern.compile("(?:[\\p{L}\\p{N},'\\.\\s\\-_\\(\\)]|&[0-9]{2};)*");

    private static final Pattern ONSITE_URL = Pattern.compile("(?:[\\p{L}\\p{N}\\\\\\.\\#@\\$%\\+&;\\-_~,\\?=/!]+|\\#(\\w)+)");

    private static final Pattern OFFSITE_URL = Pattern.compile("\\s*(?:(?:ht|f)tps?://|mailto:)[\\p{L}\\p{N}]"
            + "[\\p{L}\\p{N}\\p{Zs}\\.\\#@\\$%\\+&;:\\-_~,\\?=/!\\(\\)]*+\\s*");

    private static final Pattern NUMBER = Pattern.compile("[+-]?(?:(?:[0-9]+(?:\\.[0-9]*)?)|\\.[0-9]+)");

    private static final Pattern NAME = Pattern.compile("[a-zA-Z0-9\\-_\\$]+");

    private static final Pattern ALIGN = Pattern.compile("(?i)center|left|right|justify|char");

    private static final Pattern YOUTUBE = Pattern.compile("^(?:https?:)?(//)?(?:www\\.)?(?:youtube\\.com|youtu\\.be).*");

    private static final Pattern NUMBER_OR_PERCENT = Pattern.compile("[0-9]+%?");


    private static final Predicate<String> ONSITE_OR_OFFSITE_URL = new Predicate<String>() {
        public boolean apply(String s) {
            return ONSITE_URL.matcher(s).matches() || OFFSITE_URL.matcher(s).matches();
        }
    };

    public static final PolicyFactory POLICY_DEFINITION = new HtmlPolicyBuilder()
            .allowAttributes("align").matching(ALIGN).onElements("p")
            .allowAttributes("href").matching(ONSITE_OR_OFFSITE_URL).onElements("a").allowStandardUrlProtocols()
            .allowAttributes("nohref").onElements("a")
            .allowAttributes("name").matching(NAME).onElements("a")
            .allowAttributes("src").matching(ONSITE_OR_OFFSITE_URL).onElements("img")
            .allowAttributes("name").matching(NAME).onElements("img")
            .allowAttributes("alt").matching(PARAGRAPH).onElements("img")
            .allowAttributes("colspan", "rowspan").matching(NUMBER).onElements("iframe")
            .allowAttributes("src").matching(YOUTUBE).onElements("iframe")
            .allowAttributes("height", "width").matching(NUMBER_OR_PERCENT).onElements("iframe")
            .allowAttributes("border", "cellpadding", "cellspacing").matching(NUMBER).onElements("table")
            .allowElements(
                    "a", "pre", "code",
                    "cite", "samp", "sub", "sup", "strike", "center", "blockquote",
                    "hr", "br", "img")
            .toFactory();

    public FairEnoughHtmlSanitizer() {

    }

    public static void main(String[] args) throws IOException {
        if (args.length != 0) {
            System.err.println("Reads from STDIN and writes to STDOUT");
            System.exit(-1);
        }
        System.err.println("[Reading from STDIN]");
        // Fetch the HTML to sanitize.
        String html = CharStreams.toString(
                new InputStreamReader(System.in, Charsets.UTF_8));
        // Set up an output channel to receive the sanitized HTML.

    }

    public String clean(String html) {
        try {
            StringBuilder output = new StringBuilder();
            HtmlStreamRenderer renderer = HtmlStreamRenderer.create(
                    output,
                    // Receives notifications on a failure to write to the output.
                    new Handler<IOException>() {
                        public void handle(IOException ex) {
                            Throwables.propagate(ex);  // System.out suppresses IOExceptions
                        }
                    },
                    // Our HTML parser is very lenient, but this receives notifications on
                    // truly bizarre inputs.
                    new Handler<String>() {
                        public void handle(String x) {
                            throw new AssertionError(x);
                        }
                    });
            HtmlSanitizer.sanitize(html, POLICY_DEFINITION.apply(renderer));
            return output.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Can't sanitize HTML\n" + html, ex);
        }
    }


}
