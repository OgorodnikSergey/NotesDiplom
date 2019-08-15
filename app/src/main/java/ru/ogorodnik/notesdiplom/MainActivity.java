package ru.ogorodnik.notesdiplom;

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private static final int SETTINGS_REQUEST_CODE = 1002;
    private static final int START_SCREEN_REQUEST_CODE = 1003;

    private CursorAdapter cursorAdapter;
    private NoteRepository noteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteRepository = App.getNoteRepository();
// Основная активность, все вынес из onCreate
        checkPin();
        openEditorNewNote();
// Подсмотрено здесь https://www.youtube.com/watch?v=69C1ljfDvl0
        loadNotesList();
        getLoaderManager().initLoader(0, null, this);
    }
// Проверяю, был ли задан пароль ранее, если нет, то переходим на Activity задания пароля
    private void checkPin() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.password), Context.MODE_PRIVATE);
        String pin = sp.getString(getString(R.string.PIN), "");
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
        cursorAdapter = noteRepository.getCursorAdapter();

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(noteRepository.getContentProviderContentUri() + getString(R.string.slash) + id);
                intent.putExtra(noteRepository.getContentProviderContentItemType(), uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
// При длительном нажатии, вызываю Сообщение: удалить заметку Да или Нет
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                delAskOption(id).show();
                return true;
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
        noteRepository.deleteNote(String.valueOf(id));
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        restartLoader();
    }

    private AlertDialog delAskOption(final long id)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //Задаю вид сообщения, заголовок и иконку внутри сообщения
                .setTitle(getString(R.string.warning_title))
                .setMessage(getString(R.string.warning_delete_message))
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteNote(id);
                        dialog.dismiss();
                    }
                })

                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
        // Отслеживаю нажатие пункта меню
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
        return new CursorLoader(this, noteRepository.getContentProviderContentUri(),
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
