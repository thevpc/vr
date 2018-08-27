package net.vpc.app.vainruling.core.service.obj;

/**
 * Created by vpc on 4/15/17.
 */
public interface MainPhotoProvider {
    String getMainPhotoPath(Object id,Object valueOrNull);
    String getMainIconPath(Object id, Object valueOrNull);
}
