package com.mhp.coding.challenges.mapping

import com.mhp.coding.challenges.mapping.models.dto.ArticleDto
import com.mhp.coding.challenges.mapping.repositories.ArticleRepository
import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import java.net.URI


class IntegrationTest : ApplicationTests() {


    @BeforeEach
    fun before() {
        mockkObject(ArticleRepository)
    }

    @Nested
    inner class `getting one article` {

        private fun getArticle(): ArticleDto {
            val entity = restTemplate.getForEntity<ArticleDto>("/article/1")
            assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(entity.body).isNotNull
            return entity.body!!
        }

        @Test
        fun `returns 404 for non-existent articles`() {
            every { ArticleRepository.findBy(1) } returns null

            val entity = restTemplate.getForEntity<String>("/article/1")
            assertThat(entity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
            println(entity)
        }

        @Test
        fun `returns the article`() {
            every { ArticleRepository.findBy(1) } returns createArticle(setOf(textBlock))

            val article = getArticle()
            assertThat(article.blocks.size).isEqualTo(1)
        }
    }

    @Nested
    inner class `getting all articles` {
        private fun getArticles(): List<ArticleDto> {
            val requestEntity = RequestEntity<Any>(HttpMethod.GET, URI.create("/article"))
            val entity = restTemplate.exchange(requestEntity, typeReference<List<ArticleDto>>())

            assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(entity.body).isNotNull
            return entity.body!!
        }


        @Test
        fun `lists all articles`() {
            every { ArticleRepository.all() } returns listOf(createArticle(), createArticle())

            val articles = getArticles()
            assertThat(articles.size).isEqualTo(2)
        }

        @Test
        fun `lists emtpy sets`() {
            every { ArticleRepository.all() } returns emptyList()

            val articles = getArticles()
            assertThat(articles.size).isEqualTo(0)
        }
    }
}
