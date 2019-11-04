/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package net.vpc.app.vr.core.toolbox;

import net.vpc.common.nuts.template.DefaultProjectTemplate;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.vpc.app.nuts.NutsApplicationContext;
import net.vpc.app.nuts.NutsId;
import net.vpc.common.nuts.template.IOUtils;
import net.vpc.common.nuts.template.JavaUtils;
import net.vpc.common.nuts.template._StringUtils;
import net.vpc.common.strings.MessageNameFormat;
import net.vpc.common.strings.StringToObject;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.strings.format.AbstractFunction;
import net.vpc.common.nuts.template.ProjectTemplate;
import net.vpc.common.nuts.template.ProjectTemplateListener;

/**
 *
 * @author vpc
 */
public class VrBoxProject extends DefaultProjectTemplate {

    private VrValidatorFactory vf;

    public VrBoxProject(NutsApplicationContext appContext) {
        super(appContext);
        this.vf = new VrValidatorFactory(appContext.getWorkspace());
        getConfigListeners().add(new ProjectTemplateListener() {
            @Override
            public void onSetProperty(String propertyName, String value, ProjectTemplate project) {
                switch (propertyName) {
                    case "ProjectName": {
                        project.getConfigProperty("ProjectShortTitle").setDefaultValue(StringUtils.isBlank(value) ? "my-project" : JavaUtils.toIdFormat(value));
                        project.getConfigProperty("ProjectLongTitle").setDefaultValue(StringUtils.isBlank(value) ? "My Project" : JavaUtils.toNameFormat(value));
                        break;
                    }
                    case "ConfigPublicTheme": {
                        project.setConfigValue("EnableModule_vr-public-theme-crew", "true");
                        break;
                    }
                    case "ConfigPrivateTheme": {
                        if ("adminlte".equals(value)) {
                            project.setConfigValue("EnableModule_vr-private-theme-adminlte", "true");
                        }
                        break;
                    }
                    case "EnableModule_vr-academic": {
                        if ("false".equals(value)) {
                            project.setConfigValue("EnableModule_vr-academic-report", value);
                            project.setConfigValue("EnableModule_vr-academic-planning", value);
                            project.setConfigValue("EnableModule_vr-academic-perf-eval", value);
                            project.setConfigValue("EnableModule_vr-academic-profile", value);
                            project.setConfigValue("EnableModule_vr-academic-project-based-learning", value);
                        }
                        break;
                    }
                    case "EnableModule_vr-equipments": {
                        if ("false".equals(value)) {
                            project.setConfigValue("EnableModule_vr-equipment-tracker", value);
                        }
                        break;
                    }
                }
            }
        });
        registerDefaultsFunctions();
        registerFunction("MavenModelDependency", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                String module = String.valueOf(args[0]);
                return vrMavenModelDependency(module, VrBoxProject.this);
            }
        });
        registerFunction("MavenServiceDependency", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                String module = String.valueOf(args[0]);
                return vrMavenServiceDependency(module, VrBoxProject.this);
            }
        });
        registerFunction("MavenWebDependency", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                String module = String.valueOf(args[0]);
                return vrMavenWebDependency(module, VrBoxProject.this);
            }
        });

        setConfigProperty("ModuleName", "my-module", vf.DASHED_NAME, "Module Artifact Id", true);
        setConfigProperty("ModuleVersion", "1.0.0", vf.VERSION, "Module Version", false);
        setConfigProperty("ProjectName", "my-project", vf.DASHED_NAME, "Project Artifact Id", true);
        setConfigProperty("ProjectShortTitle", "my-project", vf.NAME, "Project Short Title", false);
        setConfigProperty("ProjectLongTitle", "my project name", vf.LABEL, "Project Title", false);
        setConfigProperty("ProjectVersion", "1.0.0", vf.VERSION, "Project Version", false);
        setConfigProperty("ProjectGroup", "com.mycompany", vf.PACKAGE, "Project Maven Group Id", true);
        setConfigProperty("ProjectRootFolder", System.getProperty("user.dir"), vf.FOLDER, "Project Root Folder", true);
        setConfigProperty("ConfigCompany", "My Company", vf.LABEL, "Project Default Company", false);
        setConfigProperty("ConfigCountry", "Tunisia", vf.LABEL, "Project Default Country", false);
        setConfigProperty("ConfigIndustry", "Services", vf.LABEL, "Project Default Industry", false);
        setConfigProperty("ConfigAuthor", System.getProperty("user.name"), vf.STRING, "Author (you)", false);
        setConfigProperty("ConfigAuthorUrl", "http://" + System.getProperty("user.name") + "-company.com", vf.URL, "Author Url (your website)", false);
        setConfigProperty("ConfigAuthorCompany", System.getProperty("user.name") + " company", vf.LABEL, "Author Company (your company)", false);
        setConfigProperty("FwkCoreVersion", "1.13.16", vf.VERSION, "VR Framework Version", false);
        setConfigProperty("PageMenuPath", "/MyMenu", vf.MENU_PATH, "Page Menu Path", true);
        setConfigProperty("PageName", "my-page", vf.DASHED_NAME, "Page Name", true);
        setConfigProperty("ConfigPublicTheme", "crew", vf.DASHED_NAME, "Public/External Theme", false);
        setConfigProperty("ConfigPrivateTheme", "adminlte", vf.DASHED_NAME, "Private/Admin Theme", false);
        setConfigProperty("GeneratePageControllerExample", "true", vf.BOOLEAN, "Generate VR Page Controller Example", true);
        getConfigProperty("ModuleName").setValue("main");
        //"EnableModule_"
        for (VrModule vrModule : VrModules.getAll()) {
            if (isAskAll() || vrModule.isProminent()) {
                setConfigProperty("EnableModule_" + vrModule.getBaseArtifactId(), "true", vf.BOOLEAN,
                        "Activate " + vrModule.getBaseArtifactId() + " Module (" + vrModule.getTitle() + ")", true
                );
            }

        }
    }

//    public NutsApplicationContext getAppContext() {
//        return appContext;
//    }
//    @Override
//    public ProjectConfig getConfig() {
//        return config$;
//    }
    public void createAppService(String folder) throws UncheckedIOException {
        println("Generating maven project ==%s==...", "app service");
        setTargetRoot(folder);
        copyXml("app/project-app-service/pom.xml", "/");
        copyXml("app/project-app-service/src/main/resources/META-INF/standalone-applicationContext.xml",
                "/src/main/resources/META-INF");
        targetMkdirs("src/main/java");
        copyProperties(
                "app/project-app-service/src/main/resources/META-INF/vr-app.version",
                "src/main/resources/META-INF"
        );
    }

    public void createAppWeb(String folder) throws UncheckedIOException {
        println("Generating maven project ==%s==...", "app web");
        setTargetRoot(folder);
        copyXml("app/project-app-web/pom.xml", "/");
        targetMkdirs("src/main/java");

        //should ask for upa config params... ?
        copyXml("app/project-app-web/src/main/resources/META-INF/upa.xml",
                "src/main/resources/META-INF"
        );

        copyXml("app/project-app-web/src/main/webapp/META-INF/context.xml",
                "src/main/webapp/META-INF"
        );
        copyXml("app/project-app-web/src/main/webapp/WEB-INF/web.xml",
                "src/main/webapp/WEB-INF"
        );
        copyXml("app/project-app-web/src/main/webapp/WEB-INF/applicationContext.xml",
                "src/main/webapp/WEB-INF"
        );
        copyXml(
                "app/project-app-web/src/main/webapp/WEB-INF/faces-config.xml",
                "src/main/webapp/WEB-INF"
        );
    }

    public void createProject(String projectName, File storePropertiesTo, File loadPropertiesFrom, String[] archetypes) {
        Set<String> archetypesSet = archetypes == null ? new HashSet<>() : new HashSet<>(Arrays.asList(archetypes));
        if (loadPropertiesFrom != null) {
            loadConfigProperties(loadPropertiesFrom);
        }
        if (projectName != null) {
            final NutsId pn = getWorkspace().id().parseRequired(projectName);
            if (pn.getGroup() != null) {
                setConfigValue("ProjectGroup", pn.getGroup());
            }
            if (pn.getName() != null) {
                setConfigValue("ProjectName", pn.getName());
            }
            if (pn.getVersion().getValue() != null) {
                setConfigValue("ProjectVersion", pn.getVersion().getValue());
            }
        }
        if (!archetypesSet.isEmpty()) {
            for (VrModule vrModule : VrModules.getAll()) {
                setConfigValue("EnableModule_" + vrModule.getBaseArtifactId(), "false");
                if (!archetypesSet.contains("none")) {
                    for (String archetype : archetypes) {
                        if (vrModule.acceptArchetype(archetype)) {
                            setConfigValue("EnableModule_" + vrModule.getBaseArtifactId(), "true");
                        }
                    }
                }
            }
        }
        setConfigValue("ModuleName", "main");
        println("Looking for latest VR version...");
        boolean newVersion = false;
        try {
            NutsId v = getWorkspace().search().id("net.vpc.app.vain-ruling.core:vr-core-service")
                    .latest().getResultIds().required();
            if (v != null) {
                println("Detected VR version ==%s==", v.getVersion());
                getConfigProperty("FwkCoreVersion").setDefaultValue(v.getVersion().getValue());
                newVersion = true;
            }
        } catch (Exception ex) {
            //ignore
        }
        if (!newVersion) {
            println("Using base VR version " + getConfigProperty("FwkCoreVersion").getValue());
        }
        println("Generating ==%s==...", "new VR project");
        copyXml("pom.xml", "/");
        targetMkdirs("app");
        targetMkdirs("plugins");
//        String projectName = config.getProjectName();
        createAppService("/app/${ProjectName}-app-service");
        createAppWeb("/app/${ProjectName}-app-web");
        createModule("main");
//        config.unset("ModuleName");
        createThemeFront();
        createThemeBack();
        println("");
        println("**--------------------------------**");
        println("**PROJECT PROPERTIES**");
        println("**--------------------------------**");
        String sortLines = _StringUtils.sortLines(
                IOUtils.toString(getConfigProperties(), null)
        );
        println(sortLines);
        if (storePropertiesTo != null) {
            try {
                IOUtils.writeString(sortLines, storePropertiesTo, this);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    public void createThemeFront() {

    }

    public void createThemeBack() {

    }

    public void createModuleModel(String name) throws UncheckedIOException {
        println("Generating ==%s==...", "module model");
        setConfigValue("ModuleName", name);
        setTargetRoot("/plugins/${ModuleName}/${ModuleName}-model");
        copyXml("/plugins/module/module-model/pom.xml", "/");
        targetMkdirs("src/main/java/${path(ProjectGroup)}/${packageName(ModuleName)}/model");
    }

    public void createModuleService(String name) throws UncheckedIOException {
        println("Generating ==%s==...", "module service");
        setConfigValue("ModuleName", name);
        setTargetRoot("/plugins/${ModuleName}/${ModuleName}-service");
        copyXml("/plugins/module/module-service/pom.xml", "/");
        copyJava("/plugins/module/module-service/src/main/java/AppPlugin.java",
                "src/main/java"
        );
        copyJava("/plugins/module/module-service/src/main/java/AppInterceptor.java",
                "src/main/java"
        );
        copyProperties("plugins/module/module-service/src/main/resources/i18n/dictionary.properties",
                "src/main/resources/i18n"
        );
        copyProperties("plugins/module/module-service/src/main/resources/i18n/service.properties",
                "src/main/resources/i18n/service.properties"
        );
        targetMkdirs("src/main/java/");
    }

    public void createModuleWebJsf_Page(String name) throws UncheckedIOException {
        setConfigValue("PageName", name);
        copyXml("/plugins/module/module-web/src/main/resources/META-INF/resources/modules/module/page.xhtml", "src/main/resources/META-INF/resources/modules/${ModuleName}", "${PageName}.xhtml");
        copyJava("/plugins/module/module-web/src/main/java/AppPageCtrl.java", "src/main/java");
        targetAppendProperties("src/main/resources/i18n/presentation.properties",
                new String[]{
                    "Controller.${{varName(PageName)}}", "${{PageName}}",
                    "Package.${{pathToPackage(PageMenuPath)}}", "Name for ${{pathToPackage(PageMenuPath)}}"
                }
        );
    }

    public void createModuleWebJsf(String name) throws UncheckedIOException {
        println("Generating module ==%s==...", "web (jsf)");
        setConfigValue("ModuleName", name);
        setTargetRoot("/plugins/${ModuleName}/${ModuleName}-web");
        copyXml("/plugins/module/module-web/pom.xml", "/");
        copyXml("/plugins/module/module-web/src/main/resources/META-INF/faces-config.xml", "src/main/resources/META-INF/");

        copyXml("/plugins/module/module-web/src/main/resources/i18n/presentation.properties", "src/main/resources/i18n");
        targetMkdirs("src/main/java/");
        if (getConfigProperty("GeneratePageControllerExample").getBoolean(true)) {
            createModuleWebJsf_Page(null);
        }
    }

    public void createModule(String name) throws UncheckedIOException {
        println("Generating ==%s==...", "new module");
        setConfigValue("ModuleName", name);
        setTargetRoot("/plugins/${ModuleName}");
        copyXml("/plugins/module/pom.xml", "/");
        String moduleName = getModuleName();
        createModuleModel(moduleName);
        createModuleService(moduleName);
        createModuleWebJsf(moduleName);

        setTargetRoot("/");
        targetAddPomParentModule("pom.xml", "plugins/${ModuleName}");
    }

    public static String vrMavenModelDependency(String module, ProjectTemplate config) {
        boolean ok = config.getConfigProperty("EnableModule_" + module).getBoolean(true);
        if (ok) {
            VrModule mm = VrModules.get(module);
            if (mm != null && mm.isModel()) {
                return "<dependency>\n"
                        + "            <groupId>" + mm.getGroupId() + "</groupId>\n"
                        + "            <artifactId>" + mm.getBaseArtifactId() + "-model</artifactId>\n"
                        + "            <version>${version.vr}</version>\n"
                        + "        </dependency>";
            }
        }
        return "";
    }

    public static String vrMavenServiceDependency(String module, ProjectTemplate config) {
        boolean ok = config.getConfigProperty("EnableModule_" + module).getBoolean(true);
        if (ok) {
            VrModule mm = VrModules.get(module);
            if (mm != null && mm.isService()) {
                return "<dependency>\n"
                        + "            <groupId>" + mm.getGroupId() + "</groupId>\n"
                        + "            <artifactId>" + mm.getBaseArtifactId() + "-service</artifactId>\n"
                        + "            <version>${version.vr}</version>\n"
                        + "        </dependency>";
            }
        }
        return "";
    }

    public static String vrMavenWebDependency(String module, ProjectTemplate config) {
        boolean ok = config.getConfigProperty("EnableModule_" + module).getBoolean(true);
        if (ok) {
            VrModule mm = VrModules.get(module);
            if (mm != null && mm.isWeb()) {
                return "<dependency>\n"
                        + "            <groupId>" + mm.getGroupId() + "</groupId>\n"
                        + "            <artifactId>" + mm.getBaseArtifactId() + (mm.isTheme() ? "" : "-web") + "</artifactId>\n"
                        + "            <version>${version.vr}</version>\n"
                        + "        </dependency>";
            }
        }
        return "";
    }

}
