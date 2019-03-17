package net.vpc.app.vr.core.toolbox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.vpc.app.nuts.NutsId;
import net.vpc.app.nuts.app.NutsApplication;
import net.vpc.app.nuts.app.NutsApplicationContext;
import net.vpc.app.vr.core.toolbox.util.IOUtils;
import net.vpc.app.vr.core.toolbox.util._StringUtils;
import net.vpc.common.commandline.Argument;
import net.vpc.common.commandline.CommandLine;

public class VrBoxMain extends NutsApplication {

    private static final Logger log = Logger.getLogger(VrBoxMain.class.getName());

    public static void main(String[] args) {
        new VrBoxMain().launchAndExit(args);
    }


    private ProjectConfig config;
    private FileSystemTemplater fileSystemTemplater;
    private NutsApplicationContext appContext;

    @Override
    public int launch(NutsApplicationContext appContext) {
        this.appContext = appContext;
        CommandLine cmd = new CommandLine(appContext);
        if (cmd.isExecMode()) {
            appContext.out().printf("==Vain Ruling Toolbox== v##%s##\n", appContext.getAppId().getVersion());
            appContext.out().printf("(c) Taha Ben Salah (==%s==) 2018 - ==%s==\n", "@vpc", "github.com/thevpc");
        }
        config = new ProjectConfig(new TemplateConsole() {
            @Override
            public void println(String message, Object... params) {
                appContext.out().printf(message + "\n", params);
            }

            @Override
            public String ask(String propName, String propertyTitle, StringValidator validator, String defaultValue) {
                String hints = null;
                if (validator != null) {
                    hints = validator.getHints();
                }
                while (true) {
                    appContext.out().printf("Resolving value for : [[%s]] (<<%s>>)\n", propertyTitle, propName);
                    if (hints != null) {
                        appContext.out().printf("\t<<hints>>       : "+hints+"\n");
                    }
                    String line = null;
                    if (defaultValue != null) {
                        line = appContext.terminal().readLine("\t<<Enter value>> (<<default is>>  : ==%s==) : ",defaultValue);
                    }else{
                        line = appContext.terminal().readLine("\t@@Enter value@@ : ");
                    }
                    if (line == null || line.length() == 0) {
                        if (defaultValue != null) {
                            return defaultValue;
                        }
                    }
                    if (validator != null) {
                        try {
                            return validator.validate(line);
                        } catch (Exception ex) {
                            appContext.out().printf("@@Invalid value@@ : "+ex.getMessage()+".\n");
                        }
                    } else {
                        return line;
                    }
                }
            }
        });
        config.getListeners().add(new VrProjectConfigListener(config));
        fileSystemTemplater = new FileSystemTemplater(config);
        config.setProperty("vrModuleName", "my-module", ValidatorFactory.NAME, "Module Artifact Id");
        config.setProperty("vrModuleVersion", "1.0.0", ValidatorFactory.VERSION, "Module Version");
        config.setProperty("vrProjectName", "my-project", ValidatorFactory.NAME, "Project Artifact Id");
        config.setProperty("vrProjectShortTitle", "my-project", ValidatorFactory.NAME, "Project Short Title");
        config.setProperty("vrProjectLongTitle", "my project name", ValidatorFactory.LABEL, "Project Title");
        config.setProperty("vrProjectVersion", "1.0.0", ValidatorFactory.VERSION, "Project Version");
        config.setProperty("vrProjectGroup", "com.mycompany", ValidatorFactory.GROUP, "Project Maven Group Id");
        config.setProperty("vrProjectRootFolder", System.getProperty("user.dir"), ValidatorFactory.FOLDER, "Project Root Folder");
        config.setProperty("vrConfigCompany", "My Company", ValidatorFactory.LABEL, "Project Default Company");
        config.setProperty("vrConfigCountry", "Tunisia", ValidatorFactory.LABEL, "Project Default Country");
        config.setProperty("vrConfigIndustry", "Services", ValidatorFactory.LABEL, "Project Default Industry");
        config.setProperty("vrConfigAuthor", System.getProperty("user.name"), ValidatorFactory.STRING, "Author (you)");
        config.setProperty("vrConfigAuthorUrl", "http://" + System.getProperty("user.name") + "-company.com", ValidatorFactory.URL, "Author Url (your website)");
        config.setProperty("vrConfigAuthorCompany", System.getProperty("user.name") + " company", ValidatorFactory.LABEL, "Author Company (your company)");
        config.setProperty("vrFwkCoreVersion", "1.13.13", ValidatorFactory.VERSION, "VR Framework Version");
        config.setProperty("vrPageMenuPath", "/MyMenu", ValidatorFactory.FOLDER, "Page Menu Path");
        config.setProperty("vrPageName", "my-page", ValidatorFactory.NAME, "Page Name");
        config.setProperty("vrConfigPublicTheme", "crew", ValidatorFactory.NAME, "Public/External Theme");
        config.setProperty("vrConfigPrivateTheme", "adminlte", ValidatorFactory.NAME, "Private/Admin Theme");
        config.setProperty("vrGeneratePageControllerExample", "true", ValidatorFactory.NAME, "Generate VR Page Controller Example");
        config.get("vrModuleName").setValue("main");
        //"vrEnableModule_"
        for (VrModule vrModule : VrModules.getAll()) {
            config.setProperty("vrEnableModule_" + vrModule.getBaseArtifactId(), "true", ValidatorFactory.BOOLEAN,
                    "Activate " + vrModule.getBaseArtifactId() + " Module (" + vrModule.getTitle() + ")"
            );

        }
        boolean ok = false;
        File storeTo = null;
        Argument a;
        int execCode = 0;
        String generate_project = null;
        String generate_module = null;
        String generate_page = null;
        while (cmd.hasNext()) {
            if (appContext.configure(cmd)) {

            } else if (
                    (a = cmd.readOption("--generate-project")) != null
                            || (a = cmd.readNonOption("generate-project")) != null
            ) {
                ok = true;
                try {
                    execCode = 0;
                    if (cmd.isExecMode()) {
                        createProject(storeTo);
                    }
                } catch (IOException ex) {
                    execCode = 1;
                    log.log(Level.SEVERE, null, ex);
                }
            } else if (
                    (a = cmd.readOption("--generate-module")) != null
                            || (a = cmd.readNonOption("generate-module")) != null
            ) {
                ok = true;
                try {
                    if (cmd.isExecMode()) {
                        createModule(null);
                    }
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            } else if (
                    (a = cmd.readOption("--generate-page")) != null
                            || (a = cmd.readNonOption("generate-page")) != null
            ) {
                ok = true;
                try {
                    if (cmd.isExecMode()) {
                        createModuleWebJsf_Page(null);
                    }
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            } else if ((a = cmd.readStringOption("--load-properties")) != null) {
                if (cmd.isExecMode()) {
                    getFileSystemTemplater().getConfig().loadProperties(new File(appContext.getWorkspace().getIOManager().expandPath(a.getStringValue())));
                }
            } else if ((a = cmd.readNonOption("load-properties")) != null) {
                String c = cmd.readNonOption().getStringExpression();
                if (cmd.isExecMode()) {
                    getFileSystemTemplater().getConfig().loadProperties(new File(appContext.getWorkspace().getIOManager().expandPath(c)));
                }
            } else if ((a = cmd.readStringOption("--store-properties")) != null) {
                if (cmd.isExecMode()) {
                    storeTo = new File(appContext.getWorkspace().getIOManager().expandPath(a.getStringValue()));
                }
            } else if ((a = cmd.readNonOption("store-properties")) != null) {
                String c = cmd.readNonOption().getStringExpression();
                if (cmd.isExecMode()) {
                    storeTo = new File(appContext.getWorkspace().getIOManager().expandPath(c));
                }
            } else {
                cmd.unexpectedArgument("vr-box");
            }
        }
        if (!ok) {
            try {
                execCode = 0;
                createProject(storeTo);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
                execCode = 1;
            }
        }
        return execCode;
    }


    public VrBoxMain() {
    }

    public void createAppService(String folder) throws IOException {
        getFileSystemTemplater().println("Generating maven project ==%s==...", "app service");
        getFileSystemTemplater().setTargetRoot(folder);
        getFileSystemTemplater().copyXml("app/project-app-service/pom.xml", "/");
        getFileSystemTemplater().copyXml("app/project-app-service/src/main/resources/META-INF/standalone-applicationContext.xml",
                "/src/main/resources/META-INF");
        getFileSystemTemplater().targetMkdirs("src/main/java");
        getFileSystemTemplater().copyProperties(
                "app/project-app-service/src/main/resources/META-INF/vr-app.version",
                "src/main/resources/META-INF"
        );
    }

    public void createAppWeb(String folder) throws IOException {
        getFileSystemTemplater().println("Generating maven project ==%s==...", "app web");
        getFileSystemTemplater().setTargetRoot(folder);
        getFileSystemTemplater().copyXml("app/project-app-web/pom.xml", "/");
        getFileSystemTemplater().targetMkdirs("src/main/java");

        //should ask for upa config params... ?
        getFileSystemTemplater().copyXml("app/project-app-web/src/main/resources/META-INF/upa.xml",
                "src/main/resources/META-INF"
        );

        getFileSystemTemplater().copyXml("app/project-app-web/src/main/webapp/META-INF/context.xml",
                "src/main/webapp/META-INF"
        );
        getFileSystemTemplater().copyXml("app/project-app-web/src/main/webapp/WEB-INF/web.xml",
                "src/main/webapp/WEB-INF"
        );
        getFileSystemTemplater().copyXml("app/project-app-web/src/main/webapp/WEB-INF/applicationContext.xml",
                "src/main/webapp/WEB-INF"
        );
        getFileSystemTemplater().copyXml(
                "app/project-app-web/src/main/webapp/WEB-INF/faces-config.xml",
                "src/main/webapp/WEB-INF"
        );
    }

    public void createProject(File storePropertiesTo) throws IOException {
        config.set("vrModuleName", "main");
        getFileSystemTemplater().println("Looking for latest VR version...");
        boolean newVersion = false;
        try {
            NutsId v = appContext.getWorkspace().createQuery().addId("net.vpc.app.vain-ruling.core:vr-core-service").setLatestVersions(true).findOne();
            if (v != null) {
                getFileSystemTemplater().println("Detected VR version ==%s==",v.getVersion());
                config.get("vrFwkCoreVersion").setDefaultValue(v.getVersion().getValue());
                newVersion = true;
            }
        } catch (Exception ex) {
            //ignore
        }
        if (!newVersion) {
            getFileSystemTemplater().println("Using base VR version " + config.get("vrFwkCoreVersion").getValue());
        }
        getFileSystemTemplater().println("Generating ==%s==...", "new VR project");
        getFileSystemTemplater().copyXml("pom.xml", "/");
        getFileSystemTemplater().targetMkdirs("app");
        getFileSystemTemplater().targetMkdirs("plugins");
//        String projectName = config.getProjectName();
        createAppService("/app/${vrProjectName}-app-service");
        createAppWeb("/app/${vrProjectName}-app-web");
        createModule("main");
//        config.unset("vrModuleName");
        createThemeFront();
        createThemeBack();
        getFileSystemTemplater().println("");
        getFileSystemTemplater().println("**--------------------------------**");
        getFileSystemTemplater().println("**PROJECT PROPERTIES**");
        getFileSystemTemplater().println("**--------------------------------**");
        String sortLines = _StringUtils.sortLines(
                IOUtils.toString(getFileSystemTemplater().getConfig().getProperties(), null)
        );
        getFileSystemTemplater().println(sortLines);
        if (storePropertiesTo != null) {
            IOUtils.writeString(sortLines, storePropertiesTo, getFileSystemTemplater().getConfig().getConsole());
        }
    }

    private FileSystemTemplater getFileSystemTemplater() {
        return fileSystemTemplater;
    }

    public void createThemeFront() {

    }

    public void createThemeBack() {

    }

    public void createModuleModel(String name) throws IOException {
        getFileSystemTemplater().println("Generating ==%s==...", "module model");
        config.set("vrModuleName", name);
        getFileSystemTemplater().setTargetRoot("/plugins/${vrModuleName}/${vrModuleName}-model");
        getFileSystemTemplater().copyXml("/plugins/module/module-model/pom.xml", "/");
        getFileSystemTemplater().targetMkdirs("src/main/java/${path(vrProjectGroup)}/${packageName(vrModuleName)}/model");
    }

    public void createModuleService(String name) throws IOException {
        getFileSystemTemplater().println("Generating ==%s==...", "module service");
        config.set("vrModuleName", name);
        getFileSystemTemplater().setTargetRoot("/plugins/${vrModuleName}/${vrModuleName}-service");
        getFileSystemTemplater().copyXml("/plugins/module/module-service/pom.xml", "/");
        getFileSystemTemplater().copyJava("/plugins/module/module-service/src/main/java/AppPlugin.java",
                "src/main/java"
        );
        getFileSystemTemplater().copyJava("/plugins/module/module-service/src/main/java/AppInterceptor.java",
                "src/main/java"
        );
        getFileSystemTemplater().copyProperties("plugins/module/module-service/src/main/resources/i18n/dictionary.properties",
                "src/main/resources/i18n"
        );
        getFileSystemTemplater().copyProperties("plugins/module/module-service/src/main/resources/i18n/service.properties",
                "src/main/resources/i18n/service.properties"
        );
        getFileSystemTemplater().targetMkdirs("src/main/java/");
    }

    public void createModuleWebJsf_Page(String name) throws IOException {
        config.set("vrPageName", name);
        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/META-INF/resources/modules/module/page.xhtml", "src/main/resources/META-INF/resources/modules/${vrModuleName}", "${vrPageName}.xhtml");
        getFileSystemTemplater().copyJava("/plugins/module/module-web/src/main/java/AppPageCtrl.java", "src/main/java");
        getFileSystemTemplater().targetAppendProperties("src/main/resources/i18n/presentation.properties",
                new String[]{
                        "Controller.${{varName(vrPageName)}}", "${{vrPageName}}",
                        "Package.${{pathTpPackage(vrPageMenuPath)}}", "Name for ${{pathTpPackage(vrPageMenuPath)}}"
                }
        );
    }

    public void createModuleWebJsf(String name) throws IOException {
        getFileSystemTemplater().println("Generating module ==%s==...", "web (jsf)");
        config.set("vrModuleName", name);
        getFileSystemTemplater().setTargetRoot("/plugins/${vrModuleName}/${vrModuleName}-web");
        getFileSystemTemplater().copyXml("/plugins/module/module-web/pom.xml", "/");
        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/META-INF/faces-config.xml", "src/main/resources/META-INF/");

        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/i18n/presentation.properties", "src/main/resources/i18n");
        getFileSystemTemplater().targetMkdirs("src/main/java/");
        if (config.get("vrGeneratePageControllerExample").getBoolean(true)) {
            createModuleWebJsf_Page(null);
        }
    }

    public void createModule(String name) throws IOException {
        getFileSystemTemplater().println("Generating ==%s==...", "new module");
        config.set("vrModuleName", name);
        getFileSystemTemplater().setTargetRoot("/plugins/${vrModuleName}");
        getFileSystemTemplater().copyXml("/plugins/module/pom.xml", "/");
        String moduleName = config.getModuleName();
        createModuleModel(moduleName);
        createModuleService(moduleName);
        createModuleWebJsf(moduleName);

        getFileSystemTemplater().setTargetRoot("/");
        getFileSystemTemplater().targetAddPomParentModule("pom.xml", "plugins/${vrModuleName}");
    }

}
