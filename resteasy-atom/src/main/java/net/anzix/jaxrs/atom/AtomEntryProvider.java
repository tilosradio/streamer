package net.anzix.jaxrs.atom;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

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
public class AtomEntryProvider implements MessageBodyReader<Entry>, MessageBodyWriter<Entry> {
    @Context
    protected Providers providers;


    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Entry.class.isAssignableFrom(type);
    }

    public Entry readFrom(Class<Entry> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {


        try {
            JAXBContext ctx = null;
            Entry entry = (Entry) ctx.createUnmarshaller().unmarshal(entityStream);

            return entry;
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshal: " + mediaType, e);
        }
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Entry.class.isAssignableFrom(type);
    }

    public long getSize(Entry entry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Entry entry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        HashSet<Class> set = new HashSet<Class>();
        set.add(Entry.class);

        if (entry.getAnyOtherJAXBObject() != null) {
            set.add(entry.getAnyOtherJAXBObject().getClass());
        }
        if (entry.getContent() != null && entry.getContent().getJAXBObject() != null) {
            set.add(entry.getContent().getJAXBObject().getClass());
        }
        try {
//            JAXBContext ctx = finder.findCacheContext(mediaType, annotations, set.toArray(new Class[set.size()]));
            JAXBContext ctx = JAXBContext.newInstance(Entry.class);
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(entry, entityStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal: " + mediaType, e);
        }
    }
}