package com.kevinlawler.twitterbot;

import com.kevinlawler.twitterbot.SplashResource;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.util.Map;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Configure Guice/Jersey
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    private static final String JERSEY_API_JSON_POJO_MAPPING_FEATURE = "com.sun.jersey.api.json.POJOMappingFeature";
    private static final Map<String, String> INIT_PARAMS;
    
    static {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put(JERSEY_API_JSON_POJO_MAPPING_FEATURE, "true");
        
        INIT_PARAMS = builder.build();
    }

    @Override
        protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {

                @Override
                    protected void configureServlets() {
 
                    TweetConfig.loadConfigFromFile();

                    /* bind the REST resources */
                    bind(SplashResource.class);

                    /* bind jackson converters for JAXB/JSON serialization */
                    bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
                    bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

                    /* Start the TweetReader (persistent, scheduler) */
                    TweetIssuer.getInstance().start();

                    serve("/rest/*").with(GuiceContainer.class, INIT_PARAMS);
                }
            });
    }
}
