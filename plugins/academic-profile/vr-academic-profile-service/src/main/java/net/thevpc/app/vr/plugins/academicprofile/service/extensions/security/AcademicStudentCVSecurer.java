/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vr.plugins.academicprofile.service.extensions.security;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.upa.DefaultEntitySecurityManager;
import net.thevpc.upa.Entity;
import net.thevpc.upa.config.SecurityContext;
import net.thevpc.upa.exceptions.UPAException;
import net.thevpc.upa.expressions.Expression;
import net.thevpc.upa.expressions.UserExpression;

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
        if (core.isCurrentSessionAdmin()) {
            return null;
        }
        return new UserExpression("this.student.user.login=currentUser()");
    }

}
