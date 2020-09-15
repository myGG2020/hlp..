package org.techtown.puppydiary;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionBar = getSupportActionBar();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffD6336B));
        getSupportActionBar().setTitle("댕댕이어리");
        actionBar.setIcon(R.drawable.white_puppy);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        final Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);


        // 로그인 누르면 다음 화면으로 넘어가게
        Button button_lgn = findViewById(R.id.btn_login);
        button_lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_cal = new Intent(getApplicationContext(), CalendarTab.class);
                startActivity(intent_cal);
            }
        });

        // 회원가입 누르면 회원 가입으로 넘어가게
        TextView tv_join = findViewById(R.id.tv_join);
        tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_join = new Intent(getApplicationContext(), Signup.class);
                startActivityForResult(intent_join, 2000);
            }
        });
    }
}
