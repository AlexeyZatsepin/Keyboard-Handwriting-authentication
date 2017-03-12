package com.securityconfirm.layout.ui;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.securityconfirm.layout.R;
import com.securityconfirm.layout.model.Account;
import com.securityconfirm.layout.model.Bounds;
import com.securityconfirm.layout.model.SecurityContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.securityconfirm.layout.math.Utils.discardingOutliers;
import static com.securityconfirm.layout.math.Utils.dispersion;
import static com.securityconfirm.layout.math.Utils.fisherCheck;
import static com.securityconfirm.layout.math.Utils.limitValueChecker;
import static com.securityconfirm.layout.math.Utils.mathExpectation;
import static com.securityconfirm.layout.math.Utils.studentCheck;

public class SecureConfirmationLayout extends LinearLayout{

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
    private Map<String,Long> pressingLength=new HashMap<String,Long>();
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
    private Context context;

    public SecureConfirmationLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SecureConfirmationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();

    }

    public SecureConfirmationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SecureConfirmationLayout, defStyleAttr, 0);
        type = a.getString(R.styleable.SecureConfirmationLayout_type);
        String text = a.getString(R.styleable.SecureConfirmationLayout_text);
        a.recycle();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.secure_confirmation_layout, this);

        editText = (EditText) findViewById(R.id.editText);

        usernameEditText = (EditText) findViewById(R.id.username);
        usernameEditText.setGravity(Gravity.CENTER);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (Account user: SecurityContext.getUserList()) {
                    if ((user.getUsername().equals(usernameEditText.getText().toString().trim()))&&
                            type.equals("study")){
                        Toast.makeText(context,"This username already in use",Toast.LENGTH_LONG).show();
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
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), 0);
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

    public void rewind(){
        this.fullTime= - System.currentTimeMillis();
        this.del_counter=0;
        this.editText.setText("");
        this.pressingLength.clear();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return verified account or null
     */
    public Account verify(){
        if (textView.getText().toString().equals(editText.getText().toString().trim())){
            fullTime+=System.currentTimeMillis();

            String username = usernameEditText.getText().toString();
            List<Double> mathExpectation = mathExpectation(pressingLength); // base value
            List<Double> dispersion = dispersion(pressingLength, mathExpectation); //base value

            Map<String,Long> clearPressingLength = discardingOutliers(pressingLength,dispersion,mathExpectation); // delete "wrong values"
            mathExpectation = mathExpectation(clearPressingLength); // clear value
            dispersion = dispersion(clearPressingLength,mathExpectation); // clear value

            if (type.equals(context.getString(R.string.study_mode))){
                for (Account user:SecurityContext.getUserList()) {
                    if (user.getUsername().equals(username)){
                        Toast.makeText(context,"This username already in use",Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
                Account account = new Account(username, clearPressingLength, dispersion, fullTime, del_counter);
                SecurityContext.add(account);
                SecurityContext.save(context);

                Toast.makeText(context,"Successfully joined",Toast.LENGTH_LONG).show();
                return null;
//                context.startActivity(new Intent(context,PreActivity.class));
            }else if(type.equals(context.getString(R.string.recognition_mode))){
                for (Account user:SecurityContext.getUserList()) {
                    if (user.getUsername().equals(username)){

                        if(fisherCheck(user.getS(), dispersion)
                                && limitValueChecker(del_counter,user.getDelCounter(), Bounds.BACKSPACES_LIMIT)
                                && limitValueChecker(fullTime,user.getFullTime(), Bounds.TIME_LIMIT)
                                && studentCheck(user.getY(),clearPressingLength)){
                            Account verified=new Account(username, clearPressingLength, dispersion,fullTime,del_counter);
                            SecurityContext.remove(user);
                            SecurityContext.add(verified);
                            SecurityContext.save(context);
                            return verified;
//                            Intent intent=new Intent(context,CongratulationActivity.class).putExtra(Intent.EXTRA_TEXT,user).putExtra("DATA",verified);
//                            context.startActivity(intent);
                        }else {
                            Toast.makeText(context,"Please try again, bad handwriting",Toast.LENGTH_LONG).show();
                            return null;
//                            context.startActivity(new Intent(context,PreActivity.class));
                        }

                    }else{
                        //Toast.makeText(getApplicationContext(),"User doesn't exists",Toast.LENGTH_SHORT).show();
                        return null;
//                        context.startActivity(new Intent(context,PreActivity.class));
                    }
                }
            }
        }else {
            Toast.makeText(context,"Text isn't correct",Toast.LENGTH_LONG).show();
            editText.setText("");
            fullTime=-System.currentTimeMillis();
            return null;
        }
        return null;
    }
}
