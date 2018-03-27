#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${vr-groupId}.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;
import net.vpc.upa.types.*;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;


/**
 * @author me
 */
//uncomment @Entity to enable Table creation!
//@Entity(listOrder = "name")
@Path("/CustomPlugins")
@Properties(
        {
                @Property(name = "ui.auto-filter.group", value = "{expr='this.group',order=1}")
        })
public class SampleEntity {

    @Id @Sequence
    private int id;

    @Main @Unique
    private String name;

    @Summary
    private String title;

    private SampleEntityGroup group;

    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "512")
    private String observation;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private DateTime creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private DateTime updateDate;

    //add getters and setters
}
