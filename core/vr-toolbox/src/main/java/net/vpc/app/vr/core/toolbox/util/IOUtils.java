/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vr.core.toolbox.TemplateConsole;
import net.vpc.app.vr.core.toolbox.ValidatorFactory;

/**
 *
 * @author vpc
 */
public class IOUtils {

    public static String getTextResource(String url) throws IOException {
        URL r = IOUtils.class.getResource(url);
        if (r == null) {
            throw new IOException("Resource not found : [" + url + "]");
        }
        return getText(r);
    }

    public static String extractFileName(String str) throws IOException {
        int i = str.lastIndexOf('/');
        if (i < 0) {
            return str;
        }
        return str.substring(i + 1);
    }

    public static void writeStringAppend(String str, File file) throws IOException {
        File pf = file.getParentFile();
        if (pf != null) {
            pf.mkdirs();
        }
        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.append("\n" + str);
//        fileWriter.flush();
        fileWriter.close();
        System.out.println("[APPEND TO] " + file.getPath());
    }

    public static void writeString(String str, File file, TemplateConsole console) throws IOException {
        String old = file.exists() ? getText(file) : null;
        boolean isOverride = false;
        if (old != null) {
            if (old.equals(str)) {
                //do nothing...
                return;
            }
            if (!console.ask("[[override file " + file.getPath() + "]]", ValidatorFactory.BOOLEAN, null).equals("true")) {
                System.out.println("[WONT OVERRIDE] " + file.getPath());
                return;
            }
            isOverride = true;
        }
        File pf = file.getParentFile();
        if (pf != null) {
            pf.mkdirs();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(str);
        fileWriter.flush();
        fileWriter.close();
        if (isOverride) {
            System.out.println("[OVERRIDE] " + file.getPath());
        } else {
            System.out.println("[GENERATE] " + file.getPath());
        }
    }

    public static String getText(File website) throws IOException {
        return getText(website.toURI().toURL());
    }

    public static String getText(URL website) throws IOException {
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;
        boolean first = true;
        while ((inputLine = in.readLine()) != null) {
            if (first) {
                first = false;
            } else {
                response.append("\n");
            }
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }

    public static String getArtifactVersionOrDev() {
        String groupId = "net.vpc.app.vain-ruling.core";
        String artifactId = "vr-toolbox";
        return getArtifactVersionOrDev(groupId, artifactId);
    }

    public static String toString(Properties newProperties, String comments) {
        try {
            StringWriter s = new StringWriter();
            newProperties.store(s, comments==null?"any":comments);
            String ss = s.getBuffer().toString();
            if(comments!=null){
                return ss;
            }
            String[] all = ss.split("\n");
            StringBuilder finalV = new StringBuilder();
            for (int i = 0; i < all.length; i++) {
                if (finalV.length() > 0) {
                    finalV.append("\n");
                }
                if (!all[i].startsWith("#")) {
                    finalV.append(all[i]);
                }
            }
            return finalV.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static String getArtifactVersionOrDev(String groupId, String artifactId) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties");

        if (url != null) {
            Properties p = new Properties();
            try {
                p.load(url.openStream());
            } catch (IOException e) {
                //
            }
            String version = p.getProperty("version");
            if (!isEmpty(version)) {
                return version;
            }
        }
        return "DEV";
    }

}
