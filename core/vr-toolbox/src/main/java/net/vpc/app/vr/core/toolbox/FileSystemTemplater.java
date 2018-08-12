package net.vpc.app.vr.core.toolbox;

import net.vpc.app.vr.core.toolbox.util.JavaUtils;
import net.vpc.app.vr.core.toolbox.util.IOUtils;
import net.vpc.app.vr.core.toolbox.util.ClassInfo;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import net.vpc.app.vr.core.toolbox.util.XmlUtils;
import net.vpc.common.strings.StringConverter;
import net.vpc.common.strings.StringUtils;
import org.w3c.dom.Document;

public class FileSystemTemplater {

    private ProjectConfig config;
    private String targetRoot = "/";
    private StringConverter dollar_converter = new StringConverter() {
        @Override
        public String convert(String str) {
            return TemplateExprEvaluator.eval(str, FileSystemTemplater.this);
        }
    };

    public void println(String message) {
        config.getConsole().println(replacePlaceHoldersSimple(message));
    }

    public ProjectConfig getConfig() {
        return config;
    }

    public FileSystemTemplater(ProjectConfig config) {
        this.config = config;
    }

    public static String sourceConvertPath(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("/META-INF/templates");
        if (!path.startsWith("/")) {
            sb.append("/");
        }
        sb.append(path);
        return sb.toString();
    }

    public String replacePlaceHoldersSimple(String path) {
        return StringUtils.replacePlaceHolders(path, "${", "}", dollar_converter);
    }

    public File convertToPath(String path) {
        return new File(config.getProjectRootFolder(), StringUtils.replacePlaceHolders(targetRoot + "/" + path, "${", "}", dollar_converter));
    }

    public Document loadSourceXmlDocument(String from) throws IOException {
        return XmlUtils.load(IOUtils.getTextResource(FileSystemTemplater.sourceConvertPath(from)));
    }

    public Document loadTargetXmlDocument(String from) throws IOException {
        return XmlUtils.load(IOUtils.getText(convertToPath(from)));
    }

    public void storeTargetXmlDocument(Document doc, String to) throws IOException {
        IOUtils.writeString(XmlUtils.toString(doc), convertToPath(to), config.getConsole());
    }

    public void copyXml(String from, String toFolder) throws IOException {
        String n = IOUtils.extractFileName(from);
        String text = IOUtils.getTextResource(sourceConvertPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        IOUtils.writeString(converted, convertToPath(toFolder + "/" + n), config.getConsole());
    }

    public void copyXml(String from, String toFolder, String newName) throws IOException {
        String text = IOUtils.getTextResource(sourceConvertPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        IOUtils.writeString(converted, convertToPath(toFolder + "/" + newName), config.getConsole());
    }

    public void copyProperties(String from, String toFolder) throws IOException {
        String n = IOUtils.extractFileName(from);
        String text = IOUtils.getTextResource(sourceConvertPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        IOUtils.writeString(converted, convertToPath(toFolder + "/" + n), config.getConsole());
    }

    public void targetAppendProperty(String toFile, String name, String value) throws IOException {
        targetAppendProperties(toFile, new String[]{name, value});
    }

    public void targetAppendProperties(String toFile, String[] keyValues) throws IOException {
        File okFile = convertToPath(toFile);
        Properties oldProperties = new Properties();
        oldProperties.load(new StringReader(IOUtils.getText(okFile)));

        Properties newProperties = new Properties();
        for (int i = 0; i < keyValues.length; i += 2) {
            String name = keyValues[i];
            String value = keyValues[i + 1];
            String name2 = StringUtils.replacePlaceHolders(name, "${{", "}}", dollar_converter);
            String value2 = StringUtils.replacePlaceHolders(value, "${{", "}}", dollar_converter);
            if (!value2.equals(oldProperties.getProperty(name2))) {
                newProperties.setProperty(name2, value2);
            }
        }
        if (newProperties.size() > 0) {
            IOUtils.writeStringAppend(IOUtils.toString(newProperties, null), okFile);
        }
    }

    public void copyJava(String from, String toFolder) throws IOException {
        String text = IOUtils.getTextResource(sourceConvertPath(from));
        String converted = StringUtils.replacePlaceHolders(text, "${{", "}}", dollar_converter);
        ClassInfo c = JavaUtils.detectedClassInfo(converted);
        IOUtils.writeString(converted, new File(convertToPath(toFolder).getPath() + "/" + c.getFullClassName().replace('.', '/') + ".java"), config.getConsole());
    }

    public String getTargetRoot() {
        return targetRoot;
    }

    public void setTargetRoot(String targetRoot) {
        this.targetRoot = targetRoot;
    }

    public void targetMkdirs(String path) {
        convertToPath(path).mkdirs();
    }

    public void targetAddPomParentModule(String pomxml, String modulePath) throws IOException {
        Document doc = loadTargetXmlDocument(pomxml);
        if (XmlUtils.addMavenModule(doc, replacePlaceHoldersSimple(modulePath))) {
            storeTargetXmlDocument(doc, pomxml);
        }
    }

}
