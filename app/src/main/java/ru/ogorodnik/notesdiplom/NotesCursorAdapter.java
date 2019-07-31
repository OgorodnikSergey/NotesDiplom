package ru.ogorodnik.notesdiplom;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter {

    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Читаем наш лэйаут note_list_item, возвращаем его обратно, когда вызывается метод newView
        return LayoutInflater.from(context).inflate(
                R.layout.note_list_item,
                parent,
                false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Передаем полученные данные от объекта Cursor в строки, которые будут отображаться на экране во вьюшке (Заголовок, текст и Дата)

        String noteText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT)
        );

        String titleText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_TITLE)
        );

        String deadlineText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_DEADLINE)
        );

        TextView tvText = view.findViewById(R.id.tvNoteText);
        tvText.setText(noteText);

        TextView tvTitle = view.findViewById(R.id.tvNoteTitle);
        tvTitle.setText(titleText);

        TextView tvDeadline = view.findViewById(R.id.tvDeadline);
        tvDeadline.setText(deadlineText);
    }
}
