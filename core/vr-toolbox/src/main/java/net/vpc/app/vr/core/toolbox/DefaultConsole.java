/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox;

import java.util.Scanner;

/**
 *
 * @author vpc
 */
class DefaultConsole implements TemplateConsole {

    public DefaultConsole() {
    }
    Scanner scanner = new Scanner(System.in);

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    public String ask(String propName, StringValidator validator, String defaultValue) {
        if (defaultValue != null) {
            return defaultValue;
        }
        String hints = null;
        if (validator != null) {
            hints = validator.getHints();
        }
        while (true) {
            System.out.println("Resolving value for : " + propName);
            if (defaultValue != null) {
                System.out.println("\tdefault is  : " + defaultValue);
            }
            if (hints != null) {
                System.out.println("\thints       : " + hints);
            }
            System.out.print("\tEnter value : ");
            String line = scanner.nextLine();
            if (line == null || line.length() == 0) {
                if (defaultValue != null) {
                    return defaultValue;
                }
            }
            if (validator != null) {
                try {
                    return validator.validate(line);
                } catch (Exception ex) {
                    System.err.println("Invalid value : " + ex.getMessage());
                }
            } else {
                return line;
            }
        }
    }

}
