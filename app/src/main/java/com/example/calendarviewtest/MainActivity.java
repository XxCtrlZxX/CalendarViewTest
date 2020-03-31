package com.example.calendarviewtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String STRING_DATA_NAME = "Data";

    CalendarView mCalendarView;
    String date;
    Button addBtn, removeBtn;
    RecyclerView recyclerView;
    ListAdapter adapter;
    Switch colorSwitch;

    int textColor;
    ArrayList<String> dateArray = new ArrayList<>();

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendarView = findViewById(R.id.calendarView);
        addBtn = findViewById(R.id.addBtn);
        removeBtn = findViewById(R.id.removeBtn);
        recyclerView = findViewById(R.id.recyclerView);
        colorSwitch = findViewById(R.id.switch1);

        textColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

        SharedPreferences pref = getSharedPreferences(STRING_DATA_NAME, 0);
        editor = pref.edit();

        // RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        // RecyclerView의 Adapter 세팅
        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        // 첫 화면 세팅
        long now = System.currentTimeMillis();  // 현재 시간을 가져온다.
        Date mDate = new Date(now); // Date 형식으로 고친다.
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd");
        date = simpleDate.format(mDate);  // 날짜, 시간을 가져오고 싶은 형태로 가져올 수 있다.
        String json = pref.getString("dateArray", "");  // string 가져오기
        dateArray = getDateArray(json); // 변환 후 가져오기
        for (String i: dateArray) { // recyclerView에 값 넣기
            DateItem dateItem = new DateItem();
            dateItem.setDate(i);
            dateItem.setTxtColor(textColor);
            adapter.addItem(dateItem);
            adapter.notifyDataSetChanged();
        }

        colorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

        });

        // 캘린더 날짜 선택
        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String clickedDate = year + "/" + String.format("%02d", (month + 1)) + "/" + String.format("%02d",  dayOfMonth);
            if (clickedDate.equals(date)) {
                OnAddBtnClick(view);
            } else {
                date = clickedDate;
            }
        });

        removeBtn.setOnClickListener(v -> {
            editor.putString("dateArray", "");
            editor.commit();
            Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    public void OnAddBtnClick(View v) {
        for (String i: dateArray) {
            if (date.equals(i)) {
                Toast.makeText(this, "이미 날짜가 존재 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        dateArray.add(date);

        // 객체 생성, 값 세팅
        DateItem dateItem = new DateItem();
        dateItem.setDate(date);
        dateItem.setTxtColor(textColor);

        // ListAdapter에 객체 추가
        adapter.addItem(dateItem);
        adapter.notifyDataSetChanged();

        // SharedPreferences에 저장
        if (!dateArray.isEmpty()) {
            editor.putString("dateArray", ArrayList_To_String(dateArray));
        } else {
            editor.putString("dateArray", "");
        }
        editor.commit();
    }

    // ArrayList를 JSONArray로 변환한 후 String으로 리턴
    private String ArrayList_To_String(ArrayList<String> arrayList) {
        JSONArray jsonArray = new JSONArray();
        for (String i: arrayList) {
            jsonArray.put(i);
        }
        return jsonArray.toString();
    }

    // String을 ArrayList<String>으로 변환한 후 리턴
    private ArrayList<String> getDateArray(String json) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (json.length() > 0) {
            try {
                JSONArray jArray = new JSONArray(json);
                for (int i = 0; i < jArray.length(); i++) {
                    String data = jArray.optString(i);
                    arrayList.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}