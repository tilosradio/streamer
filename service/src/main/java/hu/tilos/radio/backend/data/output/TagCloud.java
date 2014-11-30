package hu.tilos.radio.backend.data.output;

import java.util.ArrayList;
import java.util.List;

public class TagCloud {

    private List<TagCloudElement> tags = new ArrayList<>();

    public List<TagCloudElement> getTags() {
        return tags;
    }

    public void setTags(List<TagCloudElement> tags) {
        this.tags = tags;
    }

    public void add(TagCloudElement element) {
        tags.add(element);
    }
}
