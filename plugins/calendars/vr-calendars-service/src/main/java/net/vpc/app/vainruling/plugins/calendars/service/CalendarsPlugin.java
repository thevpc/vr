/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service;

import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningData;
import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningDay;
import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningHour;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@AppPlugin()
public class CalendarsPlugin {

    @Autowired
    CorePlugin core;

    public List<PlanningData> loadUserPlannings(int userId) {
        List<PlanningData> all=new ArrayList<>();
        for (Map.Entry<String,VrPlanningProvider> o : VrApp.getContext().getBeansOfType(VrPlanningProvider.class).entrySet()) {
            List<PlanningData> found = o.getValue().loadUserPlannings(userId);
            if(found!=null){
                for (PlanningData planningData : found) {
                    if(planningData!=null){
                        all.add(planningData);
                    }
                }
            }
        }
        return all;
    }



    public PlanningData parsePlanningDataXML(Node planningNode) {
//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
        if (planningNode.getNodeType() == Node.ELEMENT_NODE) {

            Element eElement = (Element) planningNode;
            String tn = eElement.getAttribute("name");
            PlanningData p2 = new PlanningData();
            p2.setPlanningName(tn.trim());
            p2.setDays(new ArrayList<PlanningDay>());
            NodeList days = eElement.getElementsByTagName("Day");
            for (int di = 0; di < days.getLength(); di++) {
                Node dayNode = days.item(di);
                PlanningDay dd = parsePlanningDayXML(dayNode);
                if (dd != null) {
                    p2.getDays().add(dd);
                }
            }
            while (p2.getDays().size() < 6) {
                PlanningDay d = new PlanningDay();
                d.setDayName("Day " + p2.getDays().size());
                p2.getDays().add(d);
            }
            return p2;
        }
        return null;
    }

    private PlanningDay parsePlanningDayXML(Node dayNode) {
        if (dayNode.getNodeType() == Node.ELEMENT_NODE) {
            Element dayElement = (Element) dayNode;
            PlanningDay planningDay = new PlanningDay();
            planningDay.setDayName(dayElement.getAttribute("name"));
            List<PlanningHour> planningHours = new ArrayList<>();
            NodeList hours = dayElement.getElementsByTagName("Hour");
            for (int hi = 0; hi < hours.getLength(); hi++) {
                Node hourNode = hours.item(hi);
                PlanningHour ph = parsePlanningHourXML(hourNode);
                if (ph != null) {
                    planningHours.add(ph);
                }

            }
            planningDay.setHours(planningHours);

            if (planningDay.getHours() == null || planningDay.getHours().size() < 5) {
                List<PlanningHour> all = new ArrayList<>();
                if (planningDay.getHours() != null) {
                    all.addAll((planningDay.getHours()));
                }
                while (all.size() < 5) {
                    PlanningHour dd = new PlanningHour();
                    dd.setHour("H #" + all.size());
                    all.add(dd);
                }
                planningDay.setHours(all);
            }

            return (planningDay);
        }
        return null;
    }

    private PlanningHour parsePlanningHourXML(Node hourNode) {
        if (hourNode.getNodeType() == Node.ELEMENT_NODE) {
            Element hourElement = (Element) hourNode;
            PlanningHour ph = new PlanningHour();
            ph.setHour(hourElement.getAttribute("name"));
            NodeList childNodes = hourElement.getChildNodes();
            for (int ci = 0; ci < childNodes.getLength(); ci++) {
                Node cNode = childNodes.item(ci);
                if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cElement = (Element) cNode;
                    if ("Activity_Tag".equals(cElement.getNodeName())) {
                        ph.setActivity(cElement.getAttribute("name"));
                    } else if ("Students".equals(cElement.getNodeName())) {
                        ph.setStudents(cElement.getAttribute("name"));
                    } else if ("Teacher".equals(cElement.getNodeName())) {
                        ph.setTeacher(cElement.getAttribute("name"));
                    } else if ("Subject".equals(cElement.getNodeName())) {
                        ph.setSubject(cElement.getAttribute("name"));
                    } else if ("Room".equals(cElement.getNodeName())) {
                        ph.setRoom(cElement.getAttribute("name"));
                    }
                }
            }
            String actor = "";
            if (!StringUtils.isEmpty(ph.getStudents())) {
                if (actor.length() > 0) {
                    actor += " / ";
                }
                actor += ph.getStudents();
            }
            if (!StringUtils.isEmpty(ph.getTeacher())) {
                if (actor.length() > 0) {
                    actor += " / ";
                }
                actor += ph.getTeacher();
            }
            ph.setActor(actor);
            return (ph);
        }
        return null;
    }


    private Set<String> splitOtherNames(String value) {
        Set<String> all = new HashSet<>();
        if (value != null) {
            for (String s : value.split(",|;")) {
                if (s.trim().length() > 0) {
                    all.add(s.trim().toLowerCase());
                }
            }
        }
        return all;
    }


    public List<PlanningData> loadCalendars(String type, String key){
        List<PlanningData> all=new ArrayList<>();
        for (Map.Entry<String,VrPlanningProvider> o : VrApp.getContext().getBeansOfType(VrPlanningProvider.class).entrySet()) {
            List<PlanningData> found = o.getValue().loadCalendars(type,key);
            if(found!=null){
                for (PlanningData planningData : found) {
                    if(planningData!=null){
                        all.add(planningData);
                    }
                }
            }
        }
        return all;
    }

}
