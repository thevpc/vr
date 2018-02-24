package net.vpc.app.vainruling.plugins.academic.planning.service;

import net.vpc.common.vfs.VFile;
import net.vpc.upa.Closeable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vpc on 10/12/16.
 */
public class FETXmlParser implements Closeable {
    private VFile p;
    private String sourceName;
    private String tagName;
    private NodeList nList;
    private InputStream inputStream;
    private int pos = -1;

    public FETXmlParser(VFile p, String tagName, String sourceName) {
        this.p = p;
        this.sourceName = sourceName;
        this.tagName = tagName;
    }

    public CalendarWeekParser next() {
        try {
            if (nList == null) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                inputStream = p.getInputStream();
                Document doc = dBuilder.parse(inputStream);
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                nList = doc.getElementsByTagName(tagName);
                pos = 0;
            }
            while (pos < nList.getLength()) {
                Node nNode = nList.item(pos);
                pos++;
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String tn = eElement.getAttribute("name");
                    return new CalendarWeekParser(tn.trim(), sourceName, nNode);
                }
            }
            close();
            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
