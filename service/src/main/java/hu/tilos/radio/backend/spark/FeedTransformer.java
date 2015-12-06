package hu.tilos.radio.backend.spark;

import net.anzix.jaxrs.atom.Entry;
import net.anzix.jaxrs.atom.Feed;
import spark.ResponseTransformer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashSet;

public class FeedTransformer implements ResponseTransformer {


    @Override
    public String render(Object model) throws Exception {
        Feed feed = (Feed) model;
        HashSet<Class> set = new HashSet<Class>();
        set.add(Feed.class);
        for (Entry entry : feed.getEntries()) {
            if (entry.getAnyOtherJAXBObject() != null) {
                set.add(entry.getAnyOtherJAXBObject().getClass());
            }
            if (entry.getContent() != null && entry.getContent().getJAXBObject() != null) {
                set.add(entry.getContent().getJAXBObject().getClass());
            }
        }
        try {
            JAXBContext ctx = JAXBContext.newInstance(Feed.class);
            Marshaller marshaller = ctx.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter w = new StringWriter();
            marshaller.marshal(model, w);
            return w.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal: ", e);
        }

    }
}
