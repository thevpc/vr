//package net.thevpc.app.vainruling.core.service.cache;
//
//import net.thevpc.app.vainruling.core.service.CorePlugin;
//import net.thevpc.app.vainruling.core.service.VrApp;
//import net.thevpc.upa.Entity;
//import net.thevpc.upa.PersistenceUnit;
//import net.thevpc.upa.callbacks.PersistEvent;
//import net.thevpc.upa.callbacks.RemoveEvent;
//import net.thevpc.upa.callbacks.UpdateEvent;
//import net.thevpc.upa.config.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by vpc on 5/17/16.
// */
//@Callback(
//        //config = @Config(persistenceUnit = "main")
//)
//public class CorePluginCacheInvalidatorCallback {
//    private Map<String,String[]> invalidators=new HashMap<>();
//
//    public CorePluginCacheInvalidatorCallback() {
//        invalidators.put("AppUserProfileBinding",new String[]{
//                "findUniformProfileNamesMapByUserId:true",
//                "findUniformProfileNamesMapByUserId:false"});
//
//        invalidators.put("AppUser",new String[]{
//                "findUniformProfileNamesMapByUserId:true",
//                "findUniformProfileNamesMapByUserId:false",
//                "findUserLoginToIdMap"
//        });
//
//    }
//
//    private void onChange(String entityName,CorePlugin core,Map<String, Object> globalCache){
//        String[] keys = invalidators.get(entityName);
//        if(keys!=null){
//            synchronized (globalCache) {
//                for (String key : keys) {
//                    globalCache.remove(key);
//                }
//            }
//        }
//    }
//
//    @OnPrePersist
//    public void onPrePersist(PersistEvent event) {
////        PersistenceUnit pu = event.getPersistenceUnit();
//        Entity entity = event.getEntity();
//        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        onChange(entity.getName(),core,core.getGlobalCache());
//    }
//
//    @OnPreRemove
//    public void onPreRemove(RemoveEvent event) {
////        PersistenceUnit pu = event.getPersistenceUnit();
//        Entity entity = event.getEntity();
//        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        onChange(entity.getName(),core,core.getGlobalCache());
//    }
//
//    @OnPreUpdate
//    public void onPreUpdate(UpdateEvent event) {
////        PersistenceUnit pu = event.getPersistenceUnit();
//        Entity entity = event.getEntity();
//        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        onChange(entity.getName(),core,core.getGlobalCache());
//    }
//
//}
