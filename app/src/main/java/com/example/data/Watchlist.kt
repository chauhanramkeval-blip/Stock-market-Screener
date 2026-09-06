package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlist",
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["isin"],
            childColumns = ["company_isin"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Watchlist(
    @PrimaryKey
    @ColumnInfo(name = "company_isin")
    val companyIsin: String,
    
    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
