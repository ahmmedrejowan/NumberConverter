package com.rejowan.numberconverter.data.local.database.converter

import androidx.room.TypeConverter
import com.rejowan.numberconverter.domain.model.NumberBase

class NumberBaseConverter {
    @TypeConverter
    fun fromNumberBase(value: NumberBase): String {
        return value.name
    }

    @TypeConverter
    fun toNumberBase(value: String): NumberBase {
        return NumberBase.valueOf(value)
    }
}
