package com.airthcompany.gradecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import android.widget.TextView;


public class CoursesActivity extends ActionBarActivity {

    public DataBaseAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        setTitle("Classes");
        openDB();

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.add_course);
        Button addCourseButton = (Button) findViewById(R.id.button_add);

        Cursor cursor = myDb.getAllTables();
        DisplayTables(mainLayout, cursor, "");

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder addCourseAlert = new AlertDialog.Builder(v.getContext());
                addCourseAlert.setTitle("Add Class");

                final EditText courseInput = new EditText(v.getContext());
                courseInput.setHint("Class Name");

                addCourseAlert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Editable courseName = courseInput.getText();

                        myDb.createTable(courseName.toString().trim()); // creating table
                        Cursor c = myDb.getAllTables();

                        DisplayTables(mainLayout, c, courseName.toString().trim());

                    }
                });

                addCourseAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                addCourseAlert.setView(courseInput);
                addCourseAlert.show();

            }
        }); // end onclick listener for add course button

    }


    public void DisplayTables(final LinearLayout layout, Cursor cursor, final String table){
        if(cursor.moveToFirst()){
            do{

                final String tableName = cursor.getString(cursor.getColumnIndex("name"));

                final LinearLayout innerLayout = new LinearLayout(this);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);

                final Button nameButton = new Button(innerLayout.getContext());
                LinearLayout.LayoutParams nameButtonParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                nameButton.setBackgroundResource(R.drawable.button_bg);
                nameButton.setLayoutParams(nameButtonParam);
                nameButton.setText(tableName);
                nameButton.setGravity(Gravity.CENTER_VERTICAL);

                nameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                        alert.setTitle("What's next?");
                        alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                                nextScreen.putExtra("table", tableName);

                                startActivity(nextScreen);
                            }
                        });

                        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDb.deleteTable(tableName);
                                layout.removeView(nameButton);

                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // DO NOTHING
                            }
                        });

                        alert.show();



                    }
                });

                if( (table.length() == 0 || tableName.equals(table)) && !tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")) {
                    layout.addView(nameButton);
                }

            } while(cursor.moveToNext());
        }
    }


    private void openDB(){
        myDb = new DataBaseAdapter(this);
        myDb.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

