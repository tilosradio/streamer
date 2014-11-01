package hu.tilos.radio.backend;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = super.getClasses();
        System.out.println(classes.size());
        for (Class clazz : classes){
            System.out.println(clazz);
        }
        return classes;
    }


}