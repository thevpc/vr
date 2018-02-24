package net.vpc.app.vainruling.core.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class VrBootstrapComponent {

    @PostConstruct
    private void initialize(){
        VrApp.getBean(CorePlugin.class).prepare();
    }
}
