/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vr.core.toolbox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vpc
 */
public class VrModule {

    private String groupId;
    private String baseArtifactId;
    private String title;
    private boolean model;
    private boolean service;
    private boolean web;
    private boolean theme;
    private boolean prominent;
    private Set<String> archetypes;

    public VrModule(String groupId, String baseArtifactId, String title, boolean model,
            boolean service, boolean web, boolean theme,
            boolean prominent,
            String[] archetypes
    ) {
        this.groupId = groupId;
        this.baseArtifactId = baseArtifactId;
        this.model = model;
        this.service = service;
        this.web = web;
        this.theme = theme;
        this.title = title;
        this.prominent = prominent;
        this.archetypes = new HashSet<>(Arrays.asList(archetypes));
        this.archetypes.add("all");
    }

    public boolean acceptArchetype(String arch) {
        if (arch == null || arch.equals("all")) {
            return true;
        }
        return archetypes.contains(arch);
    }

    public boolean isProminent() {
        return prominent;
    }

    public String getTitle() {
        return title;
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
