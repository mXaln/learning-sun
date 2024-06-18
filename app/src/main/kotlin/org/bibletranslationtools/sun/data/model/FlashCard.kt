package org.bibletranslationtools.sun.data.model

import java.util.UUID

data class FlashCard(
    val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var description: String? = null
)