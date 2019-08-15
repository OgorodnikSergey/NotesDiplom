package ru.ogorodnik.notesdiplom;

import android.net.Uri;
import android.widget.CursorAdapter;

public interface NoteRepository {

    Uri getContentProviderContentUri();
    CursorAdapter getCursorAdapter();
    String getContentProviderContentItemType();
    void deleteNote(String id);
    void updateNote(String id, String noteText, String noteTitle, String noteDeadline);
    void insertNote(String noteText, String noteTitle, String noteDeadline);
    Note getSelectedNote(Uri uri);

}
