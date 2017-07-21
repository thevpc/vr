/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vr.plugins.academicprofile.service.security;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.upa.DefaultEntitySecurityManager;
import net.vpc.upa.Entity;
import net.vpc.upa.config.SecurityContext;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.UserExpression;

/**
 * Each Student will see only his own CV that's it!
 */
@SecurityContext(entity = "AcademicStudentCV")
public class AcademicStudentCVSecurer extends DefaultEntitySecurityManager {

    public AcademicStudentCVSecurer() {
    }


    @Override
    public Expression getEntityFilter(Entity entity) throws UPAException {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (core.isUserSessionAdmin()) {
            return null;
        }
        return new UserExpression("this.student.user.login=currentUser()");
    }

}
