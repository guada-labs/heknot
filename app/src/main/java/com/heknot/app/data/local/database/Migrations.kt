package com.heknot.app.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `fitnessGoal` TEXT NOT NULL DEFAULT 'LOSE_WEIGHT'")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `neckCm` REAL")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `waistCm` REAL")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `hipCm` REAL")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `preferredUnit` TEXT NOT NULL DEFAULT 'METRIC'")
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `biometricEnabled` INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Crear nuevas tablas
            db.execSQL("CREATE TABLE IF NOT EXISTS `water_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amountMl` INTEGER NOT NULL, `date` TEXT NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS `sleep_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hours` REAL NOT NULL, `quality` INTEGER NOT NULL, `date` TEXT NOT NULL, `notes` TEXT)")
            db.execSQL("CREATE TABLE IF NOT EXISTS `guided_exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `durationSeconds` INTEGER, `repetitions` INTEGER, `imageResName` TEXT, `videoUrl` TEXT)")
        }
    }

    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Crear tablas food_items y recipes
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `food_items` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `name` TEXT NOT NULL, `brand` TEXT, `category` TEXT NOT NULL, 
                    `servingSize` REAL NOT NULL, `servingUnit` TEXT NOT NULL, 
                    `calories` INTEGER NOT NULL, `protein` REAL NOT NULL, 
                    `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fiber` REAL NOT NULL, 
                    `sugar` REAL NOT NULL, `sodium` REAL NOT NULL, `imageUrl` TEXT, 
                    `pixelArtGenerated` INTEGER NOT NULL, `isInPantry` INTEGER NOT NULL, 
                    `isFavorite` INTEGER NOT NULL, `isCustom` INTEGER NOT NULL, 
                    `createdAt` INTEGER NOT NULL, `lastUsed` INTEGER
                )
            """.trimIndent())
            
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `recipes` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `name` TEXT NOT NULL, `description` TEXT, 
                    `prepTimeMinutes` INTEGER NOT NULL, `cookTimeMinutes` INTEGER NOT NULL, 
                    `servings` INTEGER NOT NULL, `instructions` TEXT NOT NULL, 
                    `imageUrl` TEXT, `pixelArtGenerated` INTEGER NOT NULL, 
                    `mealType` TEXT NOT NULL, `difficulty` TEXT NOT NULL, 
                    `isFavorite` INTEGER NOT NULL, `timesCooked` INTEGER NOT NULL, 
                    `lastCooked` INTEGER, `isCustom` INTEGER NOT NULL, 
                    `createdAt` INTEGER NOT NULL
                )
            """.trimIndent())

            // Refactorizar meal_logs (Room no soporta ALTER TABLE DROP COLUMN o RENAME COLUMN fácilmente, 
            // pero para añadir columnas y FKs podemos usar una tabla temporal si es complejo, 
            // o simplemente recrear si estamos seguros de que no hay datos críticos en esta fase prematura)
            db.execSQL("DROP TABLE IF EXISTS `meal_logs` ")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `meal_logs` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `dateTime` TEXT NOT NULL, `type` TEXT NOT NULL, 
                    `foodItemId` INTEGER, `recipeId` INTEGER, `servings` REAL NOT NULL, 
                    `description` TEXT, `calories` INTEGER NOT NULL, `protein` REAL NOT NULL, 
                    `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `imageUrl` TEXT, 
                    `userFeedback` TEXT, `satisfactionLevel` INTEGER, 
                    `detectedByAI` INTEGER NOT NULL, `aiConfidence` REAL, 
                    `isPlanned` INTEGER NOT NULL, `isPast` INTEGER NOT NULL, 
                    FOREIGN KEY(`foodItemId`) REFERENCES `food_items`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , 
                    FOREIGN KEY(`recipeId`) REFERENCES `recipes`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL 
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_meal_logs_foodItemId` ON `meal_logs` (`foodItemId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_meal_logs_recipeId` ON `meal_logs` (`recipeId`)")

            // Renombrar columna en water_logs (Simulado con DROP/CREATE o ALTER si es simple)
            // El esquema 11 muestra 'dateTime' en lugar de 'date'
            db.execSQL("ALTER TABLE `water_logs` RENAME COLUMN `date` TO `dateTime` ")
        }
    }

    val MIGRATION_11_12 = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `recipe_ingredients` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `recipeId` INTEGER NOT NULL, 
                    `foodItemId` INTEGER NOT NULL, 
                    `amount` REAL NOT NULL, 
                    `unit` TEXT NOT NULL, 
                    `notes` TEXT, 
                    `isOptional` INTEGER NOT NULL, 
                    FOREIGN KEY(`recipeId`) REFERENCES `recipes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, 
                    FOREIGN KEY(`foodItemId`) REFERENCES `food_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_recipeId` ON `recipe_ingredients` (`recipeId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_foodItemId` ON `recipe_ingredients` (`foodItemId`)")
        }
    }

    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `waterGoal` INTEGER")
            // El esquema 13 muestra que water_logs ahora tiene 'type'
            db.execSQL("ALTER TABLE `water_logs` ADD COLUMN `type` TEXT NOT NULL DEFAULT 'WATER'")
        }
    }

    val MIGRATION_13_14 = object : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `chestCm` REAL")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `armCm` REAL")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `thighCm` REAL")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `calfCm` REAL")
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `bodyFatPercentage` REAL")
        }
    }

    val MIGRATION_14_15 = object : Migration(14, 15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // El esquema 15 muestra que weight_entries ahora tiene mediciones corporales
            db.execSQL("ALTER TABLE `weight_entries` ADD COLUMN `neckCm` REAL")
            db.execSQL("ALTER TABLE `weight_entries` ADD COLUMN `waistCm` REAL")
            db.execSQL("ALTER TABLE `weight_entries` ADD COLUMN `hipCm` REAL")
            db.execSQL("ALTER TABLE `weight_entries` ADD COLUMN `chestCm` REAL")
            db.execSQL("ALTER TABLE `weight_entries` ADD COLUMN `armCm` REAL")
            db.execSQL("ALTER TABLE `weight_entries` ADD COLUMN `thighCm` REAL")
            db.execSQL("ALTER TABLE `weight_entries` ADD COLUMN `calfCm` REAL")
        }
    }
    
    val MIGRATION_15_16 = object : Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Re-crear tablas para asegurar que coincidan con las nuevas entidades
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `user_equipment` (
                    `equipmentId` TEXT NOT NULL, 
                    `name` TEXT NOT NULL, 
                    `isAvailable` INTEGER NOT NULL, 
                    PRIMARY KEY(`equipmentId`)
                )
            """.trimIndent())
            
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `workout_plans` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `title` TEXT NOT NULL, 
                    `description` TEXT NOT NULL, 
                    `difficulty` TEXT NOT NULL, 
                    `goal` TEXT NOT NULL, 
                    `durationWeeks` INTEGER NOT NULL, 
                    `suggestedEquipment` TEXT NOT NULL
                )
            """.trimIndent())
            
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `workout_routines` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `planId` INTEGER NOT NULL, 
                    `dayNumber` INTEGER NOT NULL, 
                    `title` TEXT NOT NULL, 
                    `description` TEXT, 
                    `isCardioBlock` INTEGER NOT NULL, 
                    FOREIGN KEY(`planId`) REFERENCES `workout_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                )
            """.trimIndent())
            
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_routines_planId` ON `workout_routines` (`planId`)")
            
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `routine_exercises` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `routineId` INTEGER NOT NULL, 
                    `exerciseId` TEXT NOT NULL, 
                    `alternativeExerciseId` TEXT, 
                    `sets` INTEGER NOT NULL, 
                    `reps` INTEGER, 
                    `durationSeconds` INTEGER, 
                    `restSeconds` INTEGER NOT NULL, 
                    `orderInRoutine` INTEGER NOT NULL, 
                    FOREIGN KEY(`routineId`) REFERENCES `workout_routines`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                )
            """.trimIndent())
            
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_exercises_routineId` ON `routine_exercises` (`routineId`)")
        }
    }
    
    // NOTA: Es posible que necesite corregir los tipos de datos generados por Room si hay discrepancias.
}
