package net.vpc.app.vainruling.plugins.calendars.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
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
import java.util.*;

/**
 * Created by vpc on 9/15/16.
 */
@Service
public class VrDefaultCalendarProvider implements VrCalendarProvider {
    @Autowired
    CorePlugin core;
    @Autowired
    CalendarsPlugin calendarsPlugin;


    @Override
    public Set<Integer> retainUsersWithPublicCalendars(Set<Integer> users) {
        Set<Integer> ret=new HashSet<>();
        for (Integer userId : users) {
            if(findUserCalendarsCount(userId,"/Config/public-plannings.xml")>0){
                ret.add(userId);
            }
        }
        return ret;
    }

    public List<CalendarWeek> findUserPrivateCalendars(int userId) {
        return findUserCalendars(userId, "/Config/private-plannings.xml");
    }

    public List<CalendarWeek> findUserPublicCalendars(int userId) {
        return findUserCalendars(userId, "/Config/public-plannings.xml");
    }

    public int findUserCalendarsCount(int userId, String path) {
        AppUser uuu = core.findUser(userId);
        int count=0;
        if (uuu == null) {
            return 0;
        }
//        String teacherName = uuu == null ? "" : uuu.resolveFullName();
        VFile p = core.getUserFolder(uuu.getLogin()).get(path);
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
                        count++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public List<CalendarWeek> findUserCalendars(int userId, String path) {
        AppUser uuu = core.findUser(userId);
        List<CalendarWeek> list = new ArrayList<>();
        if (uuu == null) {
            return list;
        }
//        String teacherName = uuu == null ? "" : uuu.resolveFullName();
        VFile p = core.getUserFolder(uuu.getLogin()).get(path);
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
                        CalendarWeek d = calendarsPlugin.parsePlanningDataXML(nNode, "Mon Planning");
                        if (d != null) {
                            d.setId("Custom-" + (temp + 1));
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
    public List<CalendarWeek> findCalendars(String type, String key) {
        if ("my-calendars".equals(type)) {
            AppUser user = core.getCurrentUser();
            if (user != null) {
                return calendarsPlugin.findUserPublicCalendars(user.getId(), true);
            }
        } else if ("user-calendars".equals(type)) {
            if (!StringUtils.isEmpty(key)) {
                key = key.trim();
                if (Character.isDigit(key.charAt(0))) {
                    return calendarsPlugin.findUserPublicCalendars(Integer.parseInt(key), true);
                }
                AppUser user = core.findUser(key);
                if (user != null) {
                    return calendarsPlugin.findUserPublicCalendars(user.getId(), true);
                }
            }
        }
        return Collections.EMPTY_LIST;
    }
}
