package net.thevpc.app.vainruling.plugins.academic.web.load;

import net.thevpc.app.vainruling.plugins.academic.service.stat.TeacherBaseStat;

import java.util.List;

/**
 * Created by vpc on 7/2/16.
 */
public class TeacherBaseStatTable {
    private String title;
    private List<TeacherBaseStat> rows;

    public TeacherBaseStatTable(String title, List<TeacherBaseStat> rows) {
        this.title = title;
        this.rows = rows;
    }

    public String getTitle() {
        return title;
    }

    public List<TeacherBaseStat> getRows() {
        return rows;
    }
}
