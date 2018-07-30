package net.vpc.app.vr.core.toolbox;

import java.io.File;

public class Main {
    private ProjectConfig config = new ProjectConfig();
    private FileSystemTemplater fileSystemTemplater = new FileSystemTemplater();
    private IOTemplater io;

    public void createProject() {
        getFileSystemTemplater().copyXml("pom.xml");
        getFileSystemTemplater().mkdirs("app");
        getFileSystemTemplater().mkdirs("plugins");
//        String projectName = config.getProjectName();

        getFileSystemTemplater().copyXml("app/${projectName}-app-service/pom.xml");
        getFileSystemTemplater().copyXml("app/${projectName}-app-service/src/main/resources/META-INF/standalone-applicationContext.xml");
        getFileSystemTemplater().mkdirs("app/${projectName}-app-service/src/main/java");
        getFileSystemTemplater().copyProperties("app/${projectName}-app-service/src/main/resources/META-INF/vr-app.version");

        getFileSystemTemplater().copyXml("app/${projectName}-app-web/pom.xml");
        getFileSystemTemplater().mkdirs("app/${projectName}-app-web/src/main/java");

        //should ask for upa config params... ?
        getFileSystemTemplater().copyXml("app/${projectName}-app-web/src/main/resources/META-INF/upa.xml");

        getFileSystemTemplater().copyXml("app/${projectName}-app-web/src/main/webapp/META-INF/context.xml");
        getFileSystemTemplater().copyXml("app/${projectName}-app-web/src/main/webapp/WEB-INF/web.xml");
        getFileSystemTemplater().copyXml("app/${projectName}-app-web/src/main/webapp/WEB-INF/faces-config.xml");
        config.set("moduleName", "main");
        createModule();
        createThemeFront();
        createThemeBack();
    }

    private FileSystemTemplater getFileSystemTemplater() {
        File f = fileSystemTemplater.getRoot();
        if (f == null) {
            f = config.getProjectRootFolder();
            fileSystemTemplater.setRoot(f);
        }
        return fileSystemTemplater;
    }


    public void createThemeFront() {

    }

    public void createThemeBack() {

    }

    public void createModule() {
        getFileSystemTemplater().mkdirs("plugins/${moduleName}/${moduleName}-model/pom.xml");
        getFileSystemTemplater().mkdirs("plugins/${moduleName}/${moduleName}-model/src/main/java/");
        getFileSystemTemplater().mkdirs("plugins/${moduleName}/${moduleName}-model/src/main/java/${path(projectGroup)}/${packageName(moduleName)}/model");

        getFileSystemTemplater().mkdirs("plugins/${moduleName}/${moduleName}-service/pom.xml");
        getFileSystemTemplater().mkdirs("plugins/${moduleName}/${moduleName}-service/src/main/java/");
        getFileSystemTemplater().copyJava("plugins/${moduleName}/${moduleName}-service/src/main/java/${path(projectGroup)}/${packageName(moduleName)}/${className(moduleName)}Plugin");
        getFileSystemTemplater().copyJava("plugins/${moduleName}/${moduleName}-service/src/main/java/${path(projectGroup)}/${packageName(moduleName)}/${className(moduleName)}Interceptor");
        getFileSystemTemplater().copyProperties("plugins/${moduleName}/${moduleName}-service/src/main/resources/i18n/dictionary.properties");
        getFileSystemTemplater().copyProperties("plugins/${moduleName}/${moduleName}-service/src/main/resources/i18n/service.properties");
    }
}
