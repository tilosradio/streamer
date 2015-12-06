package hu.tilos.radio.backend.bus;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import com.google.inject.Injector;

public class GuiceIndircetActorProducer implements IndirectActorProducer {

    private Injector injector;

    private Class<? extends Actor> type;

    public GuiceIndircetActorProducer(Injector injector, Class<? extends Actor> type) {
        this.injector = injector;
        this.type = type;
    }

    @Override
    public Actor produce() {
        return injector.getInstance(type);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return type;
    }
}
