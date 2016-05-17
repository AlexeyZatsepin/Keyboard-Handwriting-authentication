package kpi.security.keyboard.handwriting;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * KeyboardHandwriting
 * Created 5/2/16, with IntelliJ IDEA
 *
 * @author Alex
 */
public class CongratulationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_activity_layout);
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
            Map<String,Long> pressingLength = (HashMap<String, Long>)intent.getSerializableExtra("MAP");
            StringBuilder sb=new StringBuilder();
            for (String key:pressingLength.keySet()) {
                sb.append(key).append(":").append(pressingLength.get(key)).append("\n");
            }
            result1.setText(String.format("Time:%d", fullTime));
            result2.setText(String.format("Backspaces:%d", del_counter));
            result3.setText(String.format("Frequency:%s", sb.toString()));
        }
    }

    public void backToPreLayout(View view){
        startActivity(new Intent(this,PreActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
