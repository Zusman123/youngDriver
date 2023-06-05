package com.mz.SmartApps.youngDriver;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PaymentsAdapter extends BaseAdapter {

    private Context context;
    View view;
    private ArrayList<Payment> payments;

    public PaymentsAdapter(ArrayList<Payment> payments, Context context) {
        this.payments = payments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return payments.size();
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
        view = inflater.inflate(R.layout.payment_item, null);

        TextView name = view.findViewById(R.id.namePay);
        TextView date = view.findViewById(R.id.datePay);
        TextView price = view.findViewById(R.id.pricePay);

        name.setText(payments.get(position).getName());
        date.setText(payments.get(position).getStringDate());
        price.setText(payments.get(position).getPrice()+" ₪");

        return view;
    }

    public void updateGroups(ArrayList<Payment> updatedPayments) {
        payments.clear();
        payments.addAll(updatedPayments);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

}
