/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.config;

/**
 * @author taha.bensalah@gmail.com
 */
public enum AcademicTeacherSituationType {
    PERMANENT(true),
    LEAVE(false),
    TEMPORARY(false),
    CONTRACTUAL(true);

    private boolean withDue;

    AcademicTeacherSituationType(boolean withDue) {
        this.withDue = withDue;
    }

    public boolean isWithDue() {
        return withDue;
    }
}
