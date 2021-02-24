/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model.content;

/**
 *
 * @author vpc
 */
public class VrContentTextConfig {

    private int imageWidth;
    private int imageHeight;
    private int thumbnailWidth;
    private int thumbnailHeight;

    public int getImageWidth() {
        return imageWidth;
    }

    public VrContentTextConfig setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
        return this;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public VrContentTextConfig setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
        return this;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public VrContentTextConfig setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
        return this;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public VrContentTextConfig setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
        return this;
    }

}
