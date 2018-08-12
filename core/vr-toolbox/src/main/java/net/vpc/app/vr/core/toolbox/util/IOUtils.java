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
import java.net.URL;
import java.net.URLConnection;

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
        fileWriter.write("\n" + str);
        fileWriter.flush();
        fileWriter.close();
        System.out.println("[APPEND] " + file.getPath());
    }

    public static void writeString(String str, File file) throws IOException {
        File pf = file.getParentFile();
        if (pf != null) {
            pf.mkdirs();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(str);
        fileWriter.flush();
        fileWriter.close();
        System.out.println("[WRITTEN] " + file.getPath());
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

}
