package net.vpc.app.vainruling.core.service.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 8/26/16.
 */
public class SelectableObject<T> {
    private boolean selected;
    private T value;

    public SelectableObject(T value,boolean selected) {
        this.selected = selected;
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static <T> List<SelectableObject<T>> wrap(List<T> list){
        List<SelectableObject<T>> selList=new ArrayList<>(list.size());
        for (T t : list) {
            selList.add(new SelectableObject<T>(t,false));
        }
        return selList;
    }

    public static <T> List<T> unwrapSelected(List<SelectableObject<T>> list){
        List<T> selList=new ArrayList<>(list.size());
        for (SelectableObject<T> t : list) {
            if(t.isSelected()) {
                selList.add(t.getValue());
            }
        }
        return selList;
    }
    public static <T> List<T> unwrapAll(List<SelectableObject<T>> list){
        List<T> selList=new ArrayList<>(list.size());
        for (SelectableObject<T> t : list) {
            selList.add(t.getValue());
        }
        return selList;
    }
}
