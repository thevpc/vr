package net.thevpc.app.vainruling;

import java.util.List;
import java.util.Map;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.upa.Document;

/**
 *
 * Implementations must be annotated with @ForEntity(...).
 * <pre>
 * @Properties({
 * @Property(name = UIConstants.ENTITY_TEXT_SEARCH_FACTORY, value =
 * "full_name_of_your_class_here") } )
 * </pre>
 *
 * Created by vpc on 6/25/17.
 */
public interface VrEditorSearch {

    default String getId() {
        return VrUtils.getBeanName(this);
    }


    default String getTitle() {
        return getName();
    }

    default String createHelperString(String name, String entityName) {
        return "Tapez ici les mots clés de recherche.";
    }

    default String createPreProcessingExpression(String entityName, Map<String, Object> parameters, String paramPrefix) {
        return null;
    }

    String getName();

    List filterDocumentList(List<Document> list, String entityName, String expression);

}
