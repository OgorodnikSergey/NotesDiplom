package ru.ogorodnik.notesdiplom;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    //Константы, задаем имя базы данных и версию
    // Книга "Разработка мобильных приложений" Федотенко М.А., стр 245 - работа с базами данных
    // https://www.andrious.com/tutorials/create-a-note-app-using-sqlite-database/
    // https://github.com/mitchtabian/SQLite-for-Beginners-2019
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 9;

    //Константы, определяющие перечень полей в базе данных
    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static  final String NOTE_TEXT = "noteText";
    public static final String NOTE_TITLE = "noteTitle";
    public static final String NOTE_DEADLINE = "noteDeadline";
    public static final String NOTE_HAS_DEADLINE = "noteHaveDeadline";
    public static final String NOTE_CREATED = "noteCreated";

    public static final String[] ALL_COLUMNS = {NOTE_ID, NOTE_TEXT, NOTE_TITLE, NOTE_DEADLINE, NOTE_HAS_DEADLINE, NOTE_CREATED};

    //SQLite - создание базы (таблицы)
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TITLE + " TEXT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_DEADLINE + " TEXT, " +
                    NOTE_HAS_DEADLINE + " TEXT, " +
                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ") ";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
}