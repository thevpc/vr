package net.vpc.app.vr.core.toolbox;

import net.vpc.app.vr.core.toolbox.util.JavaUtils;
import net.vpc.app.vr.core.toolbox.util.IOUtils;
import net.vpc.app.vr.core.toolbox.util.ClassInfo;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import net.vpc.common.strings.MessageNameFormat;
import net.vpc.common.strings.StringConverter;
import net.vpc.common.strings.StringToObject;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.strings.format.AbstractFunction;

public class FileSystemTemplater {

    private ProjectConfig config;
    private String toRoot = "/";
    private StringConverter dollar_converter = new StringConverter() {
        @Override
        public String convert(String str) {
            MessageNameFormat f = new MessageNameFormat("${" + str + "}");
            f.register("path", new AbstractFunction() {
                @Override
                public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                    return JavaUtils.path(String.valueOf(args[0]));
                }
            });
            f.register("packageName", new AbstractFunction() {
                @Override
                public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                    return JavaUtils.packageName(String.valueOf(args[0]));
                }
            });
            f.register("className", new AbstractFunction() {
                @Override
                public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                    return JavaUtils.className(String.valueOf(args[0]));
                }
            });
            f.register("varName", new AbstractFunction() {
                @Override
                public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                    return JavaUtils.varName(String.valueOf(args[0]));
                }
            });
            f.register("vrMavenModelDependency", new AbstractFunction() {
                @Override
                public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                    String module = String.valueOf(args[0]);
                    boolean ok = config.getBoolean("vrEnableModule_" + module, true);
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
            });
            f.register("vrMavenServiceDependency", new AbstractFunction() {
                @Override
                public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                    String module = String.valueOf(args[0]);
                    boolean ok = config.getBoolean("vrEnableModule_" + module, true);
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
            });
            f.register("vrMavenWebDependency", new AbstractFunction() {
                @Override
                public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                    String module = String.valueOf(args[0]);
                    boolean ok = config.getBoolean("vrEnableModule_" + module, true);
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
            });
            return f.format(new StringToObject() {
                @Override
                public Object toObject(String string) {
                    return config.get(string);
                }
            });
        }
    };

    public FileSystemTemplater(ProjectConfig config) {
        this.config = config;
    }

    public String convertFromPath(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("/META-INF/templates");
        if (!path.startsWith("/")) {
            sb.append("/");
        }
        sb.append(path);
        return sb.toString();
    }

    public File convertToPath(String path) {
        return new File(config.getProjectRootFolder(), StringUtils.replacePlaceHolders(toRoot + "/" + path, "${", "}", dollar_converter));
    }

    public void copyXml(String from, String toFolder) throws IOException {
        String n = IOUtils.extractFileName(from);
        String text = IOUtils.getTextResource(convertFromPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        IOUtils.writeString(converted, convertToPath(toFolder + "/" + n));
    }

    public void copyXml(String from, String toFolder, String newName) throws IOException {
        String text = IOUtils.getTextResource(convertFromPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        IOUtils.writeString(converted, convertToPath(toFolder + "/" + newName));
    }

    public void copyProperties(String from, String toFolder) throws IOException {
        String n = IOUtils.extractFileName(from);
        String text = IOUtils.getTextResource(convertFromPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        IOUtils.writeString(converted, convertToPath(toFolder + "/" + n));
    }

    public void appendProperty(String toFile, String name, String value) throws IOException {
        File okFile = convertToPath(toFile);
        Properties oldProperties = new Properties();
        oldProperties.load(new StringReader(IOUtils.getText(okFile)));
        String name2 = StringUtils.replacePlaceHolders(name, "${{", "}}", dollar_converter);
        String value2 = StringUtils.replacePlaceHolders(value, "${{", "}}", dollar_converter);
        if (!value2.equals(oldProperties.getProperty(name2))) {
            Properties p = new Properties();
            p.setProperty(name2, value2);
            StringWriter s = new StringWriter();
            p.store(s, "any");
            String[] all = s.getBuffer().toString().split("\n");
            StringBuilder finalV = new StringBuilder();
            for (int i = 0; i < all.length; i++) {
                if (finalV.length() > 0) {
                    finalV.append("\n");
                }
                if (!all[i].startsWith("#")) {
                    finalV.append(all[i]);
                }
            }
            IOUtils.writeStringAppend(finalV.toString(), okFile);
        }
    }

    public void copyJava(String from, String toFolder) throws IOException {
        String text = IOUtils.getTextResource(convertFromPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        ClassInfo c = JavaUtils.detectedClassInfo(converted);
        IOUtils.writeString(converted, new File(convertToPath(toFolder).getPath() + "/" + c.getFullClassName().replace('.', '/') + ".java"));
    }

    public String getToRoot() {
        return toRoot;
    }

    public void setToRoot(String toRoot) {
        this.toRoot = toRoot;
    }

    public void mkdirs(String path) {
        convertToPath(path).mkdirs();
    }

}
