package org.techtown.puppydiary.accountmenu;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.puppydiary.CalendarTab;
import org.techtown.puppydiary.KgTab;
import org.techtown.puppydiary.MypuppyTab;
import org.techtown.puppydiary.R;
import org.techtown.puppydiary.SetPuppy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MoneyTab extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";
    ActionBar actionBar;
    public static Context context;

    Button calendar;
    Button kg;
    Button money;
    Button puppy;


    Button moneycalendar;
    Button plus;

    TextView tv_date;
    TextView tv_total;
    EditText et_price;
    EditText et_memo;
    int price = 0;
    String memo = null;

    Calendar myCalendar = Calendar.getInstance();

    int year_money = 0;
    int month_money = 0;
    int day_money = 0;

    int position = 0;

    DBHelper_money dbHelper = null;
    Cursor cursor;
    EditText[] EditTexts;
    MoneytabItem items;
    ArrayList<MoneytabItem> itemArray = null;
    ListAdapter adapter;
    ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);


        actionBar = getSupportActionBar();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffD6336B));
        getSupportActionBar().setTitle("댕댕이어리");
        actionBar.setIcon(R.drawable.white_puppy) ;
        actionBar.setDisplayUseLogoEnabled(true) ;
        actionBar.setDisplayShowHomeEnabled(true) ;


        calendar = findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_calendar = new Intent(getApplicationContext(), CalendarTab.class); //일단 바로 검색결과 띄음
                startActivity(intent_calendar);
            }
        });

        kg = findViewById(R.id.kg);
        kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_kg = new Intent(getApplicationContext(), KgTab.class); //일단 바로 검색결과 띄음
                startActivity(intent_kg);
            }
        });

        money = findViewById(R.id.account);
        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_money = new Intent(getApplicationContext(), MoneyTab.class); //일단 바로 검색결과 띄음
                startActivity(intent_money);
            }
        });

        puppy = findViewById(R.id.puppy);
        puppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_puppy = new Intent(getApplicationContext(), MypuppyTab.class); //일단 바로 검색결과 띄음
                startActivity(intent_puppy);
            }
        });
        listview = findViewById(R.id.accountlist);
        listview.setOnItemClickListener(this);

        tv_date = findViewById(R.id.date);
        tv_total = findViewById(R.id.total_sum);
        et_memo = (EditText) findViewById(R.id.edit_context);
        et_price = (EditText) findViewById(R.id.edit_price);

        itemArray = new ArrayList<MoneytabItem>();
        adapter = new ListAdapter(this, itemArray);

        dbHelper = new DBHelper_money(getApplicationContext(), "mt6.db", null, 1);
        EditTexts = new EditText[]{
                (EditText) findViewById(R.id.edit_context),
                (EditText) findViewById(R.id.edit_price)
        };


        // 아이템 수정, 삭제 후 해당 날짜로 돌아오기
        Intent intent_after = new Intent(getIntent());
        int after_position = intent_after.getIntExtra("after_position", 0);
        int after_year = intent_after.getIntExtra("after_year", 0);
        int after_month = intent_after.getIntExtra("after_month", 0);
        int after_day = intent_after.getIntExtra("after_day", 0);
        String after_memo = intent_after.getStringExtra("after_memo");
        int after_price = intent_after.getIntExtra("after_price", 0);

        if(after_year != 0){
            //edit, delete 후 화면
            year_money = after_year;
            month_money = after_month;
            day_money = after_day;
            tv_date.setText(year_money + "/" + month_money + "/" + day_money);
            itemArray.clear();
            CursorToArray();
            adapter.setArrayList(itemArray);
            //items = new MoneytabItem(after_memo, after_price);
            //itemArray.add(after_position-1, items);
            //int trash = dbHelper.getCount(year_money, month_money, day_money);
            //itemArray.remove(trash-1);
            tv_total.setText(dbHelper.getSum(year_money, month_money, day_money) + "원");
            listview.setAdapter(adapter);
        } else {
            // 기본 시작화면 : 오늘날짜 세팅
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d", Locale.KOREA);
            tv_date.setText(sdf.format(myCalendar.getTime()));
            year_money = myCalendar.get(Calendar.YEAR);
            month_money = myCalendar.get(Calendar.MONTH) + 1;
            day_money = myCalendar.get(Calendar.DAY_OF_MONTH);
            itemArray.clear();
            CursorToArray();
            adapter.setArrayList(itemArray);
            listview.setAdapter(adapter);
            tv_total.setText(dbHelper.getSum(year_money, month_money, day_money) + "원");
        }


        // 날짜 선택 -> 날짜에 해당하는 아이템들 보여줌
        final DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d", Locale.KOREA);
                tv_date.setText(sdf.format(myCalendar.getTime()));
                year_money = myCalendar.get(Calendar.YEAR);
                month_money = myCalendar.get(Calendar.MONTH) + 1;
                day_money = myCalendar.get(Calendar.DAY_OF_MONTH);
                itemArray.clear();
                CursorToArray();
                adapter.setArrayList(itemArray);
                tv_total.setText(dbHelper.getSum(year_money, month_money, day_money) + "원");
                listview.setAdapter(adapter);
            }
        };

        // 달력 버튼 (날짜 선택)
        moneycalendar = findViewById(R.id.money_calendar);
        moneycalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MoneyTab.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // + 버튼 (아이템 추가)
        plus = findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memo = et_memo.getText().toString();
                //price = et_price.getText().toString();
                price = Integer.parseInt(et_price.getText().toString());

                //중복체크
                switch (dbHelper.check(year_money, month_money, day_money, price, memo)){
                    case 1 : {
                        Toast.makeText(getApplicationContext(), "중복 항목이 존재합니다.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default : {
                        dbHelper.insert(year_money, month_money, day_money, price, memo);
                        itemArray.clear();
                        CursorToArray();
                        adapter.setArrayList(itemArray);
                        adapter.notifyDataSetChanged();
                        tv_total.setText(dbHelper.getSum(year_money, month_money, day_money) + "원");

                        et_memo.getText().clear();
                        et_price.getText().clear();
                        // 저장 버튼 누른 후 키보드 안보이게 하기
                        InputMethodManager imm =
                                (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
                        imm.hideSoftInputFromWindow(et_price.getWindowToken(), 0 );
                    }
                }

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        int setyear = year_money;
        int setmonth = month_money;
        int setday = day_money;
        position = i+1;

        Intent intent = new Intent(MoneyTab.this, MoneyEdit.class);
        intent.putExtra("position", position);
        intent.putExtra("year", setyear);
        intent.putExtra("month", setmonth);
        intent.putExtra("day", setday);
        startActivity(intent);

    }


    public static class MoneytabItem {

        String memo;
        int price;

        public MoneytabItem(String memo, int price) {
            this.memo = memo;
            this.price = price;
        }

    }


    private void CursorToArray() {

        cursor = null;
        cursor = dbHelper.getResult(year_money, month_money, day_money);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                items = new MoneytabItem(
                        cursor.getString(cursor.getColumnIndex("memo")),
                        cursor.getInt(cursor.getColumnIndex("price"))
                );
                itemArray.add(items);
            } while (cursor.moveToNext());
        }

        cursor.close();

    }


    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<MoneytabItem> itemList;
        private ViewHolder viewHolder;

        public ListAdapter(Context c, ArrayList<MoneytabItem> array) {
            inflater = LayoutInflater.from(c);
            itemList = array;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertview, ViewGroup parent) {

            View v = convertview;

            if (v == null) {
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.fragment_itemlist, null);
                viewHolder.memo = (TextView) v.findViewById(R.id.item_context);
                viewHolder.price = (TextView) v.findViewById(R.id.item_price);
                v.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            viewHolder.memo.setText(itemList.get(position).memo);
            viewHolder.price.setText(Integer.toString(itemList.get(position).price));

            return v;
        }

        public void setArrayList(ArrayList<MoneytabItem> arrays) {
            this.itemList = arrays;
        }

        public ArrayList<MoneytabItem> getArrayList() {
            return itemList;
        }


        class ViewHolder {
            TextView memo;
            TextView price;
        }
    }


}