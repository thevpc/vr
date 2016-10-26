package net.vpc.app.vainruling.core.service.model.content;

import net.vpc.app.vainruling.core.service.content.ContentPath;

/**
 * Created by vpc on 9/5/16.
 */
public class DefaultContentPath implements ContentPath {
    private ArticlesFile file;

    public DefaultContentPath(ArticlesFile file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getPath() {
        return file.getPath();
    }
    @Override
    public String getStyle() {
        return file.getStyle();
    }
}
