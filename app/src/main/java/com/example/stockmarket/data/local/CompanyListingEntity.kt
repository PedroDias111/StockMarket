package com.example.stockmarket.data.local

import android.icu.text.SymbolTable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CompanyListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val symbol: String,
    val name: String,
    val exchange: String
)
