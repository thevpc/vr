package net.vpc.app.vainruling.plugins.calendars.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningData;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vpc on 9/15/16.
 */
@Service
public class VrDefaultPlanningProvider implements VrPlanningProvider{
    @Autowired
    CorePlugin core;
    @Autowired
    CalendarsPlugin calendarsPlugin;


    public List<PlanningData> loadUserPlannings(int userId) {
        AppUser uuu = core.findUser(userId);
        List<PlanningData> list = new ArrayList<>();
        if (uuu == null) {
            return list;
        }
//        String teacherName = uuu == null ? "" : uuu.getContact().getFullName();
        VFile p = core.getUserFolder(uuu.getLogin()).get("/myplanning.xml");
        if (p != null && p.exists()) {
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(p.getInputStream());
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("User");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        PlanningData d = calendarsPlugin.parsePlanningDataXML(nNode);
                        if (d != null) {
                            d.setId("Custom-"+(temp+1));
                            list.add(d);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public List<PlanningData> loadCalendars(String type, String key) {
        if("my-calendars".equals(type)) {
            AppUser user = UserSession.getCurrentUser();
            if (user != null) {
                return calendarsPlugin.loadUserPlannings(user.getId());
            }
        }else if("user-calendars".equals(type)){
            if(!StringUtils.isEmpty(key)){
                key=key.trim();
                if(Character.isDigit(key.charAt(0))){
                    return calendarsPlugin.loadUserPlannings(Integer.parseInt(key));
                }
                AppUser user = core.findUser(key);
                if(user!=null){
                    return calendarsPlugin.loadUserPlannings(user.getId());
                }
            }
        }
        return Collections.EMPTY_LIST;
    }
}
