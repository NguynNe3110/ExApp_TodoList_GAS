package com.example.domain.model

data class Category(
    val id: String,
    val name: String,
    val colorHex: String,
    val iconName: String
) {
    companion object {
        // Default categories requested by the user: "học tập" (learning), "việc vặt" (chores), etc.
        val DEFAULT_CATEGORIES = listOf(
            Category("1", "Việc vặt", "#F9FFF5", "home"),
        )
    }
}
