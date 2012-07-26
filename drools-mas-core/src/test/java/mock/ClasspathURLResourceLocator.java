/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mock;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.drools.builder.ResourceType;

/**
 *
 * @author esteban
 */
public class ClasspathURLResourceLocator implements Serializable{
    
    /**
     * patient name used for routing
     */
    private String name;
    
    private String URL;  
    private ResourceType resourceType;

    /** A {@link URLStreamHandler} that handles resources on the classpath. */
    public class ClasspathURLHandler extends URLStreamHandler {
        /** The classloader to find resources from. */
        private final ClassLoader classLoader;

        public ClasspathURLHandler() {
            this.classLoader = getClass().getClassLoader();
        }

        public ClasspathURLHandler(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            final URL resourceUrl = classLoader.getResource(u.getPath());
            return resourceUrl.openConnection();
        }
    }
    
    public ClasspathURLResourceLocator(String URL, ResourceType resourceType) {
        this.URL = URL;
        this.resourceType = resourceType;
    }

    public URL getURL() throws MalformedURLException {
        return new URL(null, URL, new ClasspathURLHandler());
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
