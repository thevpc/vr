/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox;

import java.util.LinkedHashMap;

/**
 *
 * @author vpc
 */
public class VrModules {

    private static LinkedHashMap<String, VrModule> all = new LinkedHashMap<String, VrModule>();

    static {
        reg("net.vpc.app.vain-ruling.plugins.themes", "vr-public-theme-crew");
        reg("net.vpc.app.vain-ruling.plugins.themes", "vr-private-theme-adminlte");
        reg("net.vpc.app.vain-ruling.plugins.mailbox", "vr-mailbox");
        reg("net.vpc.app.vain-ruling.plugins.tasks", "vr-tasks");
        reg("net.vpc.app.vain-ruling.plugins.forum", "vr-forum");
        reg("net.vpc.app.vain-ruling.plugins.dev-toolbox", "vr-dev-toolbox");

        reg("net.vpc.app.vain-ruling.plugins.academic", "vr-academic");
        reg("net.vpc.app.vain-ruling.plugins.academic-report", "vr-academic-report");
        reg("net.vpc.app.vain-ruling.plugins.academic-planning", "vr-academic-planning");
        reg("net.vpc.app.vain-ruling.plugins.academic-perf-eval", "vr-academic-perf-eval");
        reg("net.vpc.app.vain-ruling.plugins.academic-profile", "vr-academic-profile");
        reg("net.vpc.app.vain-ruling.plugins.academic-project-based-learning", "vr-academic-project-based-learning");

        reg("net.vpc.app.vain-ruling.plugins.equipments", "vr-equipments");
        reg("net.vpc.app.vain-ruling.plugins.equipment-tracker", "vr-equipment-tracker");
    }

    public static void reg(String group, String name) {
        boolean service = true;
        boolean theme = false;
        if (group.endsWith(".themes")) {
            theme = true;
            service = false;
        }
        all.put(name, new VrModule(group, name, false, service, true, theme));
    }

    public static VrModule get(String name) {
        return all.get(name);
    }

    public static void main(String[] args) {
        for (VrModule value : all.values()) {
            if (value.isModel()) {
                System.out.println("${{vrMavenModelDependency('" + value.getBaseArtifactId() + "')}}");
            }
        }
        System.out.println("");
        for (VrModule value : all.values()) {
            if (value.isService()) {
                System.out.println("${{vrMavenServiceDependency('" + value.getBaseArtifactId() + "')}}");
            }
        }
        System.out.println("");

        for (VrModule value : all.values()) {
            if (value.isWeb()) {
                System.out.println("${{vrMavenWebDependency('" + value.getBaseArtifactId() + "')}}");
            }
        }
    }
}
