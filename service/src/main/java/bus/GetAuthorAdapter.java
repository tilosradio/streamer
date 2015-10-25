package bus;

import akka.actor.UntypedActor;
import hu.tilos.radio.backend.author.AuthorService;
import hu.tilos.radio.backend.author.GetAuthorCommand;

import javax.inject.Inject;

public class GetAuthorAdapter extends UntypedActor {

    @Inject
    private AuthorService service;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof GetAuthorCommand) {
            getSender().tell(service.get(((GetAuthorCommand) message).getIdOrAlias(), null), null);
        }

    }
}
