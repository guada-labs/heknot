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
}
