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
    private Map<VrThemeFace,VrThemeFactoryFace> faces=new HashMap<>(); ;


    public VrThemeFactory() {
        faces.put(VrThemeFace.PUBLIC, new VrThemeFactoryPublicFace());
        faces.put(VrThemeFace.PRIVATE, new VrThemeFactoryPrivateFace());
    }

    public VrThemeFactoryFace getFace(VrThemeFace face) {
        VrThemeFactoryFace factory = faces.get(face);
        if(factory==null){
            throw new NoSuchElementException(String.valueOf(face));
        }
        return factory;
    }

    public VrTheme getTheme(VrThemeFace face,String id) {
        return getFace(face).getTheme(id);
    }

    public List<VrTheme> getThemes(VrThemeFace face) {
        return getFace(face).getThemes();
    }

    public Map<String,VrTheme> getThemesMap(VrThemeFace face) {
        return getFace(face).getThemesMap();
    }

    private static class VrThemeFactoryPublicFace extends VrThemeFactoryFace{
        protected String loadDefaultTheme(){
            return CorePlugin.get().getAppVersion().getDefaultPublicTheme();
        }

        @Override
        protected String getThemePrefix() {
            return "public";
        }
    }

    private static class VrThemeFactoryPrivateFace extends VrThemeFactoryFace{
        protected String loadDefaultTheme(){
            return CorePlugin.get().getAppVersion().getDefaultPrivateTheme();
        }

        @Override
        protected String getThemePrefix() {
            return "private";
        }
    }

    private static abstract class VrThemeFactoryFace{
        private Map<String,VrTheme> themes = new HashMap<>();
        private String defaultTheme;

        public VrThemeFactoryFace() {
        }

        protected abstract String loadDefaultTheme();
        protected abstract String getThemePrefix();

        public Map<String,VrTheme> loadThemes() {
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
            String themePrefix=getThemePrefix()+"-theme.";
            for (Map.Entry<Object, Object> e : all.entrySet()) {
                String k = (String) e.getKey();
                if(k.startsWith(themePrefix)){
                    String t=k.substring(themePrefix.length());
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
                    t.setName(all.getProperty(themePrefix + id+".name"));
                }
                if(StringUtils.isEmpty(t.getName())){
                    t.setName(id);
                }
                t.setVersion(all.getProperty(themePrefix + id + ".version"));
                if(StringUtils.isEmpty(t.getVersion())){
                    t.setVersion("1.0");
                }
                t.setComponents(all.getProperty(themePrefix + id + ".components"));
                if(StringUtils.isEmpty(t.getVersion())){
                    t.setComponents("vr-default");
                }
                t.setAuthor(all.getProperty(themePrefix + id + ".author"));
                if(StringUtils.isEmpty(t.getAuthor())){
                    t.setAuthor("anonymous");
                }
                t.setDescription(all.getProperty(themePrefix + id + ".description"));
                themes.put(id,t);
            }
            return themes;
        }

        public VrTheme getTheme(String id) {
            Map<String, VrTheme> themesMap = getThemesMap();
            VrTheme vrTheme = themesMap.get(id);
            if(vrTheme==null){
                if("default".equals(id)){
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
                defaultTheme=loadDefaultTheme();
            }
            return themes;
        }

        public List<VrTheme> getThemes() {
            return new ArrayList<VrTheme>(getThemesMap().values());
        }
    }
}
