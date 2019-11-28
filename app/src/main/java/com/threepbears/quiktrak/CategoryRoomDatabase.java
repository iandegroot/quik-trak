package com.threepbears.quiktrak;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;
import java.util.concurrent.Executors;

@Database(entities = {Category.class}, version = 1)
public abstract class CategoryRoomDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    private static volatile CategoryRoomDatabase INSTANCE;

    static CategoryRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CategoryRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CategoryRoomDatabase.class, "category_database")
                            .allowMainThreadQueries()
                            .addCallback(new RoomDatabase.Callback() {
                                public void onCreate (SupportSQLiteDatabase db) {

                                    Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            getDatabase(context).categoryDao().insert(new Category(0, "Eating out"));
                                            getDatabase(context).categoryDao().insert(new Category(1, "Groceries"));
                                            getDatabase(context).categoryDao().insert(new Category(2, "Entertainment"));
                                            getDatabase(context).categoryDao().insert(new Category(3, "Fuel"));
                                            getDatabase(context).categoryDao().insert(new Category(4, "Gifts"));
                                            getDatabase(context).categoryDao().insert(new Category(5, "Trips"));
                                            getDatabase(context).categoryDao().insert(new Category(6, "Car"));
                                            getDatabase(context).categoryDao().insert(new Category(7, "Clothes"));
                                            getDatabase(context).categoryDao().insert(new Category(8, "Haircut"));
                                            getDatabase(context).categoryDao().insert(new Category(9, "Home Improvement"));
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
