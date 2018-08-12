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
    public String askForString(String propName, StringValidator validator, String defaultValue) {
        if(true){
            return defaultValue;
        }
        while (true) {

            System.out.print("Enter " + propName + "  (default " + defaultValue + ") : ");
            String line = scanner.nextLine();
            if (line == null || line.length() == 0) {
                return defaultValue;
            }
            if (validator != null) {
                try {
                    validator.validate(line);
                } catch (Exception ex) {
                    System.err.println("Invalid value : " + ex.getMessage());
                }
            }
            return line;
        }
    }

}
