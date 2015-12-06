package hu.tilos.radio.backend.bus;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.google.inject.Injector;
import hu.tilos.radio.backend.author.GetAuthorCommand;
import hu.tilos.radio.backend.author.GetAuthorHandler;
import hu.tilos.radio.backend.author.ListAuthorCommand;
import hu.tilos.radio.backend.author.ListAuthorHandler;
import scala.concurrent.Future;

import java.util.HashMap;
import java.util.Map;

public class MessageBus {

    private Map<Class, ActorRef> subscribers = new HashMap<>();

    private ActorSystem system;

    private Injector injector;

    public MessageBus(ActorSystem system, Injector injector) {
        this.system = system;
        this.injector = injector;
        addHandler(GetAuthorHandler.class, GetAuthorCommand.class);
        addHandler(ListAuthorHandler.class, ListAuthorCommand.class);
    }

    private void addHandler(Class<? extends Handler> handlerType, Class<? extends Command> commandType) {
        subscribers.put(commandType, system.actorOf(Props.create(GuiceIndirectAdapterActorProducer.class, injector, handlerType, commandType)));
    }

    public void ask(Command event) {
        subscribers.get(event.getClass()).tell(event, ActorRef.noSender());
    }

    public Future<Object> tell(Command event) {
        return Patterns.ask(subscribers.get(event.getClass()), event, 5000);
    }

}
