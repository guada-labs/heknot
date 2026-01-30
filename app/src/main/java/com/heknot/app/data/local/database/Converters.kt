package com.heknot.app.data.local.database

import androidx.room.TypeConverter
import com.heknot.app.data.local.database.entity.MealType
import com.heknot.app.data.local.database.entity.WorkoutType
import com.heknot.app.data.local.database.entity.Gender
import com.heknot.app.data.local.database.entity.ActivityLevel
import com.heknot.app.data.local.database.entity.WorkoutCategory
import com.heknot.app.data.local.database.entity.FoodCategory
import com.heknot.app.data.local.database.entity.ServingUnit
import com.heknot.app.data.local.database.entity.RecipeDifficulty
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

    // --- Gender Enum ---
    @TypeConverter
    fun fromGender(gender: Gender?): String? {
        return gender?.name
    }

    @TypeConverter
    fun toGender(genderString: String?): Gender? {
        return genderString?.let { Gender.valueOf(it) }
    }

    // --- ActivityLevel Enum ---
    @TypeConverter
    fun fromActivityLevel(level: ActivityLevel): String {
        return level.name
    }

    @TypeConverter
    fun toActivityLevel(levelString: String): ActivityLevel {
        return ActivityLevel.valueOf(levelString)
    }

    // --- WorkoutCategory Enum ---
    @TypeConverter
    fun fromWorkoutCategory(category: WorkoutCategory): String {
        return category.name
    }

    @TypeConverter
    fun toWorkoutCategory(categoryString: String): WorkoutCategory {
        return WorkoutCategory.valueOf(categoryString)
    }

    // --- FitnessGoal Enum ---
    @TypeConverter
    fun fromFitnessGoal(goal: com.heknot.app.data.local.database.entity.FitnessGoal): String {
        return goal.name
    }

    @TypeConverter
    fun toFitnessGoal(goalString: String): com.heknot.app.data.local.database.entity.FitnessGoal {
        return com.heknot.app.data.local.database.entity.FitnessGoal.valueOf(goalString)
    }

    // --- FoodCategory Enum ---
    @TypeConverter
    fun fromFoodCategory(category: FoodCategory): String {
        return category.name
    }

    @TypeConverter
    fun toFoodCategory(categoryString: String): FoodCategory {
        return FoodCategory.valueOf(categoryString)
    }

    // --- ServingUnit Enum ---
    @TypeConverter
    fun fromServingUnit(unit: ServingUnit): String {
        return unit.name
    }

    @TypeConverter
    fun toServingUnit(unitString: String): ServingUnit {
        return ServingUnit.valueOf(unitString)
    }

    // --- RecipeDifficulty Enum ---
    @TypeConverter
    fun fromRecipeDifficulty(difficulty: RecipeDifficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toRecipeDifficulty(difficultyString: String): RecipeDifficulty {
        return RecipeDifficulty.valueOf(difficultyString)
    }
}
