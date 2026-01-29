package com.fittrack.app.data.local.database

import androidx.room.TypeConverter
import com.fittrack.app.data.local.database.entity.MealType
import com.fittrack.app.data.local.database.entity.WorkoutType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    
    // --- LocalDate ---
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    // --- LocalDateTime ---
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    // --- LocalTime ---
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let {
            LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
        }
    }

    // --- WorkoutType Enum ---
    @TypeConverter
    fun fromWorkoutType(type: WorkoutType): String {
        return type.name
    }

    @TypeConverter
    fun toWorkoutType(typeString: String): WorkoutType {
        return WorkoutType.valueOf(typeString)
    }

    // --- MealType Enum ---
    @TypeConverter
    fun fromMealType(type: MealType): String {
        return type.name
    }

    @TypeConverter
    fun toMealType(typeString: String): MealType {
        return MealType.valueOf(typeString)
    }
}
