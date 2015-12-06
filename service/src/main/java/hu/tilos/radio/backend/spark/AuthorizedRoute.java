package hu.tilos.radio.backend.spark;

import hu.tilos.radio.backend.Session;
import spark.Request;
import spark.Response;

public interface AuthorizedRoute {

    Object handle(Request var1, Response var2, Session session) throws Exception;

}
