package com.heknot.app.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add fields to user_profile table
            db.execSQL("ALTER TABLE user_profile ADD COLUMN fitnessGoal TEXT NOT NULL DEFAULT 'MAINTAIN_WEIGHT'")
            db.execSQL("ALTER TABLE user_profile ADD COLUMN neckCm REAL")
            db.execSQL("ALTER TABLE user_profile ADD COLUMN waistCm REAL")
            db.execSQL("ALTER TABLE user_profile ADD COLUMN hipCm REAL")
            db.execSQL("ALTER TABLE user_profile ADD COLUMN preferredUnit TEXT NOT NULL DEFAULT 'kg'")
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Update user_profile
            db.execSQL("ALTER TABLE user_profile ADD COLUMN biometricEnabled INTEGER NOT NULL DEFAULT 0")
            
            // Create water_logs
            db.execSQL("CREATE TABLE IF NOT EXISTS `water_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amountMl` INTEGER NOT NULL, `date` TEXT NOT NULL)")
            
            // Create sleep_logs
            db.execSQL("CREATE TABLE IF NOT EXISTS `sleep_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hours` REAL NOT NULL, `quality` INTEGER NOT NULL, `date` TEXT NOT NULL, `notes` TEXT)")
        }
    }

    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `guided_exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `durationSeconds` INTEGER, `repetitions` INTEGER, `imageResName` TEXT, `videoUrl` TEXT)")
        }
    }

    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create food_items table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `food_items` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `brand` TEXT,
                    `category` TEXT NOT NULL,
                    `servingSize` REAL NOT NULL,
                    `servingUnit` TEXT NOT NULL,
                    `calories` INTEGER NOT NULL,
                    `protein` REAL NOT NULL,
                    `carbs` REAL NOT NULL,
                    `fat` REAL NOT NULL,
                    `fiber` REAL NOT NULL,
                    `sugar` REAL NOT NULL,
                    `sodium` REAL NOT NULL,
                    `imageUrl` TEXT,
                    `pixelArtGenerated` INTEGER NOT NULL,
                    `isInPantry` INTEGER NOT NULL,
                    `isFavorite` INTEGER NOT NULL,
                    `isCustom` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `lastUsed` INTEGER
                )
            """)
            
            // Create recipes table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `recipes` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `description` TEXT,
                    `prepTimeMinutes` INTEGER NOT NULL,
                    `cookTimeMinutes` INTEGER NOT NULL,
                    `servings` INTEGER NOT NULL,
                    `instructions` TEXT NOT NULL,
                    `imageUrl` TEXT,
                    `pixelArtGenerated` INTEGER NOT NULL,
                    `mealType` TEXT NOT NULL,
                    `difficulty` TEXT NOT NULL,
                    `isFavorite` INTEGER NOT NULL,
                    `timesCooked` INTEGER NOT NULL,
                    `lastCooked` INTEGER,
                    `isCustom` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
            """)
            
            // Create recipe_ingredients junction table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `recipe_ingredients` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `recipeId` INTEGER NOT NULL,
                    `foodItemId` INTEGER NOT NULL,
                    `amount` REAL NOT NULL,
                    `unit` TEXT NOT NULL,
                    `notes` TEXT,
                    `isOptional` INTEGER NOT NULL,
                    FOREIGN KEY(`recipeId`) REFERENCES `recipes`(`id`) ON DELETE CASCADE,
                    FOREIGN KEY(`foodItemId`) REFERENCES `food_items`(`id`) ON DELETE CASCADE
                )
            """)
            
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_recipeId` ON `recipe_ingredients` (`recipeId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_foodItemId` ON `recipe_ingredients` (`foodItemId`)")
            
            // Update meal_logs table - create new table and copy data
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `meal_logs_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `dateTime` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `foodItemId` INTEGER,
                    `recipeId` INTEGER,
                    `servings` REAL NOT NULL,
                    `description` TEXT,
                    `calories` INTEGER NOT NULL,
                    `protein` REAL NOT NULL,
                    `carbs` REAL NOT NULL,
                    `fat` REAL NOT NULL,
                    `imageUrl` TEXT,
                    `userFeedback` TEXT,
                    `satisfactionLevel` INTEGER,
                    `detectedByAI` INTEGER NOT NULL,
                    `aiConfidence` REAL,
                    `isPlanned` INTEGER NOT NULL,
                    `isPast` INTEGER NOT NULL,
                    FOREIGN KEY(`foodItemId`) REFERENCES `food_items`(`id`) ON DELETE SET NULL,
                    FOREIGN KEY(`recipeId`) REFERENCES `recipes`(`id`) ON DELETE SET NULL
                )
            """)
            
            // Copy existing data
            db.execSQL("""
                INSERT INTO `meal_logs_new` (
                    `id`, `dateTime`, `type`, `description`, `calories`, `protein`,
                    `servings`, `carbs`, `fat`, `detectedByAI`, `isPlanned`, `isPast`
                )
                SELECT 
                    `id`, `dateTime`, `type`, `description`, 
                    COALESCE(`calories`, 0), COALESCE(`protein`, 0),
                    1.0, 0.0, 0.0, 0, 0, 1
                FROM `meal_logs`
            """)
            
            db.execSQL("DROP TABLE `meal_logs`")
            db.execSQL("ALTER TABLE `meal_logs_new` RENAME TO `meal_logs`")
            
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_meal_logs_foodItemId` ON `meal_logs` (`foodItemId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_meal_logs_recipeId` ON `meal_logs` (`recipeId`)")
        }
    }
    val MIGRATION_11_12 = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create new water_logs table with dateTime
            db.execSQL("CREATE TABLE IF NOT EXISTS `water_logs_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amountMl` INTEGER NOT NULL, `dateTime` TEXT NOT NULL)")
            
            // Copy data, appending default time T00:00:00 to the date string
            db.execSQL("""
                INSERT INTO `water_logs_new` (`id`, `amountMl`, `dateTime`)
                SELECT `id`, `amountMl`, `date` || 'T00:00:00' FROM `water_logs`
            """)
            
            // Drop old table and rename new one
            db.execSQL("DROP TABLE `water_logs`")
            db.execSQL("ALTER TABLE `water_logs_new` RENAME TO `water_logs`")
        }
    }

    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add waterGoal to UserProfile
            db.execSQL("ALTER TABLE `user_profile` ADD COLUMN `waterGoal` INTEGER")
            
            // Add type to WaterLog default to WATER
            db.execSQL("ALTER TABLE `water_logs` ADD COLUMN `type` TEXT NOT NULL DEFAULT 'WATER'")
        }
    }
}
