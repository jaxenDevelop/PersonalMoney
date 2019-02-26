package com.example.personalmoney;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.view.Gravity.CENTER;

public class DecorationFragment extends Fragment implements View.OnClickListener {

    private AppCompatImageView addItem;
    private SQLiteDatabase sqLiteDatabase;
    private MoneyDataBase dbHelper;
    private Handler handler;
    private TableLayout tableLayout;
    private AppCompatTextView showAllCost;
    private static float allCost = 0.0f;
    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.layout_decoration, container, false);
        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(this);
        dbHelper = new MoneyDataBase(getActivity());
        tableLayout = view.findViewById(R.id.tableLayout);
        showAllCost = view.findViewById(R.id.showAllCost);
        showAllCost.setText("0.0");
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 0:
                        tableLayout.addView((View) msg.obj);
                        showAllCost.setText(allCost+"");
                        break;
                    case 1:
                        showAllCost.setText(allCost+"");
                        break;
                }

            }
        };

        initDataBaseData();
        setAllCost();
        return view;
    }

    private void setAllCost() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("decoration", null,null, null,null, null,null);
        if (cursor.moveToFirst())
        {
            do {
                allCost += cursor.getFloat(cursor.getColumnIndex("amount"));
            }
            while (cursor.moveToNext());
        }
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                Message message = Message.obtain();
                message.what = 1;
                message.obj = allCost;
                handler.sendMessage(message);
            }
        }.start();
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addItem:
                Intent intent = new Intent(getActivity(), FillMoneyActivity.class);
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case 1:
                new Thread()
                {
                    @Override
                    public void run() {
                        super.run();
                        String time = data.getStringExtra("TIME");
                        String amount = data.getStringExtra("AMOUNT");
                        String other = data.getStringExtra("OTHER");

                        sqLiteDatabase = dbHelper.getWritableDatabase();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("date", time);
                        contentValues.put("amount", amount);
                        contentValues.put("other", other);
                        sqLiteDatabase.insert("decoration", null, contentValues);

                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = new setTableRow().setTableRow(time, amount, other, getActivity());

                        handler.sendMessage(message);

                        allCost += Float.parseFloat(amount);

                    }
                }.start();
                break;
        }
    }

    private void initDataBaseData() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("decoration", null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String amount = cursor.getString(cursor.getColumnIndex("amount"));
                String other = cursor.getString(cursor.getColumnIndex("other"));

                Message message = Message.obtain();
                message.what = 0;
                message.obj = new setTableRow().setTableRow(date, amount, other, getActivity());
                handler.sendMessage(message);

            } while (cursor.moveToNext());
        }
    }
}