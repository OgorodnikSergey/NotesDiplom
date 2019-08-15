package ru.ogorodnik.notesdiplom;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.CursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLLiteNoteRepository implements NoteRepository {

    Context context;

    SQLLiteNoteRepository(Context context){
        this.context = context;
    }

    public CursorAdapter getCursorAdapter(){
        return new NotesCursorAdapter(context,
                null, 0);
    }

    public Uri getContentProviderContentUri(){
        return NotesProvider.CONTENT_URI;
    }

    public String getContentProviderContentItemType(){
        return NotesProvider.CONTENT_ITEM_TYPE;
    }


    public void deleteNote(String id) {
        String noteFilter = DBOpenHelper.NOTE_ID + context.getString(R.string.equally) + id;
        context.getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
    }

    public void updateNote(String id, String noteText, String noteTitle, String noteDeadline){
        String noteFilter = DBOpenHelper.NOTE_ID + context.getString(R.string.equally) + id;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_TITLE, noteTitle);
        Date dateNow = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.pattern_europe));
        values.put(DBOpenHelper.NOTE_CREATED, dateFormat.format(dateNow));
        setNoteDeadline(noteDeadline, values);
        context.getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
    }

    public void insertNote(String noteText, String noteTitle, String noteDeadline) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_TITLE, noteTitle);
        setNoteDeadline(noteDeadline, values);
        context.getContentResolver().insert(NotesProvider.CONTENT_URI, values);
    }


    private void setNoteDeadline(String noteDeadline, ContentValues values) {
        if (noteDeadline.length() != 0) {
            values.put(DBOpenHelper.NOTE_DEADLINE, getDateTime(noteDeadline));
            values.put(DBOpenHelper.NOTE_HAS_DEADLINE, context.getString(R.string.caps_yes));
        } else {
            values.put(DBOpenHelper.NOTE_DEADLINE, noteDeadline);
            values.put(DBOpenHelper.NOTE_HAS_DEADLINE, context.getString(R.string.caps_no));
        }
    }

    private String getDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                context.getString(R.string.pattern_US));
        Date date = new Date(dateString);
        return dateFormat.format(date);
    }


    public Note getSelectedNote(Uri uri){
        String noteFilter = DBOpenHelper.NOTE_ID + context.getString(R.string.equally) + uri.getLastPathSegment();
        Cursor cursor = context.getContentResolver().query(uri,
                DBOpenHelper.ALL_COLUMNS,
                noteFilter,
                null,
                null);
        cursor.moveToFirst();
        Note note = new Note(cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TITLE)),
                cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT)),
                cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_DEADLINE)));
        return note;
    }
}
