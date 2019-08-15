package net.vpc.app.vainruling.core.service.editor;

import net.vpc.app.vainruling.core.service.editor.EntityEditorSearch;
import net.vpc.upa.Entity;

/**
 *
 * <pre>
 * @Properties({
 * @Property(name = UIConstants.ENTITY_TEXT_SEARCH_FACTORY, value =
 * "full_name_of_your_class_here") } )
 * </pre>
 *
 * Created by vpc on 6/25/17.
 */
public interface EntityEditorSearchFactory {

    String createHelperString(String name, Entity entity);

    EntityEditorSearch create(String name, Entity entity, String expression);
}
