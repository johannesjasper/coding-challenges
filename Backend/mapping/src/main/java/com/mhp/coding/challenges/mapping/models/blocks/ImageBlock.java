package com.mhp.coding.challenges.mapping.models.blocks;

import com.mhp.coding.challenges.mapping.models.Image;

/**
 * @author Asdren Hoxha (MHP) - 17.09.18.
 */
public class ImageBlock extends ArticleBlock {

    private Image image;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
