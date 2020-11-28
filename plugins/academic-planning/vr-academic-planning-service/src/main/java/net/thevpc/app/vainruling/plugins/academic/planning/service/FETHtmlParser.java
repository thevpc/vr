//package net.thevpc.app.vainruling.plugins.academic.planning.service;
//
//import net.thevpc.common.vfs.VFile;
//import net.thevpc.upa.Closeable;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import net.thevpc.app.vainruling.core.service.util.VrUtils;
//import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsUtils;
//import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarActivity;
//import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
//import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;
//import net.thevpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;
//
///**
// * Created by vpc on 10/12/16.
// */
//public class FETHtmlParser implements Closeable {
//
//    private VFile p;
//    private String sourceName;
//    private String tagName;
//    private NodeList nList;
//    private InputStream inputStream;
//    private int pos = -1;
//    private Document doc;
//    private Iterator<org.jsoup.nodes.Element> tables;
//
//    public FETHtmlParser(VFile p, String tagName, String sourceName) {
//        this.p = p;
//        this.sourceName = sourceName;
//        this.tagName = tagName;
//
//    }
//
//    public static class CalendarWeekParser2 {
//
//        private org.jsoup.nodes.Element table;
//        private String roomName = null;
//
//        public CalendarWeekParser2(org.jsoup.nodes.Element table) {
//            this.table = table;
//            roomName = table.child(1).child(0).child(1).text().trim();
//        }
//
//        public String getName() {
//            return roomName;
//        }
//
//        public WeekCalendar parse() {
//            WeekCalendar w = new WeekCalendar();
//            w.setPlanningName(roomName);
//            w.setSourceName(roomName);
//            w.setPlanningUniformName(VrUtils.normalizeName(roomName));
//            w.setId(w.getPlanningUniformName());
//            w.setDays(new ArrayList<>());
//            Elements trs = table.child(2).children();
//            for (org.jsoup.nodes.Element tr : trs) {
//                Elements tds = tr.children();
//                for (org.jsoup.nodes.Element td : tds) {
//                    String t = td.text().trim();
//                    if(t.equals("---")){
//                        t="";
//                    }
//                    String[] yy = Arrays.stream(t.split("<br>")).map(x->x.trim()).toArray(String[]::new);
//                    CalendarActivity act=new CalendarActivity();
//                    act.setStudents(yy.length>0?yy[0]:"");
//                    act.setActor(yy.length>1?yy[1]:"");
//                    act.setSubject(yy.length>2?yy[2]:"");
//                    act.setRoom("");
//                    CalendarHour ch=new CalendarHour();
//                    ch.setActivities(new ArrayList<>(Arrays.asList(act)));
//                    
//                    CalendarDay cd=new CalendarDay();
//                    cd.set
//                }
//            }
//            return CalendarsUtils.buildWeekCalendar(w);
//        }
//    }
//
//    public CalendarWeekParser2 next() {
//        try {
//            if (tables == null) {
//                if (doc == null) {
//                    doc = Jsoup.parse(p.getInputStream(), null, null);
//                    String title = doc.title();
//                    String body = doc.body().text();
//                    tables = doc.body().getElementsByAttributeValueMatching("id", "table_.+").iterator();
//                } else {
//                    return null;
//                }
//            }
//            if (tables.hasNext()) {
//                return new CalendarWeekParser2(tables.next());
//            } else {
//                tables = null;
//                return null;
//            }
//
//            if () {
//                if (nList == null) {
//                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//                    inputStream = p.getInputStream();
//                    Document doc = dBuilder.parse(inputStream);
//                    //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
//                    doc.getDocumentElement().normalize();
//
//                    nList = doc.getElementsByTagName(tagName);
//                    pos = 0;
//                }
//            }
//            while (pos < nList.getLength()) {
//                Node nNode = nList.item(pos);
//                pos++;
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//                    String tn = eElement.getAttribute("name");
//                    return new CalendarWeekParser(tn.trim(), sourceName, nNode);
//                }
//            }
//            close();
//            return null;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void close() {
//        if (inputStream != null) {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
