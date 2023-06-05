package com.mz.SmartApps.youngDriver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    private FloatingActionButton addLesson;

    private ListView lessonsLV;
    private LessonsAdapter lessonsAdapter;
    private  LessonsDB lessonsDB;
    private ArrayList<Lesson> lessons;
    private TextView sumLessons, sumPaid, sumLength, sumNotPaid;
    private AdTools adTools;

    private double lessonsCount;
    public HomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addLesson = view.findViewById(R.id.addLesson);
        lessonsLV = view.findViewById(R.id.lessonsLV);

        lessonsDB = new LessonsDB(getActivity());
        lessons = lessonsDB.getSortLessonsByDate();

        ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.sum_header, lessonsLV, false);
        sumLessons = header.findViewById(R.id.sumLessons);
        sumPaid = header.findViewById(R.id.sumPaid);
        sumLength = header.findViewById(R.id.sumLength);
        sumNotPaid = header.findViewById(R.id.sumNotPaid);


        setSumDatas();
        adTools = new AdTools(getActivity());

        if (lessons.size() > 0) {
            adTools.loadInterstitialAd(getResources().getString(R.string.interstitial_payments));
        }


        lessonsLV.addHeaderView(header, null, false);
        lessonsAdapter = new LessonsAdapter(lessons, getActivity()) {
            @Override
            public void onPaidChanged(boolean checked, int position) {
                Lesson l = lessons.get(position);
                l.setPaid(checked);
                lessonsDB.updateGroup(l);
                lessons = lessonsDB.getSortLessonsByDate();
                lessonsAdapter.updateGroups(lessons);
                setSumDatas();
            }
        };
        //lessonsLV.setEmptyView(view.findViewById(R.id.noLessons));
        lessonsLV.setAdapter(lessonsAdapter);

        lessonsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu pm = new PopupMenu(getActivity(), view);
                Log.d(MainActivity.TAG, "onItemLongClick: ");

                pm.getMenuInflater().inflate(R.menu.lesson_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.del_ls) {
                            lessonsDB.deleteOne(lessons.get(position - 1).getId());
                            lessons = lessonsDB.getSortLessonsByDate();
                            lessonsAdapter.updateGroups(lessons);
                            setSumDatas();
                            return true;
                        }
                        return false;
                    }
                });
                pm.show();

                return true;
            }
        });

        addLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adTools.isInterstitialAdNull())
                    adTools.loadInterstitialAd(getResources().getString(R.string.interstitial_payments));
                final BottomSheetDialog dialog = new AddLessonDialog(getActivity()) {
                    @Override
                    public void onLessonAdded() {
                        lessons = lessonsDB.getSortLessonsByDate();
                        lessonsAdapter.updateGroups(lessons);
                        adTools.showInterstitialAd();
                        setSumDatas();
                    }
                };
                dialog.show();
            }
        });
        return view;
    }
    private void setSumDatas() {
        PaymentsDB paymentsDB = new PaymentsDB(getContext());
        lessonsCount =lessonsDB.getLessonsCount();
        if ((lessonsCount-(int)lessonsCount) >0)
            sumLessons.setText(lessonsCount+"");
        else
            sumLessons.setText((int)lessonsCount+"");
        sumPaid.setText(paymentsDB.getTotalPayments()+"₪");
        sumLength.setText(lessonsDB.getTotalLessonsLength());
        sumNotPaid.setText(lessonsDB.getTotalNotPaid()+"₪");
    }

}
