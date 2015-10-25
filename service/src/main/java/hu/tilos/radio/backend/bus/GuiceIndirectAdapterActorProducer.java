package hu.tilos.radio.backend.bus;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import com.google.inject.Injector;

public class GuiceIndirectAdapterActorProducer<SERVICE extends Handler, COMMAND extends Command> implements IndirectActorProducer {

    private Injector injector;

    private Class<SERVICE> serviceType;

    private Class<COMMAND> commandType;

    public GuiceIndirectAdapterActorProducer(Injector injector, Class<SERVICE> serviceType, Class<COMMAND> commandType) {
        this.injector = injector;
        this.serviceType = serviceType;
        this.commandType = commandType;
    }

    @Override
    public Actor produce() {
        ServiceAdapterActor serviceAdapterActor = new ServiceAdapterActor(serviceType, commandType);
        injector.injectMembers(serviceAdapterActor);
        return serviceAdapterActor;
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return ServiceAdapterActor.class;
    }
}
