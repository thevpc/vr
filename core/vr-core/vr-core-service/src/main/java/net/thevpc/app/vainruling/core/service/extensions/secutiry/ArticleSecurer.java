/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.secutiry;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.upa.DefaultEntitySecurityManager;
import net.thevpc.upa.Entity;
import net.thevpc.upa.config.SecurityContext;
import net.thevpc.upa.exceptions.UPAException;
import net.thevpc.upa.expressions.Expression;
import net.thevpc.upa.expressions.UserExpression;

/**
 * @author taha.bensalah@gmail.com
 */
@SecurityContext(entity = "AppArticle")
public class ArticleSecurer extends DefaultEntitySecurityManager {

    public ArticleSecurer() {
    }


    @Override
    public Expression getEntityFilter(Entity entity) throws UPAException {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (core.isCurrentSessionAdmin()) {
            return null;
        }
        return new UserExpression("this.sender.login=currentUser()");
    }

//    @Override
//    public boolean getAllowedReadPermission(Field field) throws UPAException {
//        if(field.getAbsoluteName().equals("AcademicTeacherCV.viewsCounter")){
//           return VrApp.getBean(CorePlugin.class).isCurrentSessionAdmin();
//        }
//        return super.getAllowedReadPermission(field);
//    }
//    
//    @Override
//    public boolean getAllowedWritePermission(Field field) throws UPAException {
//        if(field.getAbsoluteName().equals("AcademicTeacherCV.viewsCounter")){
//           return VrApp.getBean(CorePlugin.class).isCurrentSessionAdmin();
//        }
//        return super.getAllowedReadPermission(field);
//    }


}
