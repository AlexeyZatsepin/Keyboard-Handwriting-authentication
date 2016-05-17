package kpi.security.keyboard.handwriting;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import kpi.security.keyboard.handwriting.data.Account;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kpi.security.keyboard.handwriting.data.Utils.*;

public class MainActivity extends AppCompatActivity{
    private String LOG_TAG=MainActivity.class.getCanonicalName();
    /**
     * full list of users, analog db, stored in shared preferences
     */
    private ArrayList<Account> userList= new ArrayList<Account>();
    /**
     * helpful variables to get information
     */
    private long timer;
    private String text;
    private String current_letter;
    private int counter=0;
    /**
     * Account data variables
     */
    private HashMap<String,Long> pressingLength=new HashMap<String,Long>();
    private long fullTime;
    private int del_counter=0;
    /**
     * widgets
     */
    private EditText editText;
    private EditText usernameEditText;
    private TextView textView;
    //type of mode
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submit=(Button) findViewById(R.id.button);
        submit.setTextColor(Color.parseColor("#FFFAFA"));

        type = getIntent().getStringExtra("TYPE");
        editText = (EditText) findViewById(R.id.editText);

        usernameEditText = (EditText) findViewById(R.id.username);
        usernameEditText.setGravity(Gravity.CENTER);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (Account user:userList) {
                    if ((user.getUsername().equals(usernameEditText.getText().toString().trim()))&&
                            type.equals("study")){
                        Toast.makeText(getApplicationContext(),"This username already in use",Toast.LENGTH_LONG).show();
                        usernameEditText.clearAnimation();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textView = (TextView) findViewById(R.id.textView);
        textView.setTextColor(Color.parseColor("#757575"));

        TextView labelUser=(TextView) findViewById(R.id.label_user);
        labelUser.setTextColor(Color.parseColor("#757575"));

        TextView title = (TextView) findViewById(R.id.title);
        if (type.equals("study")){
            title.setText(R.string.reg_label);
        }else {
            title.setText(R.string.auth_label);
        }
        title.setTextColor(Color.parseColor("#607D8B"));

        fullTime=-System.currentTimeMillis();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                timer=-System.currentTimeMillis();
                text = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<=text.length()) {
                    del_counter++;
                    current_letter="backspace";
                }else{
                    /*
                    Log.v("TEXT","------------");
                    Log.v("TEXT",s.toString());
                    Log.v("TEXT",String.valueOf(s.charAt(start+before)));
                    Log.v("TEXT", String.valueOf(start+before));
                    Log.v("TEXT","------------");
                    */
                    current_letter=String.valueOf(s.charAt(start+before));
                }
                if(textView.getText().equals(editText.getText().toString().trim())){
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textView.getText().toString().contains(current_letter)){
                    timer+=System.currentTimeMillis();
                    if (pressingLength.get(current_letter)==null){
                        pressingLength.put(current_letter,timer);
                    }else {
                        pressingLength.put(current_letter+counter++,timer);
                    }
                }
                else{
                    //TODO:make last letter red
                }
            }
        });

    }

    public void onClick(View view){
        if (textView.getText().equals(editText.getText().toString().trim())){
            fullTime+=System.currentTimeMillis();

            String username = usernameEditText.getText().toString();
            List<Double> mathExpectation = mathExpectation(pressingLength); // base value
            List<Double> dispersion = dispersion(pressingLength, mathExpectation); //base value

            Map<String,Long> clearPressingLength = discardingOutliers(pressingLength,dispersion,mathExpectation); // delete "wrong values"
            mathExpectation = mathExpectation(clearPressingLength); // clear value
            dispersion = dispersion(clearPressingLength,mathExpectation); // clear value
            if (type.equals(getString(R.string.study_mode))){

                for (Account user:userList) {
                    if (user.getUsername().equals(username)){
                        Toast.makeText(getApplicationContext(),"This username already in use",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Account account = new Account(username, (HashMap<String, Long>) clearPressingLength,
                            (ArrayList<Double>) dispersion, fullTime, del_counter);
                userList.add(account);
                setAccountList();

                Toast.makeText(getApplicationContext(),"Successfuly registrated",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,PreActivity.class));
            }else if(type.equals(getString(R.string.recognition_mode))){
                getAccountList();
                for (Account user:userList) {
                    if (user.getUsername().equals(username)){

                        Log.v(LOG_TAG,clearPressingLength.toString());

                        if(fisherCheck(user.getS(), dispersion)&&delCounterChecker(del_counter,user.getDelCounter())
                                &&fullTimeCheker(fullTime,user.getFullTime())){

                            Log.v(LOG_TAG,"Fisher success");

//                            Intent intent=new Intent(this,CongratulationActivity.class).putExtra(Intent.EXTRA_TEXT,fullTime)
//                                    .putExtra("del",del_counter).putExtra("MAP",(HashMap<String,Long>)clearPressingLength);
                            Intent intent=new Intent(this,CongratulationActivity.class).putExtra(Intent.EXTRA_TEXT,user);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(),"Try again, bad handwriting",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this,PreActivity.class));
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"User doesn't exists",Toast.LENGTH_LONG).show();
                    }
                }
                //Toast.makeText(getApplicationContext(),userList.toString(),Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Text isn't correct",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }else if (id == R.id.action_help){
            showHelp();
        }else if(id== R.id.show_userlist){
            getAccountList();
            StringBuilder sb=new StringBuilder();
            for (Account user:userList) {
                sb.append(user.getUsername());
            }
            Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setAccountList(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(userList);

        editor.putString("USERLIST", json);
        editor.apply();// .commit();
    }

    private void getAccountList(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("USERLIST", null);
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        userList = gson.fromJson(json, type);
    }

    private void showHelp(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alert_text));
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
