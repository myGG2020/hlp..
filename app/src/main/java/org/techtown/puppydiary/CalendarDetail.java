package org.techtown.puppydiary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.puppydiary.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CalendarDetail extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;

    ImageView image_upload;
    byte[] image_byte = null;
    Bitmap upload_bitmap = null;

    int waterdrop = 0;
    int injection = 0;

    Button waterdrop_btn;
    Button waterdrop_btn2;
    Button injection_btn;
    Button injection_btn2;
    Button cancel_btn;
    Button save_btn;

    EditText memo_et;
    String text = null;

    TextView tv_date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar_detail);

        final DBHelper_cal dbHelper = new DBHelper_cal(getApplicationContext(), "dbcalendar.db", null, 1);
        final Intent intent = new Intent(getIntent());
        final int pos = intent.getIntExtra("pos", 0);
        final int month = intent.getIntExtra("month", 0);
        final int date = intent.getIntExtra("date", 0);

        tv_date = (TextView) findViewById(R.id.tv_date);
        waterdrop_btn = findViewById(R.id.waterdrop_detail);
        waterdrop_btn2 = findViewById(R.id.waterdrop_color);
        injection_btn = findViewById(R.id.injection_detail);
        injection_btn2 = findViewById(R.id.injection_color);
        dbHelper.getResult_state(pos, month);

        tv_date.setText(month + ". " + date);

        //기본세팅 : 물방울, 주사기 색깔 없음
        if(dbHelper.getResult_state(pos, month) == 0) {
            waterdrop_btn2.setVisibility(View.INVISIBLE);
            injection_btn2.setVisibility(View.INVISIBLE);
        } else if (dbHelper.getResult_state(pos, month) == 1){
            // 물방울만 색깔 있을 때
            waterdrop_btn2.setVisibility(View.VISIBLE);
            waterdrop_btn.setVisibility(View.INVISIBLE);
            injection_btn2.setVisibility(View.INVISIBLE);
            injection_btn.setVisibility(View.VISIBLE);
        } else if (dbHelper.getResult_state(pos, month) == 2){
            // 주사기만 색깔 있을 때
            waterdrop_btn2.setVisibility(View.INVISIBLE);
            waterdrop_btn.setVisibility(View.VISIBLE);
            injection_btn2.setVisibility(View.VISIBLE);
            injection_btn.setVisibility(View.INVISIBLE);
        } else {
            // 둘 다 색깔 있을 때
            waterdrop_btn2.setVisibility(View.VISIBLE);
            waterdrop_btn.setVisibility(View.INVISIBLE);
            injection_btn2.setVisibility(View.VISIBLE);
            injection_btn.setVisibility(View.INVISIBLE);
        }

        cancel_btn = findViewById(R.id.btn_canceldetail);
        save_btn = findViewById(R.id.btn_savedetail);

        memo_et = (EditText) findViewById(R.id.edittext_memo);
        memo_et.setText(dbHelper.getResult(pos, month));

        image_upload = (ImageView) findViewById(R.id.image_upload);
        image_byte = dbHelper.getResultimg(pos, month);
        System.out.println("open : " + image_byte);
        if (image_byte != null) {
            BitmapFactory.decodeByteArray(image_byte, 0, image_byte.length);
            image_upload.setImageBitmap(upload_bitmap);
        } else {
            image_upload.setImageResource(R.drawable.camera_imageview);
        }


        // on
        waterdrop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waterdrop_btn2.setVisibility(View.VISIBLE);
                waterdrop_btn.setVisibility(View.INVISIBLE);
                waterdrop = 1;
            }
        });

        // off
        waterdrop_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waterdrop_btn.setVisibility(View.VISIBLE);
                waterdrop_btn2.setVisibility(View.INVISIBLE);
                waterdrop = 0;
            }
        });

        // on
        injection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                injection_btn2.setVisibility(View.VISIBLE);
                injection_btn.setVisibility(View.INVISIBLE);
                injection = 2;
            }
        });

        // off
        injection_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                injection_btn.setVisibility(View.VISIBLE);
                injection_btn2.setVisibility(View.INVISIBLE);
                injection = 0;
            }
        });

        memo_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                text = memo_et.getText().toString();
                if (image_byte == null){
                    dbHelper.insert(pos, month, text, null, waterdrop, injection);
                } else {
                    dbHelper.insert(pos, month, text, image_byte, waterdrop, injection);
                }

                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();

                System.out.println("after save : " + image_byte);
                Intent return_tab = new Intent(CalendarDetail.this, CalendarTab.class);
                startActivity(return_tab);

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    //DBHelper dbHelper = new DBHelper(getApplicationContext(), "EDITMEMO.db", null, 1);
                    InputStream stream = getContentResolver().openInputStream(data.getData());
                    upload_bitmap = BitmapFactory.decodeStream(stream);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    upload_bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    image_byte = baos.toByteArray();

                    image_upload.setImageBitmap(upload_bitmap);

                    System.out.println("in : " + image_byte);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

}
