package hu.tilos.radio.backend.netty;

import com.beust.jcommander.JCommander;

public class StreamerCli {

    public static void main(String[] args) {
        StreamerCli streamerCli = new StreamerCli();
        JCommander jCommander = new JCommander();

        ServeCommand serve = new ServeCommand();
        OverlapCommand overlap = new OverlapCommand();

        jCommander.addCommand("serve", serve);
        jCommander.addCommand("overlap", overlap);

        jCommander.parse(args);

        if ("overlap".equals(jCommander.getParsedCommand())) {
            overlap.run();
        } else {
            serve.run();
        }

    }


}
