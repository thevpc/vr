/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.web;

import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppContact;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.service.test.DevSrv;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.persistence.UConnection;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Developer Upgrade",
        css = "fa-dashboard", url = "modules/admin/dev-upgrade",
        menu = "/Admin", securityKey = "Custom.DevTools"
)
@ManagedBean
@Scope(value = "session")
public class DevCtrl {

    private String message;

    public void doUpgrade() {
        try {
            VrApp.getBean(DevSrv.class).doUpgrade();
            setMessage("Successful upgrade");
        } catch (Exception e) {
            setMessage(StringUtils.verboseStacktraceToString(e));
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
