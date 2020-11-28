/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

import java.util.Objects;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrPageHistoryItem {
    private String command;
    private String arguments;

    public VrPageHistoryItem(String command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.command);
        hash = 59 * hash + Objects.hashCode(this.arguments);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VrPageHistoryItem other = (VrPageHistoryItem) obj;
        if (!Objects.equals(this.command, other.command)) {
            return false;
        }
        return Objects.equals(this.arguments, other.arguments);
    }

}
