package hu.tilos.radio.backend.episode;

import hu.tilos.radio.backend.data.types.EpisodeData;

import java.util.*;

public class Merger {

    public List<EpisodeData> merge(List<EpisodeData> first, List<EpisodeData> second) {
        List<EpisodeData> result = new ArrayList<>();
        result.addAll(first);
        result.addAll(second);
        Collections.sort(result, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData o1, EpisodeData o2) {
                int val = o1.getPlannedFrom().compareTo(o2.getPlannedFrom());
                if (val != 0) return val;
                if (o1.isPersistent() != o2.isPersistent()) {
                    if (o1.isPersistent()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }

                return 0;
            }
        });

        EpisodeData prev = null;
        Iterator<EpisodeData> it = result.iterator();
        while (it.hasNext()) {
            EpisodeData curr = it.next();
            if (prev != null && equalData(prev.getPlannedFrom(), curr.getPlannedFrom())) {
                it.remove();
            }
            prev = curr;
        }
        return result;
    }

    /**
     * Equal works even between Date and timestamp.
     *
     * @param plannedFrom1
     * @param plannedFrom2
     * @return
     */
    private boolean equalData(Date plannedFrom1, Date plannedFrom2) {
        return plannedFrom1.equals(plannedFrom2) || plannedFrom2.equals(plannedFrom1);
    }
}
