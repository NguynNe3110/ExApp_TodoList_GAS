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
            Category("1", "Học tập", "#E6FFFF", "school"),
            Category("2", "Việc vặt", "#F9FFF5", "home"),
            Category("3", "Cá nhân", "#FFFDF0", "person"),
            Category("4", "Công việc", "#FFF5F5", "work")
        )
    }
}
