package hu.tilos.radio.backend.converters;

import com.google.common.base.Throwables;
import org.owasp.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StrictHTMLSanitizer {

    private static final Logger LOG = LoggerFactory.getLogger(StrictHTMLSanitizer.class);

    public static final PolicyFactory POLICY_DEFINITION = new HtmlPolicyBuilder()
            .allowElements(
                    "br")
            .toFactory();


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
