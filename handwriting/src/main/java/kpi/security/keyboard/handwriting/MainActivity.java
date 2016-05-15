package kpi.security.keyboard.handwriting;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import kpi.security.keyboard.handwriting.data.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
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
        //Log.v("SAVE_AFTER", String.valueOf(savedInstanceState.getInt("test")));
        //Toast.makeText(getApplicationContext(),userList.toString(),Toast.LENGTH_LONG).show();

        Button submit=(Button) findViewById(R.id.button);
        submit.setTextColor(Color.parseColor("#FFFAFA"));

        type = getIntent().getStringExtra("TYPE");
        editText = (EditText) findViewById(R.id.editText);

        usernameEditText = (EditText) findViewById(R.id.username);
        usernameEditText.setGravity(Gravity.CENTER);

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
                    current_letter="del";

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
                timer+=System.currentTimeMillis();
                if (pressingLength.get(current_letter)==null){
                    pressingLength.put(current_letter,timer);
                }else {
                    pressingLength.put(current_letter,timer+pressingLength.get(current_letter));
                }
            }
        });

    }

    public void onClick(View view){
        if (textView.getText().equals(editText.getText().toString().trim())){
            if (type.equals("study")){
                fullTime+=System.currentTimeMillis();

                for (Account user:userList) {
                    if (user.getUsername().equals(usernameEditText.getText().toString())){
                        Toast.makeText(getApplicationContext(),"This username already in use",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Account account=new Account(usernameEditText.getText().toString(),pressingLength,
                        Utils.dispertion(pressingLength,Utils.mathExpectation(pressingLength)),fullTime,del_counter);
                userList.add(account);
                setAccountList();

                //Toast.makeText(getApplicationContext(),  String.valueOf(fullTime),Toast.LENGTH_LONG).show();
                Intent intent=new Intent(this,CongratulationsActivity.class).putExtra(Intent.EXTRA_TEXT,fullTime)
                        .putExtra("del",del_counter).putExtra("MAP",pressingLength);
                startActivity(intent);


            }else if(type.equals("recognition")){

                getAccountList();
                for (Account user:userList) {
                    if (user.getUsername().equals(usernameEditText.getText().toString())){

                        //TODO: verifier
                    }
                }

                Toast.makeText(getApplicationContext(),userList.toString(),Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Text isn't correct",Toast.LENGTH_LONG).show();
        }
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
        }else if (id == R.id.action_help){
            showHelp();
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

    public void showHelp(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alert_text));
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
