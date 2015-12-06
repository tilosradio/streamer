package hu.tilos.radio.backend.bus;

import scala.util.Try;

public interface Handler<COMMAND extends Command> {
    public Try handle(COMMAND command);
}
