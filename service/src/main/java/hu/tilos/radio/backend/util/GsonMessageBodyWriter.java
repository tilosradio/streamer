package hu.tilos.radio.backend.util;

import com.google.gson.Gson;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
public class GsonMessageBodyWriter implements MessageBodyWriter {

    @Context
    Providers ps;

    Gson gson = new Gson();

    @Override
    public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object value, Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return gson.toJson(value).length();
    }

    @Override
    public void writeTo(Object value, Class aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        outputStream.write(gson.toJson(value).getBytes());

    }

}
