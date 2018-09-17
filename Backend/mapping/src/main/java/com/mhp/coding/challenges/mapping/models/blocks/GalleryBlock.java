package com.mhp.coding.challenges.mapping.models.blocks;

import com.mhp.coding.challenges.mapping.models.Image;

import java.util.List;

/**
 * @author Asdren Hoxha (MHP) - 17.09.18.
 */
public class GalleryBlock {

    private List<Image> images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
