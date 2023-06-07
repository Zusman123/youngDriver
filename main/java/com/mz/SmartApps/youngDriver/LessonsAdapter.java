package com.mz.SmartApps.youngDriver;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mz.SmartApps.youngDriver.R;

import java.util.ArrayList;

public abstract class LessonsAdapter extends BaseAdapter {

    private Context context;
    private String typeLesson[] = new String[]{"טסט פנימי", "", "שיעור רגיל", "שיעור וחצי", "שיעור כפול"};
    View view;
    private ArrayList<Lesson> lessons;

    public LessonsAdapter(ArrayList<Lesson> lessons, Context context) {
        this.lessons = lessons;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lessons.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.lesson_item, null);
        TextView name = view.findViewById(R.id.name);
        TextView date = view.findViewById(R.id.date);
        TextView duration = view.findViewById(R.id.duration);
        TextView price = view.findViewById(R.id.price);
        CheckBox cbPaid = view.findViewById(R.id.cbPaid);
        boolean isGroup = lessons.get(position).isGroup();

        if (!isGroup){
            name.setText(typeLesson[(int) (lessons.get(position).getType() * 2)]);
            duration.setText(lessons.get(position).getDuration());
        }
        else {
            name.setText("קבוצה: " + getGroupLsnCount(lessons.get(position).getType())+ " שיעורים");
            name.setTextColor(context.getResources().getColor(R.color.colorAccent));
            duration.setText("");
        }
        date.setText(lessons.get(position).getStringDate());
        price.setText(lessons.get(position).getPrice() + " ₪");
        cbPaid.setChecked(lessons.get(position).isPaid());

        cbPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lessons.get(position).setPaid(isChecked);
                onPaidChanged(isChecked, position);
            }
        });
        return view;
    }

    //Updates the list of lessons with the provided updated lessons.
    public void updateGroups(ArrayList<Lesson> updatedLessons) {
        lessons.clear();
        lessons.addAll(updatedLessons);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    //Returns the formatted string representation of the group lesson count.
    private String getGroupLsnCount(double count){
        if ((count-(int)count) >0)
            return count+"";
        else
            return (int)count +"";
    }
    
    //Called when the paid status of a lesson has changed.
    public abstract void onPaidChanged(boolean checked, int position);

}
