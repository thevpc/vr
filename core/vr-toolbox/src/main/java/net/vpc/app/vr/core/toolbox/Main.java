package net.vpc.app.vr.core.toolbox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    private ProjectConfig config;
    private FileSystemTemplater fileSystemTemplater;

    public static void main(String[] args) {
        boolean ok = false;
        Main main = new Main();
        for (String arg : args) {
            if (arg.equals("--generate-project")) {
                ok = true;
                try {
                    main.createProject();
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
            } else {
                showSyntax();
                return;
            }
        }
        if (!ok) {
            try {
                main.createProject();
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void showSyntax() {
        System.out.println("--generate-project");
        System.out.println("--generate-module");
        System.out.println("--generate-page");
    }

    public Main() {
        config = new ProjectConfig();
        fileSystemTemplater = new FileSystemTemplater(config);
        config.setProperty("vrModuleName", "my-module", ValidatorFactory.ID);
        config.setProperty("vrModuleVersion", "1.0", ValidatorFactory.ID);
        config.setProperty("vrProjectName", "my-project", ValidatorFactory.ID);
        config.setProperty("vrProjectShortTitle", "my-project", ValidatorFactory.ID);
        config.setProperty("vrProjectVersion", "1.0", ValidatorFactory.VERSION);
        config.setProperty("vrProjectGroup", "com.mycompany", ValidatorFactory.GROUP);
        config.setProperty("vrProjectRootFolder", ".", ValidatorFactory.FOLDER);
        config.setProperty("vrConfigCompany", "My Company", ValidatorFactory.STRING);
        config.setProperty("vrConfigCountry", "Tunisia", ValidatorFactory.STRING);
        config.setProperty("vrConfigIndustry", "Services", ValidatorFactory.STRING);
        config.setProperty("vrConfigAuthor", System.getProperty("user.name"), ValidatorFactory.STRING);
        config.setProperty("vrFwkCoreVersion", "1.13.8", ValidatorFactory.STRING);
        config.setProperty("vrPageMenuPath", "/MyMenu", ValidatorFactory.FOLDER);
        config.setProperty("vrPageName", "my-page", ValidatorFactory.ID);
    }

    public void createAppService(String folder) throws IOException {
        getFileSystemTemplater().setToRoot(folder);
        getFileSystemTemplater().copyXml("app/project-app-service/pom.xml", "/");
        getFileSystemTemplater().copyXml("app/project-app-service/src/main/resources/META-INF/standalone-applicationContext.xml",
                "/src/main/resources/META-INF");
        getFileSystemTemplater().mkdirs("src/main/java");
        getFileSystemTemplater().copyProperties(
                "app/project-app-service/src/main/resources/META-INF/vr-app.version",
                "src/main/resources/META-INF"
        );
    }

    public void createAppWeb(String folder) throws IOException {
        getFileSystemTemplater().setToRoot(folder);
        getFileSystemTemplater().copyXml("app/project-app-web/pom.xml", "/");
        getFileSystemTemplater().mkdirs("src/main/java");

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

    public void createProject() throws IOException {
        config.set("vrModuleName", "main");
        getFileSystemTemplater().copyXml("pom.xml", "/");
        getFileSystemTemplater().mkdirs("app");
        getFileSystemTemplater().mkdirs("plugins");
//        String projectName = config.getProjectName();
        createAppService("/app/${vrProjectName}-app-service");
        createAppWeb("/app/${vrProjectName}-app-web");
        createModule("main");
//        config.unset("vrModuleName");
        createThemeFront();
        createThemeBack();
    }

    private FileSystemTemplater getFileSystemTemplater() {
        return fileSystemTemplater;
    }

    public void createThemeFront() {

    }

    public void createThemeBack() {

    }

    public void createModuleModel(String name) throws IOException {
        config.set("vrModuleName", "main");
        getFileSystemTemplater().setToRoot("/plugins/${vrModuleName}/${vrModuleName}-model");
        getFileSystemTemplater().copyXml("/plugins/module/module-model/pom.xml", "/");
        getFileSystemTemplater().mkdirs("src/main/java/${path(vrProjectGroup)}/${packageName(vrModuleName)}/model");
    }

    public void createModuleService(String name) throws IOException {
        config.set("vrModuleName", "main");
        getFileSystemTemplater().setToRoot("/plugins/${vrModuleName}/${vrModuleName}-service");
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
        getFileSystemTemplater().mkdirs("src/main/java/");
    }

    public void createModuleWebJsf_Page(String name) throws IOException {
        config.set("vrPageName", name);
        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/META-INF/resources/modules/module/page.xhtml", "src/main/resources/META-INF/resources/modules/${vrModuleName}", "${vrPageName}.xhtml");
        getFileSystemTemplater().copyJava("/plugins/module/module-web/src/main/java/AppPageCtrl.java", "src/main/java");
        getFileSystemTemplater().appendProperty("src/main/resources/i18n/presentation.properties", "Controller.${{varName(vrPageName)}}", "Action for ${{vrPageName}}");
    }

    public void createModuleWebJsf(String name) throws IOException {
        config.set("vrModuleName", name);
        getFileSystemTemplater().setToRoot("/plugins/${vrModuleName}/${vrModuleName}-web");
        getFileSystemTemplater().copyXml("/plugins/module/module-web/pom.xml", "/");
        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/META-INF/faces-config.xml", "src/main/resources/META-INF/");

        getFileSystemTemplater().copyXml("/plugins/module/module-web/src/main/resources/i18n/presentation.properties", "src/main/resources/i18n");
        getFileSystemTemplater().mkdirs("src/main/java/");
        if (config.getBoolean("vrGeneratePageControllerExample", true)) {
            createModuleWebJsf_Page(null);
        }
    }

    public void createModule(String name) throws IOException {
        config.set("vrModuleName", name);
        getFileSystemTemplater().setToRoot("/plugins/${vrModuleName}");
        getFileSystemTemplater().copyXml("/plugins/module/pom.xml", "/");

        createModuleModel(name);
        createModuleService(name);
        createModuleWebJsf(name);
    }
}
