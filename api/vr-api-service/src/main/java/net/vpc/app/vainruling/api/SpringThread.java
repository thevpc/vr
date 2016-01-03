/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@Scope(value = "prototype",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SpringThread extends Thread{

    public SpringThread(Runnable target) {
        super(target);
    }
    
}
