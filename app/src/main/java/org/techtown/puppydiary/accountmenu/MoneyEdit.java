package org.techtown.puppydiary.accountmenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.puppydiary.R;

//moneyTab item click 시 나오는 수정 삭제 화면
public class MoneyEdit extends AppCompatActivity {

    private  static Context context;
    ActionBar actionBar;

    TextView tv_date;
    TextView tv_price;
    TextView tv_memo;
    int getprice = 0;
    String getmemo = null;
    int price = 0;
    String memo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_edit);

        MoneyEdit.context = getApplicationContext();
        actionBar = getSupportActionBar();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffD6336B));
        getSupportActionBar().setTitle("댕댕이어리");
        actionBar.setIcon(R.drawable.white_puppy) ;
        actionBar.setDisplayUseLogoEnabled(true) ;
        actionBar.setDisplayShowHomeEnabled(true) ;


        final DBHelper_money dbHelper = new DBHelper_money(getApplicationContext(), "mt6.db", null, 1);

        final Intent intent = new Intent(getIntent());
        final int position = intent.getIntExtra("position", 0);
        final int getyear = intent.getIntExtra("year", 0);
        final int getmonth = intent.getIntExtra("month", 0);
        final int getday = intent.getIntExtra("day", 0);

        getmemo = dbHelper.memo(position, getyear, getmonth, getday);
        getprice = dbHelper.price(position, getyear, getmonth, getday);

        final String getdate = getyear + "/" + getmonth + "/" + getday;


        tv_price = findViewById(R.id.price_data);
        tv_price.setText(Integer.toString(getprice));

        tv_memo = findViewById(R.id.memo_data);
        tv_memo.setText(getmemo);

        // 날짜 고정 : 수정 불가
        tv_date = findViewById(R.id.date_data);
        tv_date.setText(getdate);


        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                /*
                price = Integer.parseInt(et_price.getText().toString());
                //price = et_price.getText().toString();
                memo = et_memo.getText().toString();
                dbHelper.update(position, getyear, getmonth, getday, price, memo);
                Intent intent_after = new Intent(MoneyEdit.this, MoneyTab.class);
                intent_after.putExtra("after_position", position);
                intent_after.putExtra("after_year", getyear);
                intent_after.putExtra("after_month", getmonth);
                intent_after.putExtra("after_day", getday);
                intent_after.putExtra("after_memo", memo);
                intent_after.putExtra("after_price", price);
                startActivity(intent_after);
                Toast toastView = Toast.makeText(getApplicationContext(), "수정되었습니다" , Toast.LENGTH_LONG);
                toastView.show();
                 */
            }
        });

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.delete(position, getyear, getmonth, getday);
                Intent intent_after = new Intent(MoneyEdit.this, MoneyTab.class);
                intent_after.putExtra("after_year", getyear);
                intent_after.putExtra("after_month", getmonth);
                intent_after.putExtra("after_day", getday);
                intent_after.putExtra("deletesign", 100);
                startActivity(intent_after);
                Toast toastView = Toast.makeText(getApplicationContext(), "삭제되었습니다" , Toast.LENGTH_LONG);
                toastView.show();
            }
        });
    }
}
