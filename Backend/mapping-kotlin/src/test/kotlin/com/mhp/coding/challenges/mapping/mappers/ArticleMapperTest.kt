package com.mhp.coding.challenges.mapping.mappers

import com.mhp.coding.challenges.mapping.ApplicationTests
import com.mhp.coding.challenges.mapping.models.db.blocks.ArticleBlock
import com.mhp.coding.challenges.mapping.models.db.blocks.GalleryBlock
import com.mhp.coding.challenges.mapping.models.db.blocks.TextBlock
import com.mhp.coding.challenges.mapping.models.dto.blocks.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnmappedTestBlock(
    override val sortIndex: Int = 0,
) : ArticleBlock(sortIndex)

class ArticleMapperTest : ApplicationTests() {

    @Nested
    inner class `mapping an article` {

        @Test
        fun `applies ordering to blocks`() {
            val article = createArticle(setOf(TextBlock(text = "Some Text", sortIndex = 1),
                TextBlock(text = "Some Text", sortIndex = 0)))

            val dto = articleMapper.map(article)

            assertThat(dto.blocks.size).isEqualTo(2)
            assertThat(dto.blocks.map { it.sortIndex }.toList()).isEqualTo(listOf(0, 1))
        }

        @Test
        fun `maps text blocks`() {
            val article = createArticle(setOf(textBlock))

            val dto = articleMapper.map(article)
            assertThat(dto.blocks.size).isEqualTo(1)
            dto.blocks[0] is TextBlockDto
        }

        @Test
        fun `maps image blocks`() {
            val article = createArticle(setOf(imageBlock))

            val dto = articleMapper.map(article)
            assertThat(dto.blocks.size).isEqualTo(1)
            dto.blocks[0] is ImageBlockDto
        }

        @Test
        fun `filters invalid image blocks`() {
            val article = createArticle(setOf(invlaidImageBlock))

            val dto = articleMapper.map(article)
            assertThat(dto.blocks.size).isEqualTo(0)
        }

        @Test
        fun `maps gallery blocks`() {
            val article = createArticle(setOf(galleryBlock))

            val dto = articleMapper.map(article)
            assertThat(dto.blocks.size).isEqualTo(1)
            dto.blocks[0] is GalleryBlockDto
        }

        @Test
        fun `filters empty images in gallery blocks`() {
            val article = createArticle(setOf(GalleryBlock(images = listOf(image, null))))

            val dto = articleMapper.map(article)
            assertThat(dto.blocks.size).isEqualTo(1)
            dto.blocks[0].let {
                it is GalleryBlockDto
                it as GalleryBlockDto
                assertThat(it.images.size).isEqualTo(1)
            }
        }

        @Test
        fun `maps video blocks`() {
            val article = createArticle(setOf(videoBlock))

            val dto = articleMapper.map(article)
            assertThat(dto.blocks.size).isEqualTo(1)
            dto.blocks[0] is VideoBlockDto
        }


        @Test
        fun `highlights blocks without mapper`() {
            val article = createArticle(setOf(UnmappedTestBlock()))

            val dto = articleMapper.map(article)
            assertThat(dto.blocks.size).isEqualTo(1)
            dto.blocks[0] is InvalidBlockDto
        }
    }
}
