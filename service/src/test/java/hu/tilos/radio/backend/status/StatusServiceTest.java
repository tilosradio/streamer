package hu.tilos.radio.backend.status;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class StatusServiceTest extends TestCase {
    @Test
    public void test(){
        List<String> liveSources = new StatusService().getLiveSources();

        System.out.println(liveSources);
    }

}