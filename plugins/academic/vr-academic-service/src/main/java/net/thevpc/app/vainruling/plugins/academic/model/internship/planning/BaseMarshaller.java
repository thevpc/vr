package net.thevpc.app.vainruling.plugins.academic.model.internship.planning;

import org.jgap.Configuration;

/**
 * Created by vpc on 5/20/16.
 */
public abstract class BaseMarshaller implements ChromosomeMarshaller {
    private int activity;
    private int index;
    private Configuration conf;

    public BaseMarshaller(int activity, int index, Configuration conf) {
        this.activity = activity;
        this.index = index;
        this.conf = conf;
    }

    public int getActivity() {
        return activity;
    }

    public int getIndex() {
        return index;
    }

    public Configuration getConf() {
        return conf;
    }
}
