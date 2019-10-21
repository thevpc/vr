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
        reg("net.vpc.app.vain-ruling.plugins.themes", "vr-public-theme-crew", "Crew Public/External Theme", false, new String[]{"themes", "basic"});
        reg("net.vpc.app.vain-ruling.plugins.themes", "vr-private-theme-adminlte", "AdminLTE Private/Admin Theme", false, new String[]{"themes", "basic"});
        reg("net.vpc.app.vain-ruling.plugins.mailbox", "vr-mailbox", "Internal/External Email Support", false, new String[]{"exp"});
//        reg("net.vpc.app.vain-ruling.plugins.tasks", "vr-tasks","Kanban Task Dashboard");
//        reg("net.vpc.app.vain-ruling.plugins.forum", "vr-forum","Simple Forum Module");
        reg("net.vpc.app.vain-ruling.plugins.dev-toolbox", "vr-dev-toolbox", "Developer tools (SQL/UPQL queries ...)", true, new String[]{"basic"});

        reg("net.vpc.app.vain-ruling.plugins.academic", "vr-academic", "Education Module", true, new String[]{"edu"});
        reg("net.vpc.app.vain-ruling.plugins.academic-report", "vr-academic-report", "Document based collaboration extension", false, new String[]{"exp"});
        reg("net.vpc.app.vain-ruling.plugins.academic-planning", "vr-academic-planning", "Student/Teacher Plannings", true, new String[]{"edu"});
        reg("net.vpc.app.vain-ruling.plugins.academic-perf-eval", "vr-academic-perf-eval", "Courses Evaluation", true, new String[]{"edu"});
        reg("net.vpc.app.vain-ruling.plugins.academic-profile", "vr-academic-profile", "Teacher/Student Profiles/CV", true, new String[]{"edu"});
        reg("net.vpc.app.vain-ruling.plugins.academic-project-based-learning", "vr-academic-project-based-learning", "Innovation Projects Management", true, new String[]{"edu", "basic"});

        reg("net.vpc.app.vain-ruling.plugins.equipments", "vr-equipments", "Equipments Management", true, new String[]{"equip"});
//        reg("net.vpc.app.vain-ruling.plugins.equipment-tracker", "vr-equipment-tracker","Equipments Tracker",false);
    }

    public static void reg(String group, String name, String title, boolean prominent, String[] archetypes) {
        boolean service = true;
        boolean theme = false;
        if (group.endsWith(".themes")) {
            theme = true;
            service = false;
        }
        all.put(name, new VrModule(group, name, title, false, service, true, theme, prominent, archetypes));
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
                System.out.println("${{MavenModelDependency('" + value.getBaseArtifactId() + "')}}");
            }
        }
        System.out.println("");
        for (VrModule value : all.values()) {
            if (value.isService()) {
                System.out.println("${{MavenServiceDependency('" + value.getBaseArtifactId() + "')}}");
            }
        }
        System.out.println("");

        for (VrModule value : all.values()) {
            if (value.isWeb()) {
                System.out.println("${{MavenWebDependency('" + value.getBaseArtifactId() + "')}}");
            }
        }
    }
}
