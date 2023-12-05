package main;

import main.filter.Authenticate;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

//@ApplicationPath("/")
public class ResourceConfiguration extends ResourceConfig {
public ResourceConfiguration(){
    packages("main.api");
    register(Authenticate.class);
    register(RolesAllowedDynamicFeature.class);
}
}
