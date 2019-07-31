package ru.ogorodnik.notesdiplom;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editorText;
    private EditText editorTitle;
    private EditText editorDeadline;
    private ImageButton selectDate;
    private DatePickerDialog datePickerDialog;
    private CheckBox checkBox;
    private TextView deadline;
    private String noteFilter;
    private String oldText;
    private String oldTitle;
    private String oldDeadline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setDateSelector();
        setEditors();
    }

    //объявляю редактируемы поля в Активити для ввода заметки
    private void setEditors() {
        editorTitle = findViewById(R.id.editText);
        editorText = findViewById(R.id.editText2);
        editorDeadline = findViewById(R.id.editTextDeadline);
        editorDeadline.setKeyListener(null);
        checkBox = findViewById(R.id.checkBox);

        setCheckBoxListener();

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else{
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS,
                    noteFilter,
                    null,
                    null);

            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TITLE));
            oldDeadline = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_DEADLINE));

            if (!String.valueOf(oldDeadline).equals(""))
            {
                checkBox.setChecked(true);
            }

            if(checkBox.isChecked()) {
                editorDeadline.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
                editorDeadline.setInputType(InputType.TYPE_CLASS_DATETIME);
            }

            editorText.setText(oldText);
            editorTitle.setText(oldTitle);
            editorDeadline.setText(oldDeadline);
            editorText.requestFocus();
        }
    }

    private void setCheckBoxListener() {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    editorDeadline.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
                    editorDeadline.setInputType(InputType.TYPE_CLASS_DATETIME);
                } else {
                    editorDeadline.setText("");
                    editorDeadline.setKeyListener(null);
                }
            }
        });
    }

    private void setDateSelector() {
        selectDate = findViewById(R.id.btnDate);
        deadline = findViewById(R.id.editTextDeadline);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked())
                {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditorActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
    // +1 потому что с нуля считает
                                deadline.setText((month + 1) + "/"  + day  + "/" + year + " 00:00");
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                finishEditing();
                break;
        }
        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private boolean isNoteNotNull(String text, String title, String deadline){
        if (text.length() == 0
                && title.length() == 0
                && deadline.length() == 0)
        {
            return true;
        } else return false;
    }

    private void finishEditing(){
        String newText = editorText.getText().toString().trim();
        String newTitle = editorTitle.getText().toString().trim();
        String newDeadline = editorDeadline.getText().toString().trim();

        switch (action){
            case Intent.ACTION_INSERT:
                if (isNoteNotNull(newText, newTitle, newDeadline)){
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText, newTitle, newDeadline);
                }
                break;
            case Intent.ACTION_EDIT:
                if (isNoteNotNull(newText, newTitle, newDeadline)){
                    deleteNote();
                } else if (oldText.equals(newText) && oldTitle.equals(newTitle) && oldDeadline.equals(newDeadline)){
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText, newTitle, newDeadline);
                }
        }
    }

    private String getDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm");
        Date date = new Date(dateString);
        return dateFormat.format(date);
    }

    private void setNoteDeadline(String noteDeadline, ContentValues values) {
        if (noteDeadline.length() != 0) {
            values.put(DBOpenHelper.NOTE_DEADLINE, getDateTime(noteDeadline));
            values.put(DBOpenHelper.NOTE_HAS_DEADLINE, "YES");
        } else {
            values.put(DBOpenHelper.NOTE_DEADLINE, noteDeadline);
            values.put(DBOpenHelper.NOTE_HAS_DEADLINE, "NO");
        }
    }

    private void updateNote(String noteText, String noteTitle, String noteDeadline) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_TITLE, noteTitle);
        Date dateNow = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(DBOpenHelper.NOTE_CREATED, dateFormat.format(dateNow));
        setNoteDeadline(noteDeadline, values);
        //обновляем значения в таблице
        //https://github.com/mitchtabian/SQLite-for-Beginners-2019

        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        setTextFieldsToZero ();
    }

    private void insertNote(String noteText, String noteTitle, String noteDeadline) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_TITLE, noteTitle);
        setNoteDeadline(noteDeadline, values);
        //вставляем значения в таблицу
        // https://github.com/mitchtabian/SQLite-for-Beginners-2019
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        setTextFieldsToZero ();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    private void setTextFieldsToZero (){
        editorText.setText("");
        editorTitle.setText("");
        editorDeadline.setText("");
    }
}
