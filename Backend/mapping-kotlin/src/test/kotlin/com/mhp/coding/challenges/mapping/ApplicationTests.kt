package com.mhp.coding.challenges.mapping

import com.mhp.coding.challenges.mapping.mappers.ArticleMapper
import com.mhp.coding.challenges.mapping.models.db.Article
import com.mhp.coding.challenges.mapping.models.db.Image
import com.mhp.coding.challenges.mapping.models.db.ImageSize
import com.mhp.coding.challenges.mapping.models.db.blocks.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ApplicationTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var articleMapper: ArticleMapper


    fun createArticle(blocks: Set<ArticleBlock> = emptySet()) = Article(
        id = 1L,
        lastModified = Date(),
        lastModifiedBy = "Hans Müller",
        title = "Article Nr.: 1",
        description = "Article Description 1",
        author = "Max Mustermann",
        blocks = blocks,
    )

    final val image = Image(url = "https://someurl.com/image/1",
        id = 1,
        imageSize = ImageSize.LARGE,
        lastModified = Date(),
        lastModifiedBy = "John Doe")

    final val textBlock = TextBlock(text = "Some Text")
    final val imageBlock = ImageBlock(image = image)
    final val invlaidImageBlock = ImageBlock(image = null)
    final val galleryBlock = GalleryBlock(images = listOf(image))
    final val videoBlock = VideoBlock(type = VideoBlockType.YOUTUBE, url = "https://youtu.be/myvideo")
}
