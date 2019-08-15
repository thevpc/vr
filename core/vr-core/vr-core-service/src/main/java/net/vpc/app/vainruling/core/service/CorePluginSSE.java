/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
//Serverside Extension
@Service
public class CorePluginSSE {
    public static CorePluginSSE get() {
        return VrApp.getBean(CorePluginSSE.class);
    }
    
    
}
