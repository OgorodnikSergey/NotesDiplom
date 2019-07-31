package ru.ogorodnik.notesdiplom;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private static final int SETTINGS_REQUEST_CODE = 1002;
    private static final int START_SCREEN_REQUEST_CODE = 1003;
    private static final String PIN = "PIN";

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
// Основная активность, все вынес из onCreate
        checkPin();

        openEditorNewNote();
// Подсмотрено здесь https://www.youtube.com/watch?v=69C1ljfDvl0
        loadNotesList();

        getLoaderManager().initLoader(0, null, this);
    }
// Проверяю, бил ли задан пароль ранее, если нет, то переходим на Activity задания пароля
    private void checkPin() {
        SharedPreferences sp = getSharedPreferences("password", Context.MODE_PRIVATE);
        String pin = sp.getString(PIN, "");
        if (pin.length() > 0){
            Intent intent = new Intent(getApplication(), EnterPinActivity.class);
            startActivityForResult(intent, START_SCREEN_REQUEST_CODE);
        } else {
            Intent intent = new Intent(getApplication(), SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        }
    }
// Вывод списка заметок
    private void loadNotesList() {
        cursorAdapter = new NotesCursorAdapter(this,
                null, 0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
// При длительном нажатии, вызываю Сообщение: удалить заметку Да или Нет
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                delAskOption(id).show();
                return false;
            }
        });
    }
// Нажатие по кнопке для добавления новой заметки
    private void openEditorNewNote() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), EditorActivity.class);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

    private void deleteNote(long id) {
        String noteFilter = DBOpenHelper.NOTE_ID + "=" + id;
        getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        restartLoader();
    }

    private AlertDialog delAskOption(final long id)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //Задаю вид сообщения, заголовок и иконку внутри сообщения
                .setTitle("Warning")
                .setMessage("Are you sure you want to delete this note?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteNote(id);
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Наполняю меню, добавляю действие
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Отслеживаю нажатие пункта меню.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplication(), SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI,
                null, null, null, null);
        //Выборка равна null (Selection, selectionArgs), так как нам необходимы все данные
        //перечень полей таблицы здесь на задаем, так как он задан в Provider
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //onLoadFinished получает данные из объекта cursor.
        //Задача забрать данные из объекта Курсора и передеать их в adaptor
        //Делаем это с помощью cursorAdaptor.swapCursor
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //Вызываем когда данные необходимо очистить
        cursorAdapter.swapCursor(null);
    }
}
