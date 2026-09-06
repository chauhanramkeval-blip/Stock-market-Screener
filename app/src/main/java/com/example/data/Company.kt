package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class Company(
    @PrimaryKey
    @ColumnInfo(name = "isin")
    val isin: String,
    
    @ColumnInfo(name = "nse_symbol")
    val nseSymbol: String,
    
    @ColumnInfo(name = "company_name")
    val companyName: String,
    
    @ColumnInfo(name = "sector")
    val sector: String? = null,
    
    @ColumnInfo(name = "industry")
    val industry: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
