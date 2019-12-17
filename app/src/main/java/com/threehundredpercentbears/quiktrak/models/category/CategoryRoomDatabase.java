package com.threehundredpercentbears.quiktrak.models.category;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class}, version = 1)
public abstract class CategoryRoomDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    private static volatile CategoryRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CategoryRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CategoryRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CategoryRoomDatabase.class, "category_database")
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {

        // Populate the database the first time it is created
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // Populate the database in the background
                    CategoryDao dao = INSTANCE.categoryDao();
                    dao.deleteAll();

                    dao.insert(new Category(0, "Eating out", 0));
                    dao.insert(new Category(1, "Groceries", 1));
                    dao.insert(new Category(2, "Entertainment", 2));
                    dao.insert(new Category(3, "Fuel", 3));
                    dao.insert(new Category(4, "Gifts", 4));
                    dao.insert(new Category(5, "Trips", 5));
                    dao.insert(new Category(6, "Car", 6));
                    dao.insert(new Category(7, "Clothes", 7));
                    dao.insert(new Category(8, "Haircut", 8));
                    dao.insert(new Category(9, "Home Improvement", 9));
                }
            });
        }
    };
}
