/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.Vr;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.streams.FileUtils;
import net.vpc.common.strings.StringConverter;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.exceptions.IllegalArgumentException;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.TracingAwarePropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;
import org.primefaces.event.FileUploadEvent;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            }else{
                throw new IllegalArgumentException("Unsupported");
            }
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
        }
        return req;
    }
    public static void prepareUserSession() {

        UserSession s = VrApp.getContext().getBean(UserSession.class);
        if (s != null && s.getUser() != null) {
            s.setTheme(Vr.get().getUserTheme(s.getUser().getLogin()).getId());
        } else {
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
            AppUser user = null;
            user = UserSession.get().getUser();
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


        File f = new File(p);
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
        return core.getFileSystem().get(path).getParentFile().get(f.getName());
    }
}
