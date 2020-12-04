package edu.csce4623.ahnelson.todomvp3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;
import edu.csce4623.ahnelson.todomvp3.todolistactivity.ToDoListContract;

public class AddEditToDoItem extends AppCompatActivity {

    // setting up UI variables
    Button saveChanges;
    Button delete;
    EditText etTitle;
    EditText etContent;
    EditText etDateTime;
    TextView tvDisplayMsg;
    CheckBox completedTV;
    Switch setAlarm;
    Date dateMilli;

    // to do item to be added/altered
    ToDoItem item;

    // request code
    int requestCode;

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);

    // Presenter instance for view
    private ToDoListContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("EditToDoActivity", "Called onCreate");

        // retrieve Intent data
        Intent callingIntent = this.getIntent();
        item = (ToDoItem) callingIntent.getSerializableExtra("ToDoItem");
        requestCode = (int) callingIntent.getSerializableExtra("RequestCode");
        // extract the title & content of the To Do item
        String title = item.getTitle();
        String content = item.getContent();

        String addDisplayMsg = "Create A New To-Do!";
        String updateDisplayMsg = "Update New To-Do!";

        // set proper view
        setContentView(R.layout.activity_add_edit_to_do_item);

        // link Button and EditText views, and display proper info
        saveChanges = findViewById(R.id.btnSaveToDoItem);
        delete = findViewById(R.id.btnDelete);
        etTitle = findViewById(R.id.etItemTitle);
        etTitle.setText(title);
        etContent = findViewById(R.id.etItemContent);
        etContent.setText(content);

        setAlarm = findViewById(R.id.setAlarm);


        if (item.getDueDate() > 0) {
            setAlarm.setChecked(true);
            dateMilli = new Date(item.getDueDate());
        } else {
            setAlarm.setChecked(false);
            dateMilli = new Date(System.currentTimeMillis());
        }
        String dateTime = sdf.format(dateMilli);

        etDateTime = findViewById(R.id.selectDate);
        etDateTime.setText(dateTime);
        etDateTime.setInputType(InputType.TYPE_NULL);


        setAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (setAlarm.isChecked()) {
                    etDateTime.setEnabled(true);
                    etDateTime.setClickable(true);
                } else {
                    etDateTime.setEnabled(false);
                    etDateTime.setClickable(false);
                }
            }
        });

        completedTV = findViewById(R.id.cbItemCompleted);
        completedTV.setChecked(item.getCompleted());
        // update to do item's isCompleted boolean if checkbox is clicked
        completedTV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (completedTV.isChecked()) {
                    item.setCompleted(true);
                } else {
                    item.setCompleted(false);
                }
            }
        });



        final long[] timeInMill = new long[1];
        etDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                Log.d("INDISDE DATETIME", "working!!");
                showDateTimeDialog();
                Log.d("RETURNED DATE", String.valueOf(timeInMill[0]));
            }
        });

        tvDisplayMsg = findViewById(R.id.displayMsg);
        if (requestCode == 0) {
            tvDisplayMsg.setText(addDisplayMsg);
        } else {
            tvDisplayMsg.setText(updateDisplayMsg);
        }

        // set onclick listener to bundle data & send back to model
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EditToDoActivity", "Saved Changes");
                try {
                    saveChanges();
                } catch (Exception e) {
                    Log.d("Error: ", String.valueOf(e));
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EditToDoActivity", "Deleting item...");
                deleteItem();
            }
        });

    }

    void showDateTimeDialog() {
        final Calendar cal = Calendar.getInstance();
        final long[] dateMilli = new long[1];
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.d("DATE: ", "year: " + year + "; month: " + month + "; day:" + day);
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                Log.d("DATE: ", cal.getTime().toString());


                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        dateMilli[0] = cal.getTimeInMillis();
                        String dateTime = sdf.format(new Date(dateMilli[0]));
                        etDateTime.setText(dateTime);
                    }
                };

                new TimePickerDialog(AddEditToDoItem.this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();

            }
        };
        new DatePickerDialog(AddEditToDoItem.this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    void saveChanges() throws ParseException {
            Log.d("EditToDoActivity", "SaveChangesClicked");
            // set the To Do item's title & content to info entered on view
            item.setTitle(etTitle.getText().toString());
            item.setContent(etContent.getText().toString());

            Log.d("checking outside? ", String.valueOf(setAlarm.isChecked()));
            if (!setAlarm.isChecked()) {
                Log.d("isChecked? ", String.valueOf(setAlarm.isChecked()));
                etDateTime.setText("");
                item.setDueDate(-1);
            } else {

                Log.d("should not be?", String.valueOf(setAlarm.isChecked()));
                String theDate = etDateTime.getText().toString();
                Date date = sdf.parse(theDate);

                Log.d("CHECKING DATE", String.valueOf(date.getTime()));
                item.setDueDate(date.getTime());
            }
            // create new Intent to return to model
            Intent returningIntent = new Intent();
            // add in the To Do item just created/altered
            returningIntent.putExtra("ToDoItem",item);
            // set result for the callback function which is invoked upon return to the model
            setResult(Activity.RESULT_OK, returningIntent);
            finish();

    }

    void deleteItem() {
        // create new Intent to return to model
        Intent returningIntent = new Intent();
        returningIntent.putExtra("ToDoItem",item);
        returningIntent.putExtra("Deleting",1);
        // add in the To Do item just created/altered
        // set result for the callback function which is invoked upon return to the model
        setResult(Activity.RESULT_OK, returningIntent);
        finish();
    }

}