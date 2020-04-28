package com.example.calendarviewtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String STRING_DATA_NAME = "Data";

    CalendarView mCalendarView;
    String date;
    Button addBtn, removeBtn;
    RecyclerView recyclerView;
    ListAdapter adapter;
    ItemTouchHelper helper;
    Switch colorSwitch;

    int colorNum;
    int[] colors = new int[2];
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

        // RecyclerView 구분선 추가
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        //recyclerView.addItemDecoration(dividerItemDecoration);

        colors[0] = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
        colors[1] = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        colorNum = 0;

        SharedPreferences pref = getSharedPreferences(STRING_DATA_NAME, 0);
        editor = pref.edit();

        // RecyclerView 구분선 추가 (다른 방식)
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        // RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        // RecyclerView의 Adapter 세팅
        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        // ItemTouchHelper 생성
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(new ItemTouchHelperListener() {
            @Override
            public boolean onItemMove(int from_position, int to_position) {
                // 이동할 객체 저장
                DateItem dateItem = adapter.items.get(from_position);
                // 이동할 객체 삭제
                adapter.items.remove(from_position);
                // 이동하고 싶은 position에 추가
                adapter.items.add(to_position, dateItem);

                // Adapter에 데이터 이동알림
                adapter.notifyItemMoved(from_position, to_position);
                return true;
            }

            @Override
            public void onItemSwipe(int position) {
                adapter.items.remove(position);
                adapter.notifyItemRemoved(position);
                dateArray.remove(position);
                //Toast.makeText(MainActivity.this, String.format("%d", position), Toast.LENGTH_SHORT).show();
                clearRecyclerView();
                resetRecyclerView();
                saveDateArray();
            }
        }));
        // RecyclerView 에 ItemTouchHelper 붙이기
        helper.attachToRecyclerView(recyclerView);

        // 첫 화면 세팅
        long now = System.currentTimeMillis();  // 현재 시간을 가져온다.
        Date mDate = new Date(now); // Date 형식으로 고친다.
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        date = simpleDate.format(mDate);  // 날짜, 시간을 가져오고 싶은 형태로 가져올 수 있다.
        String json = pref.getString("dateArray", "");  // string 가져오기
        dateArray = getDateArray(json); // 변환 후 가져오기
        resetRecyclerView();    // 리사이클러뷰에 값 넣기
        colorSwitch.setChecked(false);
        colorSwitch.setText("Violet  ");
        colorSwitch.setTextColor(colors[colorNum]);

        // 컬러 스위치
        colorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                colorSwitch.setText("Cyan  ");
                colorNum = 1;
            } else {
                colorSwitch.setText("Violet  ");
                colorNum = 0;
            }
            colorSwitch.setTextColor(colors[colorNum]);
        });

        // 캘린더 날짜 선택
        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String clickedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d",  dayOfMonth);
            if (clickedDate.equals(date)) {
                OnAddBtnClick(view);
            } else {
                date = clickedDate;
            }
        });

        // 삭제 버튼
        removeBtn.setOnClickListener(v -> {
            editor.putString("dateArray", "");
            editor.commit();
            clearRecyclerView();
            dateArray.clear();
            Toast.makeText(this, "모두 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    // 추가 버튼 함수
    public void OnAddBtnClick(View v) {
        for (String i: dateArray) {
            if (date.equals(i)) {
                Toast.makeText(this, "이미 날짜가 존재 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        dateArray.add(date);

        // 객체 생성, 값 세팅
        DateItem dateItem = new DateItem(adapter.items.size() + 1, date, colors[colorNum]);

        // ListAdapter에 객체 추가
        adapter.addItem(dateItem);
        adapter.notifyDataSetChanged();

        // SharedPreferences에 저장
        saveDateArray();
    }

    // dateArray 저장하는 함수
    private void saveDateArray() {
        if (!dateArray.isEmpty()) {
            editor.putString("dateArray", ArrayList_To_String(dateArray));
        } else {
            editor.putString("dateArray", "");
        }
        editor.commit();
    }

    // recyclerView 에 값 넣기
    private void resetRecyclerView() {
        for (int i = 0; i < dateArray.size(); i++) {
            DateItem dateItem = new DateItem(i + 1, dateArray.get(i), colors[colorNum]);
            adapter.addItem(dateItem);
            adapter.notifyDataSetChanged();
        }
    }

    // recyclerView 모두 삭제
    private void clearRecyclerView() {
        for (int i = 0; i < dateArray.size(); i++) {
            int n = adapter.items.size() - 1;
            adapter.items.remove(n);
            adapter.notifyItemRemoved(n);
        }
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