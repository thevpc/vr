/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.rest;

//import net.vpc.app.vainruling.core.web.jersey.mail.MailResource;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 *
 * @author vpc
 */
public class VRApplication extends ResourceConfig {

    public VRApplication() {
        register(RequestContextFilter.class);
        packages("net.vpc.app");
        register(LoggingFilter.class);
//        register(MailResource.class);
//        register(JAXBContextResolver.class);
//        register(JacksonFeature.class);
//        register(MultiPartFeature.class);
//        registerInstances(new LoggingFilter(Logger.getLogger(VRApplication.class.getName()), true));
    }

}
