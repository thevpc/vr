/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service.security;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.upa.DefaultEntitySecurityManager;
import net.vpc.upa.Entity;
import net.vpc.upa.config.SecurityContext;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.UserExpression;

/**
 * @author vpc
 */
@SecurityContext(entity = "MailboxReceived")
public class MailboxReceivedSecurer extends DefaultEntitySecurityManager {

    @Override
    public Expression getEntityFilter(Entity entity) throws UPAException {
        if (VrApp.getBean(CorePlugin.class).isUserSessionAdmin()) {
            return null;
        }
        return new UserExpression("this.deleted=false and this.sender.login=currentUser()");
    }

}
