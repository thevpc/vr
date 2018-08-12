/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox;

/**
 *
 * @author vpc
 */
public class VrModule {

    private String groupId;
    private String baseArtifactId;
    private boolean model;
    private boolean service;
    private boolean web;
    private boolean theme;

    public VrModule(String groupId, String baseArtifactId,boolean model,boolean service,boolean web,boolean theme) {
        this.groupId = groupId;
        this.baseArtifactId = baseArtifactId;
        this.model = model;
        this.service = service;
        this.web = web;
        this.theme = theme;
    }

    
    public String getGroupId() {
        return groupId;
    }

    public String getBaseArtifactId() {
        return baseArtifactId;
    }

    public boolean isModel() {
        return model;
    }

    public boolean isService() {
        return service;
    }

    public boolean isWeb() {
        return web;
    }
    
    public boolean isTheme() {
        return theme;
    }
    

}
