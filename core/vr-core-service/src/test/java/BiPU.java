/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */

import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.common.util.Chronometer;
import net.vpc.upa.*;
import net.vpc.upa.bulk.ImportPersistenceUnitListener;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.filters.EntityFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author taha.bensalah@gmail.com
 */
public class BiPU {

    public static void main(String[] args) {
        String html="<a href=\"docs://Calendriers/DS\">/Mes Documents/Calendriers/DS</a>";
////        Matcher m = Pattern.compile(".*[\"](.+])[\"].*").matcher(html);
//        Matcher m = Pattern.compile(".*[\"](.+)[\"].*").matcher(html);
//        StringBuffer sb=new StringBuffer();
//        while(m.find()){
//            String rep = m.group(1);
//            System.out.println(rep);
//            m.appendReplacement(sb,"somthing://<"+rep+">");
//        }
//        m.appendTail(sb);
        System.out.println(replace0(html));
    }

    public static String replace0(String format) {
        Pattern pattern = Pattern.compile("href=\"(?<url>[^\"]*)\"");
        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(format);
        while (m.find()) {
            String url = m.group("url");
            int x=url.indexOf("://");
            String processed=null;
            if(x>0){
                String protocol=url.substring(0,x);
                if("docs".equals(protocol)){
                    String path=url=url.substring(protocol.length()+"://".length());
                    if(!path.startsWith("/")){
                        path="/"+path;
                    }
                    path=path.replace('\'','_');//fix injection issues
                    processed=("http://eniso.info/vr/p/documents?a={path='"+path+"'}");
                }
            }
            if(processed!=null) {
                m.appendReplacement(sb,"href=\""+processed+"\"");
            }else{
                m.appendReplacement(sb,m.group());
            }
        }
        m.appendTail(sb);

        return sb.toString();
    }

//    public static void main(String[] args) {
//        BiPU b = new BiPU();
//        b.go();
//    }

    public void go() {
        VrApp.runStandalone("admin", "vilain77");
//        net.vpc.common.util.LogUtils.configure(Level.SEVERE, "net.vpc");
        final PersistenceUnit source = UPA.getPersistenceUnit("main");
        final PersistenceUnit target = UPA.getPersistenceUnit("mysql");
        target.beginStructureModification();
        for (Entity entity : target.getEntities()) {
            entity.setUserModifiers(entity.getUserModifiers().add(EntityModifier.CLEAR));
        }
        target.commitStructureModification();
        EntityFilter filterOne = new EntityFilter() {
            @Override
            public boolean accept(Entity entity) throws UPAException {
                return entity.getName().equals("AppUser");
            }
        };

        final EntityFilter filter = null;//filterOne
        final boolean clear = false;

        Chronometer chronometer = new Chronometer();
        source.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                target.invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        //target.updateFormulas(AppUser.class,null,null);
                        TraceService.get().archiveLogs(1);
                        if (clear) {
                            target.clear(filter, null);
                        }
                        target.getImportExportManager().importEntities(source, filter, clear, new ImportPersistenceUnitListener() {
                            @Override
                            public void objectPersisted(String entityName, Object source, Object target) {

                            }

                            @Override
                            public void objectMerged(String entityName, Object source, Object target) {

                            }

                            @Override
                            public void objectPersistFailed(String entityName, Object source, Object target, Exception error) throws Exception {
                                throw error;
                            }

                            @Override
                            public void objectMergeFailed(String entityName, Object source, Object target, Exception error) throws Exception {
                                throw error;
                            }
                        });
                    }
                });
            }
        });
        System.out.println("Import finished in " + chronometer.stop());
    }


}
