/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import java.io.Serializable;
import java.util.Objects;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
public class VrLabel implements Serializable, Comparable<VrLabel> {

    public static final String SUCCESS = "success";
    public static final String INFO = "info";
    public static final String DANGER = "danger";
    public static final String WARNING = "warning";
    public static final String PRIMARY = "primary";
    public static final String DEFAULT = "default";
    private final String severity;
    private final String type;
    private final String message;
    private final String actionType;
    private final String[] actionParameters;

    public static VrLabel forDefault(String type, String message) {
        return new VrLabel(DEFAULT, type, message, null, null);
    }

    public static VrLabel forDanger(String type, String message) {
        return new VrLabel(DANGER, type, message, null, null);
    }

    public static VrLabel forWarning(String type, String message) {
        return new VrLabel(WARNING, type, message, null, null);
    }

    public static VrLabel forPrimary(String type, String message) {
        return new VrLabel(PRIMARY, type, message, null, null);
    }

    public static VrLabel forInfo(String type, String message) {
        return new VrLabel(INFO, type, message, null, null);
    }

    public static VrLabel forSuccess(String type, String message) {
        return new VrLabel(SUCCESS, type, message, null, null);
    }

    public VrLabel(String severity, String type, String message, String actionType, String[] actionParameters) {
        this.severity = severity;
        this.type = type;
        this.message = message;
        this.actionType = actionType;
        this.actionParameters = actionParameters == null ? new String[0] : actionParameters;
    }

    public VrLabel setAction(String actionType, String... actionParameters) {
        return new VrLabel(severity, type, message, actionType, actionParameters);
    }

    public String getActionType() {
        return actionType;
    }

    public String[] getActionParameters() {
        return actionParameters;
    }

    public String getSeverity() {
        return severity;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.severity);
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.message);
        return hash;
    }

    @Override
    public int compareTo(VrLabel o) {
        int y = compareSeverity(severity, o.severity);
        if (y != 0) {
            return y;
        }
        y = StringUtils.trim(type).compareTo(StringUtils.trim(o.type));
        if (y != 0) {
            return y;
        }
        y = StringUtils.trim(message).compareTo(StringUtils.trim(o.message));
        if (y != 0) {
            return y;
        }
        return 0;
    }

    private static int compareSeverity(String a, String b) {
        int y = Integer.compare(hashSeverity(a), hashSeverity(b));
        if (y != 0) {
            return y;
        }
        y = VrUtils.normalizeName(a).compareTo(VrUtils.normalizeName(b));
        if (y != 0) {
            return y;
        }
        y = StringUtils.trim(a).compareTo(StringUtils.trim(b));
        if (y != 0) {
            return y;
        }
        return 0;
    }

    private static int hashSeverity(String obj) {
        switch (VrUtils.normalizeName(obj)) {
            case DANGER:
                return 1;
            case WARNING:
                return 2;
            case SUCCESS:
                return 3;
            case INFO:
                return 4;
            case PRIMARY:
                return 5;
            case DEFAULT:
                return 6;
            default:
                return 10;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VrLabel other = (VrLabel) obj;
        if (!Objects.equals(this.severity, other.severity)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return true;
    }

}
