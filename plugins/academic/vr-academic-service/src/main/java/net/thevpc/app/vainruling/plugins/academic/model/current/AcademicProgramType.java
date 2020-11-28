/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.current;

import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.FormulaType;
import net.thevpc.upa.ProtectionLevel;
import net.thevpc.upa.UserFieldModifier;
import net.thevpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Repository/Education")
public class AcademicProgramType {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    @Unique
    private String name;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Field(protectionLevel = ProtectionLevel.PROTECTED,excludeModifiers = UserFieldModifier.UPDATE)
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicProgramType() {
    }

    public AcademicProgramType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

}
