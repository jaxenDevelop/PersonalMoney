package com.example.personalmoney;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.Gravity.CENTER;

public class NoteMoneyFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton addItem;
    private TableLayout tableLayout;
    private SharedPreferences sp, sharedPreferences;
    private Handler handler;
    private int CurrentMoney;
    private MoneyDataBase dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private AppCompatButton setCurrentMoney;

    private AppCompatTextView showCurrentMoney, showParentMoney, showMyMoney;
    private static int parentMoney = 0;
    private static float myMoney = 0.0f;
    private static float currentMoney = 0.0f;

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_notemoney, container, false);

        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(this);
        tableLayout = view.findViewById(R.id.tableLayout);

        showCurrentMoney = view.findViewById(R.id.showCurrentMoney);
        showParentMoney = view.findViewById(R.id.showParentMoney);
        showMyMoney = view.findViewById(R.id.showMyMoney);

        sp = getActivity().getSharedPreferences("AllMoneyFile", Context.MODE_PRIVATE);
        sharedPreferences = getActivity().getSharedPreferences("MyCurrentAllMoney", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("account", "0");
        currentMoney = Float.parseFloat(str);

        showCurrentMoney.setText(str);

        dbHelper = new MoneyDataBase(getActivity());


        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 0:
                        tableLayout.addView((View) msg.obj);
                        showParentMoney.setText(parentMoney+"");
                        System.out.println("handlerparent:"+parentMoney);
                        myMoney = currentMoney - (float) parentMoney;
                        showMyMoney.setText(myMoney+"");
                    break;

                    case 1:
                        currentMoney = Float.parseFloat(msg.obj.toString());
                        showCurrentMoney.setText(String.valueOf(currentMoney));
                        myMoney = Float.parseFloat(msg.obj.toString()) - (float) parentMoney;
                        showMyMoney.setText(myMoney+"");
                        break;
                }

            }
        };

        initDataBaseData();

        setCurrentMoney = view.findViewById(R.id.setCurrentMoney);
        setCurrentMoney.setOnClickListener(this);
        float initMyMoney = Float.parseFloat(str) - (float) parentMoney;
        showMyMoney.setText(initMyMoney+"");
        return view;

    }

    private TableRow insertData(String date, String amount, String other)
    {
        TextView timeText = new TextView(getActivity());
        timeText.setText(date);
        timeText.setGravity(CENTER);
        timeText.setTextSize(20);


        TextView amountText = new TextView(getActivity());
        amountText.setText(amount);
        amountText.setGravity(CENTER);
        amountText.setTextSize(20);

        TextView otherText = new TextView(getActivity());
        other =  other == null ? "55":other;
        otherText.setText(other);
        otherText.setGravity(CENTER);
        otherText.setTextSize(20);

        TableRow tableRow = new TableRow(getActivity());
        tableRow.addView(timeText);
        tableRow.addView(amountText);
        tableRow.addView(otherText);

        return tableRow;
    }
    private void initDataBaseData() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("mymoney", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String amount = cursor.getString(cursor.getColumnIndex("amount"));
                String other = cursor.getString(cursor.getColumnIndex("other"));
                System.out.println("游标：" + date + "  " + amount + " " + other);

                TextView timeText = new TextView(getActivity());
                timeText.setText(date);
                timeText.setGravity(CENTER);
                timeText.setTextSize(22);


                TextView amountText = new TextView(getActivity());
                amountText.setText(amount);
                amountText.setGravity(CENTER);
                amountText.setTextSize(22);

                TextView otherText = new TextView(getActivity());
                other =  other == null ? "55":other;
                otherText.setText(other);
                otherText.setGravity(CENTER);
                otherText.setTextSize(22);

                TableRow tableRow = new TableRow(getActivity());
                tableRow.addView(timeText);
                tableRow.addView(amountText);
                tableRow.addView(otherText);

                Message message = Message.obtain();
                message.what = 0;
                message.obj = tableRow;
                handler.sendMessage(message);

                parentMoney += Integer.parseInt(amount);
            } while (cursor.moveToNext());


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addItem:
                Intent intent = new Intent(getActivity(), FillInfo.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.setCurrentMoney:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText editText = new EditText(getActivity());
                editText.setHint(showCurrentMoney.getText());
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setTitle("设置当前账户总金额").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("account", editText.getText().toString());
                        editor.commit();
                        new Thread()
                        {
                            @Override
                            public void run() {
                                super.run();
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = editText.getText();
                                handler.sendMessage(message);
                            }
                        }.start();
//                        showCurrentMoney.setText(editText.getText());
                    }
                }).show();
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
//                        sqLiteDatabase.execSQL("insert into mymoney(date, amount, other) values"+"(" + time +","+ amount+"," + "'"+other + "'"+ ")");

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("date", time);
                        contentValues.put("amount", amount);
                        contentValues.put("other", other);
                        sqLiteDatabase.insert("mymoney", null, contentValues);

                        TextView timeText = new TextView(getActivity());
                        timeText.setText(time);
                        timeText.setGravity(CENTER);
                        timeText.setTextSize(22);


                        TextView amountText = new TextView(getActivity());
                        amountText.setText(amount);
                        amountText.setGravity(CENTER);
                        amountText.setTextSize(22);

                        TextView otherText = new TextView(getActivity());
                        otherText.setText(other);
                        otherText.setGravity(CENTER);
                        otherText.setTextSize(22);

                        TableRow tableRow = new TableRow(getActivity());
                        tableRow.addView(timeText);
                        tableRow.addView(amountText);
                        tableRow.addView(otherText);

                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = tableRow;

                        parentMoney += Integer.parseInt(amount);

                        handler.sendMessage(message);

//                        SharedPreferences.Editor editor = sp.edit();
//                        CurrentMoney = sp.getInt("CurrentMoney", 0);
//                        int NewCurrentMoney = Integer.parseInt(amount) + CurrentMoney;
//                        editor.putInt("CurrentMoney", NewCurrentMoney);
//                        editor.commit();


                        System.out.println("parent:"+parentMoney);

                    }
                }.start();
                break;

        }
    }
}
