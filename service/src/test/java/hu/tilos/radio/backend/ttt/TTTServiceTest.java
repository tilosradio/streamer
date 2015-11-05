package hu.tilos.radio.backend.ttt;


import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TTTServiceTest {

    @Test
    public void getAllBusiness(){
        //given
        TTTService service = new TTTService(){
            @Override
            protected URL getUrl() throws MalformedURLException {
                return getClass().getResource("/ttt-businesses.json");
            }
        };

        //when
        Object[] allBusiness = service.getAllBusiness();

        //then
        assertEquals(32,allBusiness.length);
        
    }

}