package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "image", namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Image {

    private String href;

    public Image() {
    }

    public Image(String href) {
        this.href = href;
    }

    @XmlAttribute()
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
