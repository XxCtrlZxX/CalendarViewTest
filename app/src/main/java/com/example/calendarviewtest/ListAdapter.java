package com.example.calendarviewtest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder> {

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView dateItemText, numItemText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            dateItemText = itemView.findViewById(R.id.dateItemText);
            numItemText = itemView.findViewById(R.id.numItemText);
        }

        public void onBind(DateItem dateItem) {
            numItemText.setText(Integer.toString(dateItem.num));
            dateItemText.setText(dateItem.date);
            dateItemText.setTextColor(dateItem.txtColor);
        }
    }

    public ArrayList<DateItem> items = new ArrayList<>();
    //ListAdapter(ArrayList<DateItem> list) { mData = list; }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayOutInflater를 이용해서 원하는 레이아웃을 띄워줍니다.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.date_item, parent, false);    // 에러 이유: appcompat 버전과 recyclerView 버전이 같아야 함!
        return new ListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // ItemViewHolder가 생성되고 넣어야 할 코드들을 넣어준다.
        // 보통 onBind 함수 안에 모두 넣어줍니다.
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(DateItem dateItem) {
        // items에 DateItem객체 추가
        items.add(dateItem);
        // 추가 후 Adapter에 데이터가 변경된 것을 알림
        notifyDataSetChanged();
    }

    /*public boolean onItemMove(int from_position, int to_position) {
        // 이동할 객체 저장
        DateItem dateItem = items.get(from_position);
        // 이동할 객체 삭제
        items.remove(from_position);
        // 이동하고 싶은 position에 추가
        items.add(to_position, dateItem);

        // Adapter에 데이터 이동알림
        notifyItemMoved(from_position, to_position);
        return true;
    }

    public void onItemSwipe(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }*/
}
