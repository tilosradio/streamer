package hu.tilos.radio.backend.data.output;

import hu.tilos.radio.backend.data.types.EpisodeData;

public class ListenerStat {

   private EpisodeData episode;

    private int max;

    private int mean;

    private int min;

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public EpisodeData getEpisode() {
        return episode;
    }

    public void setEpisode(EpisodeData episode) {
        this.episode = episode;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMean() {
        return mean;
    }

    public void setMean(int mean) {
        this.mean = mean;
    }
}
