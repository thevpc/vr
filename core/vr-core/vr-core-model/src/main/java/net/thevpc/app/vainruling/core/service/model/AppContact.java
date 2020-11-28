/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.config.*;


/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.fullName")
@Path("Contact")
@Properties(
        {
                        @Property(name = UIConstants.Grid.ROW_STYLE, value = "(i.object.deleted) ?'vr-row-deleted':''")
            ,@Property(name = "ui.auto-filter.company", value = "{expr='this.company',order=1}")
            ,
                @Property(name = "ui.auto-filter.positionTitle1", value = "{expr='this.positionTitle1',order=2}")
            ,
//                @Property(name = "ui.auto-filter.company", value = "{expr='this.company',order=3}")
        }
)
public class AppContact extends AppPersonContactBase {

}
