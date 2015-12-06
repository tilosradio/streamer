package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Category {

    private String text;

    public Category() {
    }

    public Category(String text) {
        this.text = text;
    }

    @XmlAttribute(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
