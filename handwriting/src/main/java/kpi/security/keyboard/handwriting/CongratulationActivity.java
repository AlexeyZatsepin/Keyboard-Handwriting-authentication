package kpi.security.keyboard.handwriting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import kpi.security.keyboard.handwriting.data.Account;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
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
        setContentView(R.layout.congratulation_layout);
        Button back=(Button) findViewById(R.id.back);
        back.setTextColor(Color.parseColor("#FFFAFA"));

        Intent intent = getIntent();
        TextView label= (TextView) findViewById(R.id.congratulations_label);
        TextView labelLeft = (TextView) findViewById(R.id.label_left);
        TextView labelRight = (TextView) findViewById(R.id.label_right);
        TextView resultTime1 = (TextView) findViewById(R.id.resultTime1);
        TextView resultTime2 = (TextView) findViewById(R.id.resultTime2);
        TextView resultBack1 = (TextView) findViewById(R.id.resultBack1);
        TextView resultBack2 = (TextView) findViewById(R.id.resultBack2);
        TextView resultY1 = (TextView) findViewById(R.id.resultY1);
        TextView resultY2 = (TextView) findViewById(R.id.resultY2);
        TextView resultS1 = (TextView) findViewById(R.id.resultS1);
        TextView resultS2 = (TextView) findViewById(R.id.resultS2);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            Account user = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            Account data = intent.getParcelableExtra("DATA");

            label.setText(String.format("Hello %s! Your results:", user.getUsername()));
            label.setTextColor(Color.parseColor("#607D8B"));
            labelLeft.setTextColor(Color.parseColor("#607D8B"));
            labelRight.setTextColor(Color.parseColor("#607D8B"));
            resultTime1.setText(Html.fromHtml("<font color='#607D8B'>Your time: </font><font color='#6A1B9A'>"+ user.getFullTime()/1000+"</font> sec"));
            resultTime2.setText(Html.fromHtml("<font color='#607D8B'>Your time: </font><font color='#6A1B9A'>"+ data.getFullTime()/1000+"</font> sec"));
            resultBack1.setText(Html.fromHtml("<font color='#607D8B'>Backspaces used: </font><font color='#6A1B9A'>"+ user.getDelCounter()+"</font>"));
            resultBack2.setText(Html.fromHtml("<font color='#607D8B'>Backspaces used: </font><font color='#6A1B9A'>"+ data.getDelCounter()+"</font>"));
            resultY1.setText(Html.fromHtml(colored(user.getY())));
            resultY2.setText(Html.fromHtml(colored(data.getY())));
            resultS1.setText(Html.fromHtml("<font color='#607D8B'>Average dispersion: </font><font color='#6A1B9A'>"+ avg(user.getS())+"</font>"));
            resultS2.setText(Html.fromHtml("<font color='#607D8B'>Average dispersion: </font><font color='#6A1B9A'>"+ avg(data.getS())+"</font>"));
        }

    }

    public void backToPreLayout(View view){
        startActivity(new Intent(this,PreActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private String colored(Map<String,Long> s){
        StringBuilder sb = new StringBuilder("<font color='#607D8B'>Y: </font>");
        for(String key:s.keySet()){
            long value=s.get(key);
            key = key.replaceAll("[0-9]","");
            if (key.matches("[qeuioaj]")){
                sb.append("<font color='#00796B'>").append(key).append("</font>");
            }else{
                sb.append("<font color='#004D40'>").append(key).append("</font>");
            }
            sb.append("<font color='#B2DFDB'>").append("->").append("</font>")
                    .append("<font color='#0097A7'>").append(value).append("</font>").append(";");
        }
        return sb.toString();
    }

    private String avg(List<Double> s){
        double sum=0.0;
        for(double value:s){
            sum+=value;
        }
        return String.valueOf(sum/s.size());
    }
}
