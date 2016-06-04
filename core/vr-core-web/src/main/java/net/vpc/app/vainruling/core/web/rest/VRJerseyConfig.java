/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.rest;


import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
/**
 *
 * @author vpc
 */
public class VRJerseyConfig extends ResourceConfig {

    public VRJerseyConfig() {
        register(RequestContextFilter.class);
        packages("net.vpc.app");
        register(LoggingFilter.class);
    }
}
