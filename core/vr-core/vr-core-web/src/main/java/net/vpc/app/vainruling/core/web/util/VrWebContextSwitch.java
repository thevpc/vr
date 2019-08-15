package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.security.UserToken;
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
        CorePlugin corePlugin = CorePlugin.get();
        UserToken token=null;
        if(corePlugin!=null){
            token=corePlugin.getCurrentToken();
        }
        if(token==null) {
            UserSession s = (UserSession) httprequest.getSession(true).getAttribute("userSession");
            if(s!=null){
                token=s.getToken();
            }
        }
        if(token!=null){
            String d = token.getDomain();
            if(!StringUtils.isBlank(d)){
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
