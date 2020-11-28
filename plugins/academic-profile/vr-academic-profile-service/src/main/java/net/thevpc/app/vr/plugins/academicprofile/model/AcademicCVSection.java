package net.thevpc.app.vr.plugins.academicprofile.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.config.*;

import java.util.Objects;

/**
 * Created by vpc on 7/19/17.
 */
@Entity
@Path("Education")
public class AcademicCVSection {

    @Id
    @Sequence
    private int id;
    private String code;
    @Main
    private String title;

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String details;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public AcademicCVSection setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicCVSection that = (AcademicCVSection) o;
        return id == that.id &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}
