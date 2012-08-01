package org.drools.mas.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class EndPointHelper {

    public static final String DEF_SRC = "/META-INF/service.endpoint.properties";

    public static String getEndPoint( String key ) {
        Properties props = new Properties();
        try {
            InputStream in = EndPointHelper.class.getResourceAsStream( DEF_SRC );
            if ( in != null ) {
                props.load( in );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (String) props.get( key );
    }

    public static URL getEndPointURL( String key ) {
        try {
            return new URL( getEndPoint( key ) );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
