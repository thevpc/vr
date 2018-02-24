///*
// * To change this license header, choose License Headers in Project Properties.
// *
// * and open the template in the editor.
// */
//package net.vpc.app.vainruling.core.web.rest;
//
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.container.ContainerResponseContext;
//import javax.ws.rs.container.ContainerResponseFilter;
//import javax.ws.rs.ext.Provider;
//
///**
// * @author taha.bensalah@gmail.com
// */
//@Provider
//public class AppCrossOriginResourceSharingFilter implements ContainerResponseFilter {
//
//    @Override
//    public void filter(ContainerRequestContext requestContext, ContainerResponseContext response) {
//        response.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
//        response.getHeaders().putSingle("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
//        response.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type");
//    }
//
//}
