#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;
import net.vpc.upa.types.*;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;

/**
 * @author me
 */
//uncomment @Entity to enable Table creation!
//@Entity
@Path("/CustomPlugins")
public class SampleEntityGroup {

    @Id @Sequence
    private int id;

    @Main @Unique
    private String name;

    //add getters and setters
}
