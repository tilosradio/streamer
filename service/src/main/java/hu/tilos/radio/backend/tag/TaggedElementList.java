package hu.tilos.radio.backend.tag;

import java.util.ArrayList;
import java.util.List;

public class TaggedElementList {

    private List<TaggedEpisode> tagged = new ArrayList<>();

    public List<TaggedEpisode> getTagged() {
        return tagged;
    }

    public void setTagged(List<TaggedEpisode> tagged) {
        this.tagged = tagged;
    }

    public void add(TaggedEpisode map) {
        tagged.add(map);
    }
}
