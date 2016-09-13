/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.common.strings.StringConverter;
import net.vpc.common.strings.StringUtils;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrWebHelper {

    public static void prepareUserSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = attr.getRequest();
        UserSession s = VrApp.getContext().getBean(UserSession.class);
        if (s != null && s.getUser() != null) {
            s.setTheme(Vr.get().getUserTheme(s.getUser().getLogin()).getId());
        } else {
            s.setTheme(Vr.get().getAppTheme().getId());
        }
        if (s.getSessionId() == null) {
            HttpSession session = req.getSession(true); // true == allow create
            s.setSessionId(session.getId());
        }
        if (s.getLocale() == null) {
            s.setLocale(req.getLocale());
        }
        if (s.getClientIpAddress() == null) {
            String ipAddress = req.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = req.getRemoteAddr();
            }
            s.setClientIpAddress(ipAddress);
        }
    }


    public static Object evalSpringExpr(String expr) {
        if(expr==null){
            return null;
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(VrApp.getContext()));
        ExpressionParser parser=new SpelExpressionParser();
        Expression expression = parser.parseExpression(expr);
        return expression.getValue(context);
    }

    public static String evalSpringExprMessage(String message) {
        if(message==null){
            return null;
        }
        if(message.contains("#{")){
            message= StringUtils.replacePlaceHolders(message, "#{", "}",new StringConverter() {
                @Override
                public String convert(String str) {
                    //TODO
                    if("vr.themeContext".equals(str)){
                        return Vr.get().getThemeContext();
                    }
                    if("vr.themePath".equals(str)){
                        return Vr.get().getThemePath();
                    }
                    if("vr.themeRelativePath".equals(str)){
                        String themePath = Vr.get().getThemePath();
                        if(themePath.startsWith("/")){
                            return themePath.substring(1);
                        }
                        return themePath;
                    }
                    return (String)evalSpringExpr(str);
                }
            });
        }
        return message;
    }
}
