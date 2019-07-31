package ru.ogorodnik.notesdiplom;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider {

    //Строка для идентификации content provider
    private static final String AUTHORITY = "ru.ogorodnik.notesdiplom.notesprovider";

    //набор данных
    private static final String BASE_PATH = "notesdiplom";

    //CONTENT_URI уникальный идентификатор ресурса для content provider
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    //Константы для определения выполняемой операции
    private static  final int NOTES = 1;
    private static final int NOTES_ID = 2;

    //UriMatcher class для анализа URI и поределения, какая операция была запущена
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    //эта часть выполняется при первом вызове
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        // получаю URI, который начинается с base_path и заканчивается на слэш / с числом.
        // которое означает определенную строку в таблице с данными
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] strings1, @Nullable String s1) {
        // Метод query выбирает данные из таблицы заметок
        // Может возвращать все заметки или только одну строку из таблицы
        if (uriMatcher.match(uri) == NOTES_ID){
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }


        return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS,
        selection, null, null, null,
          DBOpenHelper.NOTE_HAS_DEADLINE + " DESC" + "," + DBOpenHelper.NOTE_DEADLINE  + " ASC" + "," + DBOpenHelper.NOTE_CREATED + " DESC") ;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //метод insert возвращает URI. base_path / значение ключа записи
        //Коллекция пары: имя - значение
        long id = database.insert(DBOpenHelper.TABLE_NOTES,
                null, values);

        //parse метод собирает сроку и возвращает URI
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        return database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTES, values, selection, selectionArgs);
    }
}
