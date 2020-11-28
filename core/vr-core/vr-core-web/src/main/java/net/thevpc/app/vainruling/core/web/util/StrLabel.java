package net.thevpc.app.vainruling.core.web.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vpc on 12/19/16.
 */
public class StrLabel {
    private String type;
    private String kind;
    private String value;

    public StrLabel(String type, String kind,String value) {
        this.type = type;
        this.kind = kind;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getKind() {
        return kind;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StrLabel{" +
                "type='" + type + '\'' +
                ", kind='" + kind + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static List<StrLabel> extractLabels(String expr) {
        List<StrLabel> labels=new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<labelName>\\w+)[:]((?<kindName>\\w+)[:])?(([\"](?<labelVal1>[^\"])[\"])|(?<labelVal2>[^ ]+))");
        Matcher m = pattern.matcher(expr);
        while (m.find()) {
            String labelName = m.group("labelName");
            String kindName = m.group("kindName");
            String labelVal1 = m.group("labelVal1");
            String labelVal2 = m.group("labelVal2");
            if(labelVal1==null){
                labelVal1=labelVal2;
            }
            labels.add(new StrLabel(labelName,kindName==null?"":kindName,labelVal1));
        }
        return labels;
    }

}
