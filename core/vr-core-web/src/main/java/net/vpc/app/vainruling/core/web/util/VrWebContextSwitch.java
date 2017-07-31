package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.InvokeContext;
import net.vpc.upa.UPA;
import net.vpc.upa.web.UPAWebContextSwitch;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class VrWebContextSwitch implements UPAWebContextSwitch{
    @Override
    public InvokeContext createInvokeContext(ServletRequest request) {
        HttpServletRequest httprequest=(HttpServletRequest) request;
        UserSession s=(UserSession)httprequest.getSession(true).getAttribute("userSession");
        if(s!=null){
            String d = s.getDomain();
            if(!StringUtils.isEmpty(d)){
                InvokeContext invokeContext = new InvokeContext();
                invokeContext.setPersistenceUnit(UPA.getPersistenceUnit(d));
                return invokeContext;
            }
        }
        InvokeContext invokeContext = new InvokeContext();
        invokeContext.setPersistenceUnit(UPA.getPersistenceUnit("main"));
        return invokeContext;
    }
}
