package net.vpc.app.vainruling;

import net.vpc.upa.Entity;

/**
 *
 *  Implementations must be annotated with @ForEntity(...).
 * <pre>
 * @Properties({
 * @Property(name = UIConstants.ENTITY_TEXT_SEARCH_FACTORY, value =
 * "full_name_of_your_class_here") } )
 * </pre>
 *
 * Created by vpc on 6/25/17.
 */
public interface VrEditorSearchFactory {

    String createHelperString(String name, Entity entity);

    VrEditorSearch create(String name, Entity entity, String expression);
}
