package com.mhp.coding.challenges.mapping.mappers

import com.mhp.coding.challenges.mapping.models.db.Article
import com.mhp.coding.challenges.mapping.models.db.Image
import com.mhp.coding.challenges.mapping.models.db.blocks.*
import com.mhp.coding.challenges.mapping.models.dto.ArticleDto
import com.mhp.coding.challenges.mapping.models.dto.ImageDto
import com.mhp.coding.challenges.mapping.models.dto.blocks.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class ArticleMapper {
    fun map(article: Article): ArticleDto = toDTO(article)

    // Not part of the challenge / Nicht Teil dieser Challenge.
    fun map(articleDto: ArticleDto?): Article =
        Article(title = "An Article", blocks = emptySet(), id = 1, lastModified = Date())


    fun toDTO(entity: Article): ArticleDto = ArticleDto(entity.id,
        entity.title,
        entity.description.orEmpty(),
        entity.author.orEmpty(),
        entity.blocks.mapNotNull { toDTO(it) }.sortedBy { it.sortIndex }.toList())

    fun toDTO(entity: Image): ImageDto = ImageDto(entity.id, entity.url, entity.imageSize)

    fun toDTO(entity: ArticleBlock): ArticleBlockDto? {
        return when (entity) {
            is GalleryBlock -> GalleryBlockDto(entity.images.filterNotNull().map { toDTO(it) }, entity.sortIndex)
            is ImageBlock -> entity.image?.let { // removes image blocks without image
                ImageBlockDto(toDTO(entity.image!!), entity.sortIndex)
            }
            is TextBlock -> TextBlockDto(entity.text, entity.sortIndex)
            is VideoBlock -> VideoBlockDto(entity.url, entity.type, entity.sortIndex)
            else -> InvalidBlockDto(entity.javaClass.name, entity.sortIndex)
        }
    }
}
