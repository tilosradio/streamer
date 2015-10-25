package hu.tilos.radio.backend.bus;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.google.inject.Injector;

import javax.inject.Inject;

public class ServiceAdapterActor<SERVICE extends Handler,COMMAND extends Command> extends UntypedActor {

    @Inject
    Injector inject;

    private Handler<COMMAND> service;

    private Class<SERVICE> serviceType;

    private Class<COMMAND> commandType;

    public ServiceAdapterActor(Class<SERVICE> serviceType, Class<COMMAND> commandType) {
        this.serviceType = serviceType;
        this.commandType = commandType;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        service = inject.getInstance(serviceType);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (commandType.isAssignableFrom(message.getClass())) {
            getSender().tell(service.handle((COMMAND) message), ActorRef.noSender());
        }
    }
}
