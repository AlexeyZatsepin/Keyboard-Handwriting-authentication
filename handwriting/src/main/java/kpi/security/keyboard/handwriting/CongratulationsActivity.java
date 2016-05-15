package kpi.security.keyboard.handwriting;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * KeyboardHandwriting
 * Created 5/2/16, with IntelliJ IDEA
 *
 * @author Alex
 */
public class CongratulationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congratulations_layout);
        TextView label= (TextView) findViewById(R.id.congratulations_label);
        label.setTextColor(Color.parseColor("#607D8B"));
        Button back=(Button) findViewById(R.id.back);
        back.setTextColor(Color.parseColor("#FFFAFA"));

        Intent intent = getIntent();
        TextView result1= (TextView) findViewById(R.id.result1);
        TextView result2= (TextView) findViewById(R.id.result2);
        TextView result3= (TextView) findViewById(R.id.result3);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            long fullTime = intent.getLongExtra(Intent.EXTRA_TEXT,0);
            int del_counter=intent.getIntExtra("del",0);
            HashMap<String,Long> pressingLenght = (HashMap<String, Long>)intent.getSerializableExtra("MAP");
            StringBuilder sb=new StringBuilder();
            for (String key:pressingLenght.keySet()) {
                sb.append(key).append(":").append(pressingLenght.get(key)).append("\n");
            }
            //String count = intent.getStringExtra("Backspaces");
            result1.setText("Time:" + fullTime);
            result2.setText("Backspaces:"+ del_counter);
            result3.setText("Frequency:"+ sb.toString());
        }


    }

    public void backToPreLayout(View view){
        startActivity(new Intent(this,PreActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



}
