package net.anzix.jaxrs.atom;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("application/atom+*")
@Consumes("application/atom+*")
public class AtomFeedProvider implements MessageBodyReader<Feed>, MessageBodyWriter<Feed> {
    @Context
    protected Providers providers;


    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Feed.class.isAssignableFrom(type);
    }

    public Feed readFrom(Class<Feed> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {

        try {
            JAXBContext ctx = JAXBContext.newInstance(Feed.class);
            Feed feed = (Feed) ctx.createUnmarshaller().unmarshal(entityStream);
            return feed;
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshal: " + mediaType, e);
        }
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Feed.class.isAssignableFrom(type);
    }

    public long getSize(Feed feed, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Feed feed, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
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


            marshaller.marshal(feed, entityStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal: " + mediaType, e);
        }
    }
}
