package ru.ogorodnik.notesdiplom;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.Calendar;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editorText;
    private EditText editorTitle;
    private EditText editorDeadline;
    private ImageButton selectDate;
    private DatePickerDialog datePickerDialog;
    private CheckBox checkBox;
    private TextView deadline;
    private String selectedNoteID;
    private String oldText;
    private String oldTitle;
    private String oldDeadline;
    private NoteRepository noteRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noteRepository = App.getNoteRepository();
        setDateSelector();
        setEditors();
    }

    //объявляю редактируемые поля в Активити для ввода заметки
    private void setEditors() {
        initializeEditors();
        checkBox = findViewById(R.id.checkBox);

        setCheckBoxListener();

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(noteRepository.getContentProviderContentItemType());

        if (uri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else{
            action = Intent.ACTION_EDIT;
            selectedNoteID = uri.getLastPathSegment();

            oldText = noteRepository.getSelectedNote(uri).Text;
            oldTitle = noteRepository.getSelectedNote(uri).Title;
            oldDeadline = noteRepository.getSelectedNote(uri).Deadline;

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

    private void initializeEditors() {
        editorTitle = findViewById(R.id.editText);
        editorText = findViewById(R.id.editText2);
        editorDeadline = findViewById(R.id.editTextDeadline);
        editorDeadline.setKeyListener(null);
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
                                    // +1 к месяцу потому что с нуля считает
                                    deadline.setText((month + 1) + getString(R.string.slash)  + day  +
                                            getString(R.string.slash) + year + getString(R.string.hours_minutes_00));
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
                finish();
                break;
        }
        return true;
    }

    private void deleteNote() {
        noteRepository.deleteNote(selectedNoteID);
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

    private void updateNote(String noteText, String noteTitle, String noteDeadline) {
        //обновляем значения в таблице
        //https://github.com/mitchtabian/SQLite-for-Beginners-2019
        noteRepository.updateNote(selectedNoteID, noteText, noteTitle, noteDeadline);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText, String noteTitle, String noteDeadline) {
        noteRepository.insertNote(noteText, noteTitle, noteDeadline);
        Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
