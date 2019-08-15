package net.vpc.app.vr.core.toolbox;

import java.io.File;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import net.vpc.app.nuts.NutsApplication;
import net.vpc.app.nuts.NutsApplicationContext;
import net.vpc.app.nuts.NutsCommandLine;
import net.vpc.app.nuts.NutsArgument;

public class VrBoxMain extends NutsApplication {

    private static final Logger LOG = Logger.getLogger(VrBoxMain.class.getName());
    public VrBoxProject project;

    public static void main(String[] args) {
        new VrBoxMain().runAndExit(args);
    }

    @Override
    public void run(NutsApplicationContext appContext) {
        this.project = new VrBoxProject(appContext);
        NutsCommandLine cmd = appContext.getCommandLine().setCommandName("vr-box");
        PrintStream out = appContext.session().out();

        if (cmd.isExecMode()) {
            out.printf("==Vain Ruling Toolbox== v##%s##\n", appContext.getAppId().getVersion());
            out.printf("(c) Taha Ben Salah (==%s==) 2018-2019 - ==%s==\n", "@vpc", "http://github.com/thevpc");
        }

        NutsArgument a;
        while (cmd.hasNext()) {
            if (appContext.configureFirst(cmd)) {

            } else if ((a = cmd.next("new", "n")) != null) {
                newProject(cmd);
                return;
            } else if ((a = cmd.next("generate module", "g m")) != null) {
                generateModule(cmd);
                return;
            } else if ((a = cmd.next("generate page", "g p")) != null) {
                generateWebJsf_Page(cmd);
                return;
            } else {
                cmd.unexpectedArgument();
            }
        }
        cmd.unexpectedArgument();
    }

    public VrBoxMain() {
    }

    public void generateWebJsf_Page(NutsCommandLine commandLine) throws UncheckedIOException {
        File loadPropertiesFrom = null;
        NutsArgument a;
        String pageName = null;
        while (commandLine.hasNext()) {
            if (project.getApplicationContext().configureFirst(commandLine)) {
                //
            } else if ((a = commandLine.nextString("--name")) != null) {
                pageName = a.getStringValue();
            } else if ((a = commandLine.nextString("--load")) != null) {
                loadPropertiesFrom = new File(a.getStringValue());
            } else {
                commandLine.unexpectedArgument();
            }
        }
        if (!commandLine.isExecMode()) {
            return;
        }
        if (loadPropertiesFrom != null) {
            project.loadConfigProperties(loadPropertiesFrom);
        }
        project.createModuleWebJsf_Page(pageName);
    }

    public void generateModule(NutsCommandLine commandLine) throws UncheckedIOException {
        File loadPropertiesFrom = null;
        NutsArgument a;
        String moduleName = null;
        while (commandLine.hasNext()) {
            if (project.getApplicationContext().configureFirst(commandLine)) {
                //
            } else if ((a = commandLine.nextString("--name")) != null) {
                moduleName = a.getStringValue();
            } else if ((a = commandLine.nextBoolean("--load")) != null) {
                loadPropertiesFrom = new File(a.getStringValue());
            } else if (commandLine.peek().isNonOption() && moduleName == null) {
                moduleName = commandLine.next().getString();
            } else {
                commandLine.unexpectedArgument();
            }
        }
        if (!commandLine.isExecMode()) {
            return;
        }
        if (loadPropertiesFrom != null) {
            project.loadConfigProperties(loadPropertiesFrom);
        }
        project.createModule(moduleName);
    }

    public void newProject(NutsCommandLine commandLine) {
        File storePropertiesTo = null;
        File loadPropertiesFrom = null;
        NutsArgument a;
        String projectName = null;
        Set<String> archetypes = new HashSet<String>();
        while (commandLine.hasNext()) {
            if (project.getApplicationContext().configureFirst(commandLine)) {
                //
            } else if ((a = commandLine.nextString("--name")) != null) {
                projectName = a.getStringValue();
            } else if ((a = commandLine.nextString("--save")) != null) {
                storePropertiesTo = new File(a.getStringValue());
            } else if ((a = commandLine.nextString("--load")) != null) {
                loadPropertiesFrom = new File(a.getStringValue());
            } else if ((a = commandLine.nextBoolean("--edu", "--equip", "--exp", "--all")) != null) {
                if (a.getBooleanValue()) {
                    archetypes.remove("all");
                    archetypes.remove("none");
                    archetypes.add("basic");
                    archetypes.add(a.getStringKey().substring(2));
                }
            } else if ((a = commandLine.nextBoolean("--all")) != null) {
                if (a.getBooleanValue()) {
                    archetypes.clear();
                    archetypes.add("all");
                }
            } else if ((a = commandLine.nextBoolean("--none")) != null) {
                if (a.getBooleanValue()) {
                    archetypes.clear();
                    archetypes.add("none");
                }
            } else if (commandLine.peek().isNonOption() && projectName == null) {
                projectName = commandLine.next().getString();
            } else {
                commandLine.unexpectedArgument();
            }
        }
        if (!commandLine.isExecMode()) {
            return;
        }
        project.createProject(projectName, storePropertiesTo, loadPropertiesFrom, archetypes.toArray(new String[0]));
    }

}
