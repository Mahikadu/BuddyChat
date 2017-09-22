package com.buddy.sample.chatapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.buddy.sdk.Buddy;
import com.buddy.sdk.BuddyCallback;
import com.buddy.sdk.BuddyResult;
import com.buddy.sdk.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignupNewUser extends AbstractActivity {

    String Gender;
    Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText editName = (EditText) findViewById(R.id.editName);
        final EditText editlName = (EditText) findViewById(R.id.editlname);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText editUsername = (EditText) findViewById(R.id.editUserName);
        final EditText editPassword = (EditText) findViewById(R.id.editPassword);
        final EditText dob = (EditText) findViewById(R.id.dob);
        final RadioGroup gender =(RadioGroup) findViewById(R.id.radioGroup1);

        editName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editName.setFocusableInTouchMode(true);
                return false;
            }
        });
        editlName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editlName.setFocusableInTouchMode(true);
                return false;
            }
        });
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                email.setFocusableInTouchMode(true);
                return false;
            }
        });
        editUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editUsername.setFocusableInTouchMode(true);
                return false;
            }
        });
        editPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editPassword.setFocusableInTouchMode(true);
                return false;
            }
        });


        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int monthOfYear, int dayOfMonth, int year) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(monthOfYear, dayOfMonth, year);
                dob.setText(dateFormatter.format(newDate.getTime()));

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());// it doents get future date
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });



        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                   Gender = checkedRadioButton.getText().toString();
                }
            }
        });


        Button btnSignup = (Button)findViewById(R.id.registerButton);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editName.getText().toString();
                String lastName = editlName.getText().toString();
                String mail = email.getText().toString();
                String dateofbirth = dob.getText().toString();

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                    date = dateFormat.parse(dateofbirth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(isConnectingToInternet()){
                Buddy.createUser(
                        editUsername.getText().toString(),
                        editPassword.getText().toString(),
                        name,
                        lastName,
                        mail,
                        date,
                        Gender,
                        null,
                        new BuddyCallback<User>(User.class) {
                            @Override
                            public void completed(BuddyResult<User> result) {
                                if (result.getIsSuccess()) {
                                    BuddyChatApplication.instance.setCurrentUser(result.getResult());

                                    Intent i = new Intent(getBaseContext(), MainScreen.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    // Username or password false, display and an error
                                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SignupNewUser.this);

                                    dlgAlert.setMessage(String.format("%s", result.getError()));
                                    dlgAlert.setTitle("Error Creating User");
                                    dlgAlert.setPositiveButton("OK", null);
                                    dlgAlert.setCancelable(true);
                                    dlgAlert.create().show();

                                    dlgAlert.setPositiveButton("Ok", null);
                                }
                            }
                        });
            }else{
                    displayMessage(getString(R.string.warning_internet));
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
