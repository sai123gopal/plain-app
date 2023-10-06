package com.ismartcoding.plain.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ismartcoding.plain.Constants
import com.ismartcoding.plain.MainApp

@Database(
    entities = [
        DChat::class, DBox::class, DVocabulary::class, DSession::class, DTag::class, DTagRelation::class,
        DNote::class, DFeed::class, DFeedEntry::class, DBook::class, DBookChapter::class, DAIChat::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DateConverter::class, StringListConverter::class, ChatItemContentConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    abstract fun boxDao(): BoxDao

    abstract fun vocabularyDao(): VocabularyDao

    abstract fun sessionDao(): SessionDao

    abstract fun tagDao(): TagDao

    abstract fun tagRelationDao(): TagRelationDao

    abstract fun noteDao(): NoteDao

    abstract fun feedDao(): FeedDao

    abstract fun feedEntryDao(): FeedEntryDao

    abstract fun bookDao(): BookDao

    abstract fun aiChatDao(): AIChatDao

    companion object {
        @Volatile
        private var _instance: AppDatabase? = null

        val instance: AppDatabase
            get() {
                return _instance ?: synchronized(this) {
                    _instance ?: buildDatabase(MainApp.instance).also { _instance = it }
                }
            }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            DataInitializer(context, db).apply {
                                insertWelcome()
                                insertTags()
                                insertNotes()
                            }
                        }
                    },
                )
                .build()
        }
    }
}
