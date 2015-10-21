/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service.security;

import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.upa.DefaultEntitySecurityManager;
import net.vpc.upa.Entity;
import net.vpc.upa.config.SecurityContext;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.UserExpression;

/**
 *
 * @author vpc
 */
@SecurityContext(entity = "ArticlesItem")
public class ArticleSecurer extends DefaultEntitySecurityManager {

    public ArticleSecurer() {
    }

    
    @Override
    public Expression getEntityFilter(Entity entity) throws UPAException {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (core.isActualAdmin()) {
            return null;
        }
        return new UserExpression("this.sender.login=currentUser()");
    }

//    @Override
//    public boolean isAllowedRead(Field field) throws UPAException {
//        if(field.getAbsoluteName().equals("AcademicTeacherCV.viewsCounter")){
//           return VrApp.getBean(CorePlugin.class).isActualAdmin();
//        }
//        return super.isAllowedRead(field);
//    }
//    
//    @Override
//    public boolean isAllowedWrite(Field field) throws UPAException {
//        if(field.getAbsoluteName().equals("AcademicTeacherCV.viewsCounter")){
//           return VrApp.getBean(CorePlugin.class).isActualAdmin();
//        }
//        return super.isAllowedRead(field);
//    }

    
}
