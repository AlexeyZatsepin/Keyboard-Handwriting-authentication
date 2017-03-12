package com.security.keyboard.handwriting;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.securityconfirm.layout.model.Account;
import com.securityconfirm.layout.model.SecurityContext;
import com.securityconfirm.layout.ui.SecureConfirmationLayout;


public class MainActivity extends AppCompatActivity{

    private String TAG=MainActivity.class.getSimpleName();
    private SecureConfirmationLayout confirmLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SecurityContext.init(this);

        Button submit=(Button) findViewById(R.id.button);
        submit.setTextColor(Color.parseColor("#FFFAFA"));

        confirmLayout = (SecureConfirmationLayout) findViewById(R.id.confirm_layout);
        String type = getIntent().getStringExtra("TYPE");
        confirmLayout.setType(type);

        TextView title = (TextView) findViewById(R.id.title);
        if (type.equals("study")){
            title.setText(R.string.reg_label);
        }else {
            title.setText(R.string.auth_label);
        }
        title.setTextColor(Color.parseColor("#607D8B"));

    }

    public void onClick(View view){
        Account account = confirmLayout.verify();
        if (account == null){
            startActivity(new Intent(this,PreActivity.class));
        }else {
            Intent intent=new Intent(this,CongratulationActivity.class).putExtra(Intent.EXTRA_TEXT,account).putExtra("DATA",account);
            startActivity(intent);
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
            Toast.makeText(this, "Settings opened action", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.action_help){
            showHelp();
        }else if(id== R.id.show_userlist){
            StringBuilder sb=new StringBuilder();
            for (Account user:SecurityContext.getUserList()) {
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


    private void showHelp(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alert_text));
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SecurityContext.save(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SecurityContext.save(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SecurityContext.save(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SecurityContext.init(this);
        confirmLayout.rewind();
    }

    public static class MainActivityFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }
}