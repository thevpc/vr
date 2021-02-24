package net.thevpc.app.vainruling.core.service.content;

/**
 * Created by vpc on 9/5/16.
 */
public interface VrContentPath {

    public String getName();

    public String getPath();
    
    /**
     * path to the link when image
     * @return 
     */
    public String getLinkPath();

    public boolean isImage();
    
    public int getImageWidth();

    public int getImageHeight();

    public String getThumbnailPath();

    public int getThumbnailWidth();

    public int getThumbnailHeight();

    public String getStyle();
}
