package com.mhp.coding.challenges.mapping.services

import com.mhp.coding.challenges.mapping.mappers.ArticleMapper
import com.mhp.coding.challenges.mapping.models.dto.ArticleDto
import com.mhp.coding.challenges.mapping.repositories.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val mapper: ArticleMapper,
) {
    fun list(): List<ArticleDto> {
        val articles = ArticleRepository.all()
        return articles.asSequence().map { mapper.map(it) }.toList()
    }

    fun articleForId(id: Long): ArticleDto? {
        val article = ArticleRepository.findBy(id)
        return article?.let { mapper.map(it) }
    }

    fun create(articleDto: ArticleDto): ArticleDto {
        val article = mapper.map(articleDto)
        ArticleRepository.create(article)
        return mapper.map(article)
    }
}
