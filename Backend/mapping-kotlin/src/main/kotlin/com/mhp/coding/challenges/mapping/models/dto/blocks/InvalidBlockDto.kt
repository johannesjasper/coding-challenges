package com.mhp.coding.challenges.mapping.models.dto.blocks

data class InvalidBlockDto(
    var originalType:String,
    override val sortIndex: Int
) : ArticleBlockDto
