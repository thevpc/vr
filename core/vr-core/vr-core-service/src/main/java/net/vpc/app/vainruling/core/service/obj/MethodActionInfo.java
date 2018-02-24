package net.vpc.app.vainruling.core.service.obj;

///*
// * To change this license header, choose License Headers in Project Properties.
// *
// * and open the template in the editor.
// */
//package net.vpc.app.vainruling.api.core;
//
//import net.vpc.app.vainruling.api.EntityAction;
//import net.vpc.app.vainruling.core.service.util.PlatformReflector;
//import net.vpc.upa.Entity;
//import net.vpc.upa.UPA;
//
///**
// *
// * @author taha.bensalah@gmail.com
// */
//public class MethodActionInfo extends ActionInfo {
//
//    private PlatformReflector.InstanceInvoker p;
//
//    @Override
//    public <T> T invoke(Class entityType, String actionName, Object obj, Object[] args) {
//        EntityAction r = p.getAnnotation();
//        Class[] argTypes = p.getParameterTypes();
//        Class rt = p.getReturnType();
//        boolean unspecifiedEntityType = r.entityType().equals(Void.class);
//        String rname = r.actionName();
//        if (rname.isEmpty()) {
//            rname = p.getName();
//        }
//        boolean unspecifiedActionName = rname.equals("*");
//        PluginActionEvent evt = new PluginActionEvent(entityType, actionName, obj, args);
//        Object[] vals = new Object[argTypes.length];
//        if (argTypes.length == 0) {
//
//        } else if (argTypes.length == 1 && argTypes[0].equals(PluginActionEvent.class)) {
//            vals[0] = evt;
//        } else {
//            //ignore all!!
//            for (int i = 0; i < vals.length; i++) {
//                if (argTypes[i].equals(Class.class)) {
//                    vals[i] = entityType;
//                } else if (argTypes[i].equals(Entity.class)) {
//                    vals[i] = UPA.getPersistenceUnit().getEntity(entityType);
//                } else if (argTypes[i].equals(Object.class)) {
//                    vals[i] = obj;
//                } else if (argTypes[i].equals(entityType)) {
//                    vals[i] = obj;
//                } else if (argTypes[i].equals(String.class)) {
//                    vals[i] = actionName;
//                } else if (argTypes[i].equals(Object[].class)) {
//                    vals[i] = args;
//                } else {
//                    throw new UPAIllegalArgumentException("Unsupported Action method " + p);
//                }
//            }
//        }
//
//        if ((unspecifiedEntityType
//                || r.entityType().equals(entityType))
//                && (unspecifiedActionName
//                || rname.equals(actionName))) {
//
//            T i = null;
//            boolean ok = false;
//            try {
//                i = p.invoke(vals);
//                ok = true;
//            } catch (UnsupportedOperationException e) {
//
//            }
//            if (ok) {
//                return i;
//            }
//        }
//        throw new UnsupportedOperationException();
//    }
//
//}
