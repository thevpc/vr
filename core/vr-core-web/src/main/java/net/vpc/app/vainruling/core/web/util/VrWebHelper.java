/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.common.io.FileUtils;
import net.vpc.common.strings.StringConverter;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.exceptions.UPAIllegalArgumentException;
import org.primefaces.event.FileUploadEvent;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.Method;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrWebHelper {

    public static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest req=null;
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if(requestAttributes instanceof ServletRequestAttributes){
                req=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            }else if(requestAttributes.getClass().getSimpleName().equals("JaxrsRequestAttributes")){
                try {
                    Method m = requestAttributes.getClass().getDeclaredMethod("getHttpServletRequest");
                    m.setAccessible(true);
                    req = (HttpServletRequest) m.invoke(requestAttributes);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if(requestAttributes.getClass().getSimpleName().equals("FacesRequestAttributes")){
                try {
                    Method m = requestAttributes.getClass().getDeclaredMethod("getFacesContext");
                    m.setAccessible(true);
                    FacesContext fc= (FacesContext) m.invoke(requestAttributes);
                    if(fc!=null) {
                        ExternalContext externalContext = fc.getExternalContext();
                        if(externalContext!=null) {
                            req = (HttpServletRequest) externalContext.getRequest();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                throw new UPAIllegalArgumentException("Unsupported");
            }
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
        }
        return req;
    }
    public static void prepareUserSession() {

        UserSession s = UserSession.get();
        AppUser u = CorePlugin.get().getCurrentUser();
        if (s != null) {
            if(u != null) {
                s.setTheme(Vr.get().getUserTheme(u.getLogin()).getId());
            }else{
                s.setTheme(Vr.get().getAppTheme().getId());
            }
            HttpServletRequest req = getHttpServletRequest();
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

    public static VFile handleFileUpload(FileUploadEvent event, String destinationPath, boolean userhome, boolean override) throws Exception {
        String fileName = event.getFile().getFileName();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if(destinationPath==null){
            destinationPath="";
        }
        if(StringUtils.indexOfWord(destinationPath,"..",0,"/")>=0){
            throw new RuntimeException("Invalid path : "+destinationPath);
        }
        if(destinationPath.endsWith("/*")){
            destinationPath=destinationPath.substring(0,destinationPath.length()-2)+"/"+fileName;
        }

        String path="";
        if(userhome) {
            AppUser user = core.getCurrentUser();
            if (user == null) {
                return null;
            }
            path=core.getUserFolder(user.getLogin()).getPath()+"/"+destinationPath;
        }else{
            path=destinationPath;
        }
        if(!path.startsWith("/")){
            path="/"+path;
        }
        String p = core.getNativeFileSystemPath()+ path;


        File f = new File(VrPlatformUtils.validatePath(p));
        if(f.exists() && !override){
           int index=2;
            while(true) {
                File f2 = FileUtils.changeFileSuffix(f, "-" + index);
                if(!f2.exists()){
                    f=f2;
                    break;
                }
                index++;
            }
        }
        f.getParentFile().mkdirs();
        event.getFile().write(f.getPath());
        return UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
            @Override
            public VirtualFileSystem run() {
                return core.getRootFileSystem();
            }
        }).get(path).getParentFile().get(f.getName());
    }
}
