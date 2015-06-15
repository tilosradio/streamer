package hu.tilos.radio.backend.episode.util;

import hu.tilos.radio.backend.episode.EpisodeData;

import java.util.*;

public class Merger {

    public List<EpisodeData> merge(List<EpisodeData>... episodeLists) {
        List<EpisodeData> result = new ArrayList<>();
        for (List<EpisodeData> episodeList : episodeLists) {
            result.addAll(episodeList);
        }

        Collections.sort(result, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData o1, EpisodeData o2) {
                int val = o1.getPlannedFrom().compareTo(o2.getPlannedFrom());
                if (val != 0) return val;

                if (o1.isExtra() != o2.isExtra()) {
                    if (o1.isExtra()) {
                        return -1;
                    } else if (o2.isExtra()) {
                        return 1;
                    }
                }

                if (o1.isPersistent() != o2.isPersistent()) {
                    if (o1.isPersistent()) {
                        return -1;
                    }
                    if (o2.isPersistent()) {
                        return 1;
                    }
                }
                return 0;
            }
        });
        removeDuplicates(result);
        adjustTimes(result);

        return result;
    }

    public void adjustTimes(List<EpisodeData> result) {
        EpisodeData prev = null;
        Iterator<EpisodeData> it = result.iterator();
        while (it.hasNext()) {
            EpisodeData curr = it.next();
            if (prev != null) {
                if (prev.getPlannedTo() != null && curr.getPlannedFrom() != null && curr.getPlannedFrom().getTime() < prev.getPlannedTo().getTime()) {
                    if (prev.isExtra()) {
                        if (curr.getPlannedTo().getTime() <= prev.getPlannedTo().getTime()) {
                            it.remove();
                            //no prev=curr please
                            continue;
                        } else {
                            curr.setPlannedFrom(prev.getPlannedTo());
                        }
                    } else if (curr.isExtra()) {
                        prev.setPlannedTo(curr.getPlannedFrom());
                    }
                }
            }
            prev = curr;
        }
    }

    private void removeDuplicates(List<EpisodeData> result) {
        EpisodeData prev = null;
        Iterator<EpisodeData> it = result.iterator();
        while (it.hasNext()) {
            EpisodeData curr = it.next();
            if (prev != null && equalData(prev.getPlannedFrom(), curr.getPlannedFrom()) && equalData(prev.getPlannedTo(), curr.getPlannedTo())) {
                it.remove();
            }
            prev = curr;
        }
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
