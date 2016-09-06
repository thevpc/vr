package net.vpc.app.vainruling.core.web.themes;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.common.strings.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com on 7/25/16.
 */
@Service
public class VrThemeFactory {
    private Map<String,VrTheme> themes = new HashMap<>();
    private String defaultTheme;

    public VrThemeFactory() {
    }

    public Map<String,VrTheme> loadThemes() {
        defaultTheme= CorePlugin.get().getAppVersion().getDefaultTheme();
        Properties all=new Properties();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("/META-INF/vr-themes.properties");
            while (resources.hasMoreElements()) {
                URL u = resources.nextElement();
                InputStream s=null;
                try {
                    s = u.openStream();
                    all.load(s);
                } finally {
                    if (s != null) {
                        s.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<String> ids=new HashSet<>();
        for (Map.Entry<Object, Object> e : all.entrySet()) {
            String k = (String) e.getKey();
            if(k.startsWith("theme.")){
                String t=k.substring("theme.".length());
                if(t.indexOf('.')<0){
                    ids.add(t);
                }else{
                    t=t.substring(0,t.indexOf('.'));
                    ids.add(t);
                }
            }
        }
        Map<String,VrTheme> themes=new HashMap<>();
        for (String id : ids) {
            VrTheme t=new VrTheme();
            t.setId(id);
            if(StringUtils.isEmpty(t.getName())){
                t.setName(all.getProperty("theme." + id+".name"));
            }
            if(StringUtils.isEmpty(t.getName())){
                t.setName(id);
            }
            t.setVersion(all.getProperty("theme." + id + ".version"));
            if(StringUtils.isEmpty(t.getVersion())){
                t.setVersion("1.0");
            }
            t.setComponents(all.getProperty("theme." + id + ".components"));
            if(StringUtils.isEmpty(t.getVersion())){
                t.setComponents("vr-default");
            }
            t.setAuthor(all.getProperty("theme." + id + ".author"));
            if(StringUtils.isEmpty(t.getAuthor())){
                t.setAuthor("anonymous");
            }
            t.setDescription(all.getProperty("theme." + id + ".description"));
            themes.put(id,t);
        }
        return themes;
    }

    public VrTheme getTheme(String id) {
        Map<String, VrTheme> themesMap = getThemesMap();
        VrTheme vrTheme = themesMap.get(id);
        if(vrTheme==null){
            if("default".endsWith(id)){
                vrTheme = themesMap.get(defaultTheme);
                if(vrTheme==null){
                    if(themesMap.size()>0){
                        for (VrTheme theme : themesMap.values()) {
                            vrTheme=theme;
                            if(vrTheme!=null){
                                break;
                            }
                        }
                    }
                }
            }
        }
        return vrTheme;
    }

    public Map<String,VrTheme> getThemesMap() {
        if (themes.size() == 0) {
            themes = loadThemes();
        }
        return themes;
    }
    public List<VrTheme> getThemes() {
        return new ArrayList<VrTheme>(getThemesMap().values());
    }
}
