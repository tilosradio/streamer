package hu.tilos.radio.backend.netty;

import hu.tilos.radio.backend.streamer.util.Mp3Joiner;

import java.io.File;

public class OverlapCommand implements Runnable {

    @com.beust.jcommander.Parameter(required = true, names = {"-f", "--first"})
    private File first;

    @com.beust.jcommander.Parameter(required = true, names = {"-s", "--second"})
    private File second;


    @Override
    public void run() {
        Mp3Joiner.OffsetDouble joinPositions = new Mp3Joiner().findJoinPositions(first, second);
        if (joinPositions == null) {
            System.out.println("Overlap has not been found");
        } else {
            System.out.println("Last frame of first file: " + joinPositions.firstEndOffset);
            System.out.println("First frame of second file " + joinPositions.secondStartOffset);
        }
    }
}
