package com.app.singleebookapp.databases.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.singleebookapp.models.Chapter;

import java.util.ArrayList;
import java.util.List;

public class DbChapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "chapter.db";
    public static final String TABLE_LABEL = "chapter";
    public static final String ID = "id";
    public static final String PAGE_TITLE = "title";
    public static final String PAGE_INDEX = "pageIdx";
    private final SQLiteDatabase db;

    public DbChapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableChapter(db, TABLE_LABEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABEL);
        createTableChapter(db, TABLE_LABEL);
    }

    public void truncateTableChapter(String table) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
        createTableChapter(db, table);
    }

    private void createTableChapter(SQLiteDatabase db, String table) {
        String CREATE_TABLE = "CREATE TABLE " + table + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PAGE_TITLE + " TEXT,"
                + PAGE_INDEX + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public void addListChapter(List<Chapter> chapters, String table) {
        for (Chapter c : chapters) {
            addOneChapter(db, c, table);
        }
        getChapters(table);
    }

    public void addOneChapter(SQLiteDatabase db, Chapter chapters, String table) {
        ContentValues values = new ContentValues();
        values.put(PAGE_TITLE, chapters.page_title);
        values.put(PAGE_INDEX, chapters.page_number);
        db.insert(table, null, values);
    }

    public List<Chapter> getChapters(String table) {
        return getAllChapters(table);
    }

    private List<Chapter> getAllChapters(String table) {
        List<Chapter> list;
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM " + table + " ORDER BY " + "id ASC", null);
        list = getAllChapterFormCursor(cursor);
        return list;
    }

    @SuppressLint("Range")
    private List<Chapter> getAllChapterFormCursor(Cursor cursor) {
        List<Chapter> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Chapter chapters = new Chapter();
                chapters.page_title = cursor.getString(cursor.getColumnIndex(PAGE_TITLE));
                chapters.page_number = cursor.getInt(cursor.getColumnIndex(PAGE_INDEX));
                list.add(chapters);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
