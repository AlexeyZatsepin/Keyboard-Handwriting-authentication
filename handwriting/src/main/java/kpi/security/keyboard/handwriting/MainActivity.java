package kpi.security.keyboard.handwriting;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.*;
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
import kpi.security.keyboard.handwriting.data.Bounds;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kpi.security.keyboard.handwriting.data.Utils.*;

public class MainActivity extends AppCompatActivity{
    private String LOG_TAG=MainActivity.class.getCanonicalName();
    private static final String USERLIST_TAG = "USERLIST";
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
        getAccountList();

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
//                    Log.v("TEXT",String.valueOf(s.charAt(start+before)));
//                    Log.v("TEXT", String.valueOf(start+before));
                    current_letter=String.valueOf(s.charAt(start+before));
                }
                String editTextValue = editText.getText().toString().trim();
                if(textView.getText().toString().equals(editTextValue)){
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                String standardText = textView.getText().toString();
                if(standardText.contains(editTextValue)){
                    standardText=standardText.replace(editTextValue,"<font color='#43A047'>"+editTextValue+"</font>");
                    textView.setText(Html.fromHtml(standardText));
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
            }
        });
    }

    public void onClick(View view){
        if (textView.getText().toString().equals(editText.getText().toString().trim())){
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

                Toast.makeText(getApplicationContext(),"Successfully joined",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,PreActivity.class));
            }else if(type.equals(getString(R.string.recognition_mode))){
                for (Account user:userList) {
                    if (user.getUsername().equals(username)){

                        if(fisherCheck(user.getS(), dispersion)
                                && limitValueChecker(del_counter,user.getDelCounter(), Bounds.BACKSPACES_LIMIT)
                                && limitValueChecker(fullTime,user.getFullTime(), Bounds.TIME_LIMIT)
                                && studentCheck(user.getY(),clearPressingLength)){
                            Account verified=new Account(username,(HashMap<String, Long>) clearPressingLength,
                                    (ArrayList<Double>)dispersion,fullTime,del_counter);
                            userList.remove(user);
                            userList.add(verified);
                            setAccountList();
                            Intent intent=new Intent(this,CongratulationActivity.class).putExtra(Intent.EXTRA_TEXT,user).putExtra("DATA",verified);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(),"Please try again, bad handwriting",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this,PreActivity.class));
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"User doesn't exists",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }else {
            Toast.makeText(getApplicationContext(),"Text isn't correct",Toast.LENGTH_LONG).show();
            editText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_help){
            showHelp();
        }else if(id== R.id.show_userlist){
            StringBuilder sb=new StringBuilder();
            for (Account user:userList) {
                sb.append(user.getUsername()).append(";");
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
        editor.putString(USERLIST_TAG, json);
        editor.apply();//.commit();
    }

    private void getAccountList(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString(USERLIST_TAG , null);
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        userList = gson.fromJson(json, type);
    }

    private void showHelp(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alert_text));
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setAccountList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setAccountList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setAccountList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAccountList();
    }
}