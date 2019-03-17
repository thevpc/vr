/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox;

import java.util.LinkedHashMap;

/**
 * @author vpc
 */
public class VrModules {

    private static LinkedHashMap<String, VrModule> all = new LinkedHashMap<String, VrModule>();

    static {
        reg("net.vpc.app.vain-ruling.plugins.themes", "vr-public-theme-crew","Crew Public/External Theme");
        reg("net.vpc.app.vain-ruling.plugins.themes", "vr-private-theme-adminlte","AdminLTE Private/Admin Theme");
        reg("net.vpc.app.vain-ruling.plugins.mailbox", "vr-mailbox","Internal/External Email Support");
        reg("net.vpc.app.vain-ruling.plugins.tasks", "vr-tasks","Kanban Task Dashboard");
        reg("net.vpc.app.vain-ruling.plugins.forum", "vr-forum","Simple Forum Module");
        reg("net.vpc.app.vain-ruling.plugins.dev-toolbox", "vr-dev-toolbox","Developer tools (SQL/UPQL queries ...)");

        reg("net.vpc.app.vain-ruling.plugins.academic", "vr-academic","Education Module");
        reg("net.vpc.app.vain-ruling.plugins.academic-report", "vr-academic-report","Document based collaboration extension");
        reg("net.vpc.app.vain-ruling.plugins.academic-planning", "vr-academic-planning","Student/Teacher Plannings");
        reg("net.vpc.app.vain-ruling.plugins.academic-perf-eval", "vr-academic-perf-eval","Courses Evaluation");
        reg("net.vpc.app.vain-ruling.plugins.academic-profile", "vr-academic-profile","Teacher/Student Profiles/CV");
        reg("net.vpc.app.vain-ruling.plugins.academic-project-based-learning", "vr-academic-project-based-learning","Innovation Projects Management");

        reg("net.vpc.app.vain-ruling.plugins.equipments", "vr-equipments","Equipments Management");
        reg("net.vpc.app.vain-ruling.plugins.equipment-tracker", "vr-equipment-tracker","Equipments Tracker");
    }

    public static void reg(String group, String name,String title) {
        boolean service = true;
        boolean theme = false;
        if (group.endsWith(".themes")) {
            theme = true;
            service = false;
        }
        all.put(name, new VrModule(group, name, title,false, service, true, theme));
    }

    public static VrModule[] getAll() {
        return all.values().toArray(new VrModule[0]);
    }

    public static VrModule get(String name) {
        return all.get(name);
    }

    public static void _main(String[] args) {
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
