//package net.thevpc.app.vainruling.core.service;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class TestPFE {
//    public static List<PFE> listBySupervisor(){
//        return new ArrayList<>();
//    }
//
//    public static class PFE {
//        int id;
//        int r1;
//        int r2;
//        int r3;
//        int t;
//
//        public double score() {
//            int x=0;
//            if(r1!=0){
//                x+=0.1;
//            }
//            if(r2!=0){
//                x+=0.1;
//            }
//            if(r3!=0){
//                x+=0.1;
//            }
//            if(t!=0){
//                x+=0.1;
//            }
//            if (r1 != r2 ) {
//                x+=0.1;
//            }
//            if (r2 != r3 ) {
//                x+=0.1;
//            }
//            if (r1 != r3 ) {
//                x+=0.1;
//            }
//            return x;
//        }
//        public boolean isValid() {
//            if (r1 >= 0 && r2 >= 0 && r3 >= 0 && t >= 0) {
//                if (r1 != r2 && r1 != r3 && r2 != r3) {
//                    return true;
//                }
//            }
//            return false;
//        }
//    }
//
//    public class PFEList {
//        private List<PFE> pfe;
//
//        public double getScore() {
//            double x=0;
//            Set<String> visitedr1t=new HashSet<>();
//            Set<String> visitedr2t=new HashSet<>();
//            Set<String> visitedr3t=new HashSet<>();
//            Set<Integer> visitedt=new HashSet<>();
//            for (PFE pfe1 : pfe) {
//                x+=pfe1.score();
//                if(!visitedt.contains(pfe1.t)){
//                    visitedt.add(pfe1.t);
//                }
//                String k = pfe1.r1 + "!" + pfe1.t;
//                if(visitedr1t.contains(k)){
//                    //bad
//                }else {
//                    x+=0.1;
//                    visitedr1t.add(k);
//                }
//                k = pfe1.r2 + "!" + pfe1.t;
//                if(visitedr2t.contains(k)){
//                    //bad
//                }else {
//                    x+=0.1;
//                    visitedr2t.add(k);
//                }
//                k = pfe1.r3 + "!" + pfe1.t;
//                if(visitedr3t.contains(k)){
//                    //bad
//                }else {
//                    x+=0.1;
//                    visitedr3t.add(k);
//                }
//            }
//            return x;
//        }
//
//        public boolean isValid() {
//            Set<String> visited=new HashSet<>();
//            for (PFE pfe1 : pfe) {
//                if(!pfe1.isValid()){
//                    return false;
//                }
//                String k = pfe1.r1 + "!" + pfe1.t;
//                if(visited.contains(k)){
//                    return false;
//                }else {
//                    visited.add(k);
//                }
//            }
//        }
//    }
//}
