package com.mz.SmartApps.youngDriver;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PaymentsFragment extends Fragment {
    private PaymentsDB paymentsDB;
    private int totalPayment, paidP, notPaidP;
    private Dialog addialog;
    private ArrayList<Payment> payments;
    private PaymentsAdapter paymentsAdapter;
    private TextView sumPayments, paid, notPaid;
    private AutoCompleteTextView nameP;
    private EditText priceET;
    private Button okAdd;
    private ListView paymentsList;
    private FloatingActionButton addPayment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments,container,false);
         paymentsList = view.findViewById(R.id.paymentsList);
         addPayment = view.findViewById(R.id.addPayment);



        paymentsDB = new PaymentsDB(getActivity());
        ViewGroup header = (ViewGroup)getLayoutInflater().inflate(R.layout.total_payents_header, paymentsList, false);
        sumPayments = header.findViewById(R.id.sumPayments);
        paid = header.findViewById(R.id.paymentsPaid);
        notPaid = header.findViewById(R.id.paymentsNotPaid);
        header.setLongClickable(false);
       setSumDatas();
        paymentsList.addHeaderView(header,null,false);


        LessonsDB lessonsDB = new LessonsDB(getActivity());
        if (lessonsDB.getLessonsCount()>0){
            ViewGroup lessonsHeader = (ViewGroup) getLayoutInflater().inflate(R.layout.payment_item,paymentsList,false);
            TextView name = lessonsHeader.findViewById(R.id.namePay);
            TextView date = lessonsHeader.findViewById(R.id.datePay);
            TextView price = lessonsHeader.findViewById(R.id.pricePay);
            name.setText("שיעורי נהיגה");
            name.setTextColor(getResources().getColor(R.color.colorPrimary));
            date.setVisibility(View.INVISIBLE);

            price.setText(lessonsDB.getTotalPayment()+"₪");
            paymentsList.addHeaderView(lessonsHeader);
        }
        payments = paymentsDB.getSortPaymentsByDate();
        paymentsAdapter = new PaymentsAdapter(payments,getActivity());
        paymentsList.setAdapter(paymentsAdapter);

        paymentsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > paymentsList.getHeaderViewsCount()-1 ){
                    PopupMenu pm = new PopupMenu(getActivity(), view);
                    pm.getMenuInflater().inflate(R.menu.payment_menu, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int itemId = menuItem.getItemId();
                            if (itemId == R.id.del_Payment_item) {
                                paymentsDB.deleteOne(payments.get(position-paymentsList.getHeaderViewsCount()).getId());
                                payments = paymentsDB.getSortPaymentsByDate();
                                paymentsAdapter.updateGroups(payments);
                                setSumDatas();
                                return true;
                            }
                            return false;
                        }
                    });
                    pm.show();
                    return true;
                }

                return false;
            }
        });


        setAddPaymentDilaog();

        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addialog.show();
            }
        });
        return view;
    }

    //Sets the summary data for payments.
    private void setSumDatas() {
        totalPayment = paymentsDB.getTotalPayments();
        notPaidP = paymentsDB.getTotalNotPaidAndLessonsNP();
        paidP = totalPayment-notPaidP;
        sumPayments.setText(totalPayment+"₪");
        paid.setText(paidP+"₪");
        notPaid.setText(notPaidP+"₪");
    }

    //Sets up the add payment dialog. Initializes the dialog and its layout.
    private void setAddPaymentDilaog() {
        addialog= new Dialog(getActivity());
        addialog.setContentView(R.layout.add_payment_dialog);
        nameP = addialog.findViewById(R.id.nameAutoC);
        priceET = addialog.findViewById(R.id.priceDialog);
        okAdd = addialog.findViewById(R.id.addPayBtn);
        String[] payNames = new String[]{"אגרת טסט חיצוני","דמי הרשמה","טופס ירוק","טסט חיצוני"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_singlechoice, payNames);
        nameP.setAdapter(adapter);
        nameP.setThreshold(0);
        EditText[] editTexts = new EditText[]{nameP,priceET};
        ProperInput properInput = new ProperInput();
        nameP.addTextChangedListener(properInput.cheakProperInput(editTexts,okAdd));
        priceET.addTextChangedListener(properInput.cheakProperInput(editTexts,okAdd));

        okAdd.setEnabled(false);
        okAdd.setBackgroundResource(R.color.gray);


        okAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date formatedDate = Calendar.getInstance().getTime();
                paymentsDB.addPayment(new Payment(0,nameP.getText().toString(), formatedDate,Integer.parseInt(priceET.getText().toString())));
               priceET.setText(null);
               nameP.setText(null);
                addialog.dismiss();
                payments = paymentsDB.getSortPaymentsByDate();
                paymentsAdapter.updateGroups(payments);
                setSumDatas();
            }
        });
    }


}
