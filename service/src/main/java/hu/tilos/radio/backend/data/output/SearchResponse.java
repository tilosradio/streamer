package hu.tilos.radio.backend.data.output;

import hu.tilos.radio.backend.data.output.SearchResponseElement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class SearchResponse {

    private List<SearchResponseElement> elements = new ArrayList<SearchResponseElement>();

    public void addElement(SearchResponseElement e) {
        elements.add(e);
    }

    public List<SearchResponseElement> getElements() {
        return elements;
    }

    public void setElements(List<SearchResponseElement> elements) {
        this.elements = elements;
    }
}
