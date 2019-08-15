package net.vpc.app.vainruling.core.service.editor;

/**
 * Created by vpc on 4/15/17.
 */
public interface EntityEditorMainPhotoProvider {

    String getMainPhotoPath(Object id, Object valueOrNull);

    String getMainIconPath(Object id, Object valueOrNull);
}
