package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorHex: String,
    val iconName: String
) {
    fun toDomain() = Category(
        id = id,
        name = name,
        colorHex = colorHex,
        iconName = iconName
    )

    companion object {
        fun fromDomain(category: Category) = CategoryEntity(
            id = category.id,
            name = category.name,
            colorHex = category.colorHex,
            iconName = category.iconName
        )
    }
}
