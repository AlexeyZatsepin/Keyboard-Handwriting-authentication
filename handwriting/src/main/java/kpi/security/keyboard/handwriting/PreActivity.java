package kpi.security.keyboard.handwriting;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

/**
 * KeyboardHandwriting
 * Created 5/5/16, with IntelliJ IDE
 * @author Alex
 */
public class PreActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_layout);
        Button login=(Button) findViewById(R.id.login);
        Button logup=(Button) findViewById(R.id.logup);
        login.setTextColor(Color.parseColor("#FFFAFA"));
        logup.setTextColor(Color.parseColor("#FFFAFA"));
        final Animation animationRotateCenter = AnimationUtils.loadAnimation(
                this, R.anim.rotate_center);
        ImageView myImageView = (ImageView) findViewById(R.id.imageView1);
        myImageView.startAnimation(animationRotateCenter);
    }
    public void onLoginClick(View view){
        startActivity(new Intent(this,MainActivity.class).putExtra("TYPE","recognition"));
    }
    public void onLogupClick(View view){
        startActivity(new Intent(this,MainActivity.class).putExtra("TYPE","study"));
    }

    public void onImageClick(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alert_text));
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
