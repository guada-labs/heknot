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
}
