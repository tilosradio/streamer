package hu.tilos.radio.backend.text;

/**
 * Data transfer object for text data.
 */
public class TextData extends TextDataSimple {

    private String format;

    private String content;

    private String formatted;

    public TextData() {
    }

    public TextData(String content) {
        this.content = content;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }
}
