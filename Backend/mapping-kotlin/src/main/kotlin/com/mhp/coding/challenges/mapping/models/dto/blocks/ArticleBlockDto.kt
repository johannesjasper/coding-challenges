package com.mhp.coding.challenges.mapping.models.dto.blocks

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "blockType")
@JsonSubTypes(JsonSubTypes.Type(value = GalleryBlockDto::class, name = "gallery"),
    JsonSubTypes.Type(value = ImageBlockDto::class, name = "image"),
    JsonSubTypes.Type(value = TextBlockDto::class, name = "text"),
    JsonSubTypes.Type(value = VideoBlockDto::class, name = "video"),
    JsonSubTypes.Type(value = InvalidBlockDto::class, name = "invalid"))
interface ArticleBlockDto {
    val sortIndex: Int
}
