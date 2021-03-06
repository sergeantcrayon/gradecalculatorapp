package com.airthcompany.gradecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class MainActivity extends ActionBarActivity {

    String databaseTable;
    DataBaseAdapter myDb;
    final int NO_INPUT = -1;

    HashMap<String, Button> hypoButtons;

    float averageGrade, targetGrade , currentTotalWeight, currentSumAndProduct, targetTotalWeight;
    float targetNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        hypoButtons = new HashMap<>(); // initializing hypothetical grade buttons

        averageGrade = targetGrade  = currentTotalWeight = currentSumAndProduct = targetNeeded = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        databaseTable = i.getStringExtra("table");

        openDB();
        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout); // retrieving main layout
        Button addButton = (Button) findViewById(R.id.button_add); // retrieving add button

        Cursor cursorAll = myDb.getAllRows(databaseTable); // Displaying all items
        DisplayAllStuff(mainLayout, cursorAll);

        setTitle(databaseTable);
        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(final View v) { // add button on click listener

                // Creating alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Add Item");
                //alert.setMessage("Input name, score, max score, and weight of the item");

                // Input fields
                final EditText nameInput = new EditText(v.getContext());
                nameInput.setHint("Name");

                final EditText scoreInput = new EditText(v.getContext());
                scoreInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                scoreInput.setHint("Score");

                final EditText maxInput = new EditText(v.getContext());
                maxInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                maxInput.setHint("Max Score");

                final EditText weightInput = new EditText(v.getContext());
                weightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                weightInput.setHint("Weight");

                // Layout for input fields
                LinearLayout alertLayout = new LinearLayout(v.getContext());
                alertLayout.setOrientation(LinearLayout.VERTICAL);

                // Placing input fields to layout
                alertLayout.addView(nameInput);
                alertLayout.addView(scoreInput);
                alertLayout.addView(maxInput);
                alertLayout.addView(weightInput);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // retrieving data from EditText fields
                        Editable nameValue = nameInput.getText();
                        Editable scoreValue = scoreInput.getText();
                        Editable maxValue = maxInput.getText();
                        Editable weightValue = weightInput.getText();

                        Long newId;
                        //Inserting to Database
                        if(scoreValue.toString().length() == 0 || maxValue.toString().length() == 0){ // if there's no grade placed
                             newId = myDb.insertItem(nameValue.toString().trim(), NO_INPUT, NO_INPUT, Integer.parseInt(weightValue.toString()), databaseTable);
                        } else {
                             newId = myDb.insertItem(nameValue.toString().trim(), Integer.parseInt(scoreValue.toString()), Integer.parseInt(maxValue.toString()), Integer.parseInt(weightValue.toString()), databaseTable);
                        }

                        Cursor cursor = myDb.getRow(newId, databaseTable);

                        //Retrieving same item from Database
                        if(cursor.moveToFirst()){
                            DisplayAllStuff(mainLayout, cursor);
                        }
                    }
                });// end positive button

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });// end negative button

                // Placing layout to alert  dialog
                alert.setView(alertLayout);
                alert.show();

            }
        });
    }

    // creates and displays item buttons from database
    private void DisplayAllStuff(final LinearLayout layout, Cursor cursor){

        if(cursor.moveToFirst()){
            do{
                final int id = cursor.getInt(DataBaseAdapter.COL_ROWID);
                final String name = cursor.getString(DataBaseAdapter.COL_NAME);
                final int score = cursor.getInt(DataBaseAdapter.COL_SCORE);
                final int max = cursor.getInt(DataBaseAdapter.COL_MAX);
                final int weight = cursor.getInt(DataBaseAdapter.COL_WEIGHT);

                float targetTotal, have;
                final float grade;
                have = targetTotal = 0;
                if(score != NO_INPUT && max != NO_INPUT) {
                    grade = (score / (float) max) * weight;
                    currentTotalWeight += weight;
                    targetTotalWeight += weight;
                    currentSumAndProduct += grade;
                    averageGrade = 100 * currentSumAndProduct / currentTotalWeight;

                } else {

                    grade = 0; // FILLER

                    targetTotalWeight += weight;
                    targetTotal = targetTotalWeight * targetGrade/100;
                    have = averageGrade * (currentTotalWeight)/100;
                    targetNeeded = 100*(targetTotal - have)/(targetTotalWeight - currentTotalWeight);

                }

                // Displaying data through TextView in a Layout

                // Creating new layout row
                final LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(param);

                // Name Button
                final Button nameButton = new Button(this);
                LayoutParams nameButtonParam = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                nameButton.setBackgroundResource(R.drawable.button_bg);
                nameButtonParam.weight = 4;
                nameButton.setLayoutParams(nameButtonParam);
                nameButton.setText(name);

                //onClick for nameButton
                nameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder nameButtonAlert = new AlertDialog.Builder(v.getContext());

                        nameButtonAlert.setMessage("Update or Delete item");

                        final EditText nameInput = new EditText(v.getContext());
                        nameInput.setHint("name");

                        nameButtonAlert.setPositiveButton("Update", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Editable updatedName = nameInput.getText();

                                myDb.updateRow(id, updatedName.toString().trim(), score, max, weight, databaseTable);
                                nameButton.setText(updatedName);
                            }
                        });

                        // DELETE ITEM ONCLICK
                        nameButtonAlert.setNeutralButton("Delete", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                myDb.deleteRow(id, databaseTable);
                                layout.removeView(row);


                                if(score != NO_INPUT && max != NO_INPUT) { // real Button case
                                    currentSumAndProduct -= grade;
                                    currentTotalWeight -= weight;
                                } else { // hypothetical button case
                                    targetTotalWeight -= weight;
                                }

                                updateHypoButtons();
                                displayAverage();
                            }
                        });

                        nameButtonAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // DO NOTHING
                            }
                        });

                        nameButtonAlert.setView(nameInput);
                        nameButtonAlert.show();

                        // update methods
                        updateHypoButtons();
                        displayAverage();
                    }
                });

                row.addView(nameButton);

                // divider
                ImageView divider = new ImageView(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT));
                divider.setBackgroundColor(Color.BLACK);
                row.addView(divider);

                // Score and Max Button
                final Button scoreButton = new Button(this);
                LayoutParams scoreButtonParam = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                scoreButton.setBackgroundResource(R.drawable.button_bg);
                scoreButtonParam.weight = 3;
                scoreButton.setLayoutParams(scoreButtonParam);

                // onClickListener for Score and Max Button
                scoreButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        // Creating alert dialog box
                        AlertDialog.Builder scoreButtonAlert = new AlertDialog.Builder(v.getContext());
                        scoreButtonAlert.setMessage("Update Score and Max Score");

                        // Creating a layout to fill with text input fields
                        LinearLayout updateScoreLayout = new LinearLayout(v.getContext());
                        updateScoreLayout.setOrientation(LinearLayout.VERTICAL);

                        // Creating text input fields
                        final EditText scoreInput = new EditText(v.getContext());
                        final EditText maxInput = new EditText(v.getContext());
                        scoreInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        maxInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        scoreInput.setHint("Score");
                        maxInput.setHint("Max Score");

                        // adding text input fields to layout
                        updateScoreLayout.addView(scoreInput);
                        updateScoreLayout.addView(maxInput);

                        // updates the score and max score
                        scoreButtonAlert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // retrieve data from text fields
                                Editable updatedScore = scoreInput.getText();
                                Editable updatedMax = maxInput.getText();

                                // updating database
                                myDb.updateRow(id, name.toString().trim() , Integer.parseInt(updatedScore.toString()), Integer.parseInt(updatedMax.toString()), weight, databaseTable);

                                // updating text on scoreButton
                                scoreButton.setText( updatedScore.toString()+"/"+updatedMax.toString());
                                scoreButton.setTextColor(Color.parseColor("#000000"));


                                hypoButtons.remove(name.toString());


                                // other update methods
                                computeAverage();
                                displayAverage();
                                updateHypoButtons();
                            }
                        });

                        scoreButtonAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // DO NOTHING
                            }
                        });

                        scoreButtonAlert.setView(updateScoreLayout);
                        scoreButtonAlert.show();

                    }
                });

                // setting text on scoreButton
                if(score == -1){ // if there is no score and max
                    scoreButton.setTextColor(Color.parseColor("#cccccc"));
                    scoreButton.setText(String.format("%.2f", targetNeeded)+"%");
                    hypoButtons.put(name, scoreButton);
                } else { // if there is
                    scoreButton.setText(Integer.toString(score)+"/"+Integer.toString(max));
                }

                updateHypoButtons();
                row.addView(scoreButton);

                //another divider
                ImageView divider3 = new ImageView(this);
                divider3.setLayoutParams(new LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT));
                divider3.setBackgroundColor(Color.BLACK);
                row.addView(divider3);

                // Weight Button
                Button weightButton = new Button(this);
                LayoutParams weightButtonParam = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                weightButton.setBackgroundResource(R.drawable.button_bg);
                weightButtonParam.weight = 1;
                weightButton.setLayoutParams(weightButtonParam);
                weightButton.setText(Integer.toString(weight));
                row.addView(weightButton);

                layout.addView(row); // adding row to main layout

            } while(cursor.moveToNext());
        }

        displayAverage();

    }

    /**
     *
     */
    private void updateHypoButtons(){
        float targetTotal, have;

        targetTotal = targetTotalWeight * targetGrade/100;
        have = averageGrade * (currentTotalWeight)/100;
        targetNeeded = 100*(targetTotal - have)/(targetTotalWeight - currentTotalWeight);

        String message = "";
        if(targetGrade == 0){
           message = "n/a";
        } else {
           message = "need "+String.format("%.1f", targetNeeded)+"%";
        }
        for( Map.Entry<String, Button> e : hypoButtons.entrySet()){
            e.getValue().setText(message);
        }

    }

    private void computeAverage(){
        Cursor cursor = myDb.getAllRows(databaseTable);

        targetTotalWeight = targetTotalWeight - currentTotalWeight; // what's left should be the sum of weights from hypo buttons
        currentTotalWeight = currentSumAndProduct = 0;

        if(cursor.moveToFirst()){
            do{

                final int score = cursor.getInt(DataBaseAdapter.COL_SCORE);
                final int max = cursor.getInt(DataBaseAdapter.COL_MAX);
                final int weight = cursor.getInt(DataBaseAdapter.COL_WEIGHT);


                final float grade;

                if(score != NO_INPUT && max != NO_INPUT) {
                    grade = (score / (float) max) * weight;
                    currentTotalWeight += weight;
                    targetTotalWeight += weight;
                    currentSumAndProduct += grade;
                    averageGrade = 100 * currentSumAndProduct / currentTotalWeight;

                }
            } while(cursor.moveToNext());
        }
    }
    /**
     *
     */
    private void displayAverage(){


        averageGrade = 100 * currentSumAndProduct / currentTotalWeight;

        TextView average = (TextView) findViewById(R.id.actual_average);
        String msg = String.format("%.2f", averageGrade) + "%";
        average.setText(msg);
    }

    /**
     *
     *
     * @param view
     */

    public void onClick_targetAverage(View view){
        AlertDialog.Builder targetAlert = new AlertDialog.Builder(view.getContext());

        targetAlert.setMessage("set target grade");

        final EditText targetInput = new EditText(view.getContext());
        targetInput.setHint("Target Grade");
        targetInput.setInputType(InputType.TYPE_CLASS_NUMBER);



        targetAlert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Editable targetData = targetInput.getText();
                targetGrade = new Float(targetData.toString());


                TextView average = (TextView) findViewById(R.id.target_average);
                String msg = String.format("%.2f", targetGrade) + "%";
                average.setText(msg);
                updateHypoButtons();
            }
        });

        targetAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });



        targetAlert.setView(targetInput);
        targetAlert.show();

    }


    private void openDB(){
        myDb = new DataBaseAdapter(this);
        myDb.open();
    }

    private void closeDB(){
        myDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
