package net.vpc.app.vr.core.toolbox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.nuts.Nuts;
import net.vpc.app.nuts.NutsId;
import net.vpc.app.nuts.NutsSearch;
import net.vpc.app.nuts.NutsWorkspace;
import net.vpc.app.vr.core.toolbox.util.IOUtils;
import net.vpc.app.vr.core.toolbox.util._StringUtils;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    private ProjectConfig config;
    private FileSystemTemplater fileSystemTemplater;

    public static void main(String[] args) {
        System.out.println("Vain Ruling Toolbox v" + IOUtils.getArtifactVersionOrDev());
        System.out.println("(c) Taha Ben Salah (@vpc) 2018 - github.com/thevpc");
        boolean ok = false;
        Main main = new Main();
        File storeTo = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--generate-project")) {
                ok = true;
                try {
                    main.createProject(storeTo);
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            } else if (arg.equals("--generate-module")) {
                ok = true;
                try {
                    main.createModule(null);
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            } else if (arg.equals("--generate-page")) {
                ok = true;
                try {
                    main.createModuleWebJsf_Page(null);
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            } else if (arg.equals("--load-properties")) {
                i++;
                main.getFileSystemTemplater().getConfig().loadProperties(new File(args[i]));
            } else if (arg.equals("--store-properties")) {
                i++;
                storeTo = new File(args[i]);
            } else {
                showSyntax();
                return;
            }
        }
        if (!ok) {
            try {
                main.createProject(storeTo);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void showSyntax() {
        System.out.println("Available commandes are : ");
        System.out.println("--generate-project");
        System.out.println("--generate-module");
        System.out.println("--generate-page");
        System.out.println("--load-properties <FILE>");
        System.out.println("--store-properties <FILE>");
    }

    public Main() {
        config = new ProjectConfig();
        fileSystemTemplater = new FileSystemTemplater(config);
        config.setProperty("vrModuleName", "my-module", ValidatorFactory.NAME);
        config.setProperty("vrModuleVersion", "1.0", ValidatorFactory.VERSION);
        config.setProperty("vrProjectName", "my-project", ValidatorFactory.NAME);
        config.setProperty("vrProjectShortTitle", "my-project", ValidatorFactory.LABEL);
        config.setProperty("vrProjectLongTitle", "my project name", ValidatorFactory.LABEL);
        config.setProperty("vrProjectVersion", "1.0", ValidatorFactory.VERSION);
        config.setProperty("vrProjectGroup", "com.mycompany", ValidatorFactory.GROUP);
        config.setProperty("vrProjectRootFolder", ".", ValidatorFactory.FOLDER);
        config.setProperty("vrConfigCompany", "My Company", ValidatorFactory.LABEL);
        config.setProperty("vrConfigCountry", "Tunisia", ValidatorFactory.LABEL);
        config.setProperty("vrConfigIndustry", "Services", ValidatorFactory.LABEL);
        config.setProperty("vrConfigAuthor", System.getProperty("user.name"), ValidatorFactory.STRING);
        config.setProperty("vrConfigAuthorUrl", "http://" + System.getProperty("user.name") + "-company.com", ValidatorFactory.URL);
        config.setProperty("vrConfigAuthorCompany", System.getProperty("user.name") + " company", ValidatorFactory.LABEL);
        config.setProperty("vrFwkCoreVersion", "1.13.10", ValidatorFactory.VERSION);
        config.setProperty("vrPageMenuPath", "/MyMenu", ValidatorFactory.FOLDER);
        config.setProperty("vrPageName", "my-page", ValidatorFactory.NAME);
        config.setProperty("vrConfigPublicTheme", "crew", ValidatorFactory.NAME);
        config.setProperty("vrConfigPrivateTheme", "crew", ValidatorFactory.NAME);
    }

    public void createAppService(String folder) throws IOException {
        getFileSystemTemplater().println("Generating project app service...");
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
        getFileSystemTemplater().println("Generating project app web...");
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
        NutsWorkspace w = Nuts.openWorkspace();
        boolean newVersion = false;
        try {
            NutsId v = w.findOne(new NutsSearch("net.vpc.app.vain-ruling.core:vr-core-service").setLastestVersions(true), null);
            if (v != null) {
                getFileSystemTemplater().println("Detected VR version " + v.getVersion());
                config.setProperty("vrFwkCoreVersion", v.getVersion().getValue(), ValidatorFactory.VERSION);
                newVersion = true;
            }
        } catch (Exception ex) {
            //ignore
        }
        if (!newVersion) {
            getFileSystemTemplater().println("Using base VR version " + config.getProperties(true).getProperty("vrFwkCoreVersion"));
        }
        getFileSystemTemplater().println("Generating new project project...");
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
        getFileSystemTemplater().println("--------------------------------");
        getFileSystemTemplater().println("PROJECT PROPERTIES");
        getFileSystemTemplater().println("--------------------------------");
        String sortLines = _StringUtils.sortLines(
                IOUtils.toString(getFileSystemTemplater().getConfig().getProperties(false), null)
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
        getFileSystemTemplater().println("Generating module model...");
        config.set("vrModuleName", "main");
        getFileSystemTemplater().setTargetRoot("/plugins/${vrModuleName}/${vrModuleName}-model");
        getFileSystemTemplater().copyXml("/plugins/module/module-model/pom.xml", "/");
        getFileSystemTemplater().targetMkdirs("src/main/java/${path(vrProjectGroup)}/${packageName(vrModuleName)}/model");
    }

    public void createModuleService(String name) throws IOException {
        getFileSystemTemplater().println("Generating module service...");
        config.set("vrModuleName", "main");
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
        getFileSystemTemplater().println("Generating module we (jsf)...");
        config.set("vrModuleName", name);
        getFileSystemTemplater().setTargetRoot("/plugins/${vrModuleName}/${vrModuleName}-web");
        getFileSystemTemplater().copyXml("/plugins/module/module-web/pom.xml", "/");
        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/META-INF/faces-config.xml", "src/main/resources/META-INF/");

        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/i18n/presentation.properties", "src/main/resources/i18n");
        getFileSystemTemplater().targetMkdirs("src/main/java/");
        if (config.getBoolean("vrGeneratePageControllerExample", true)) {
            createModuleWebJsf_Page(null);
        }
    }

    public void createModule(String name) throws IOException {
        getFileSystemTemplater().println("Generating new module...");
        config.set("vrModuleName", name);
        getFileSystemTemplater().setTargetRoot("/plugins/${vrModuleName}");
        getFileSystemTemplater().copyXml("/plugins/module/pom.xml", "/");

        createModuleModel(name);
        createModuleService(name);
        createModuleWebJsf(name);

        getFileSystemTemplater().setTargetRoot("/");
        getFileSystemTemplater().targetAddPomParentModule("pom.xml", "plugins/${vrModuleName}");
    }
}
