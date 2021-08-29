package com.mhp.coding.challenges.mapping.models.dto.blocks

import com.mhp.coding.challenges.mapping.models.dto.ImageDto

data class ImageBlockDto(
    var image: ImageDto,
    override val sortIndex: Int,
) : ArticleBlockDto
