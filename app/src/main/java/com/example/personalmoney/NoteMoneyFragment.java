package com.example.personalmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.view.Gravity.CENTER;

public class NoteMoneyFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton addItem;
    private TableLayout tableLayout;
    private SharedPreferences sp;
    private Handler handler;
    private int CurrentMoney;
    private MoneyDataBase db;
    private SQLiteDatabase sqLiteDatabase;

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_notemoney, container, false);

        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(this);
        tableLayout = view.findViewById(R.id.tableLayout);

        sp = getActivity().getSharedPreferences("AllMoneyFile", 0);

        db = new MoneyDataBase(getActivity());


        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                tableLayout.addView((View) msg.obj);
            }
        };

        initDataBase();
        return view;

    }

    private void initDataBase()
    {
        sqLiteDatabase = db.getReadableDatabase();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addItem:
                Intent intent = new Intent(getActivity(), FillInfo.class);
                startActivityForResult(intent, 1);
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

                        SQLiteDatabase db = NoteMoneyFragment.this.db.getWritableDatabase();
                        db.execSQL("insert into mymoney(date, amount, other) values"+"(" + time +","+ amount+"," + "'"+other + "'"+ ")");

                        TextView timeText = new TextView(getActivity());
                        timeText.setText(time);
                        timeText.setGravity(CENTER);
                        timeText.setTextSize(20);


                        TextView amountText = new TextView(getActivity());
                        amountText.setText(amount);
                        amountText.setGravity(CENTER);
                        amountText.setTextSize(20);

                        TextView otherText = new TextView(getActivity());
                        otherText.setText(other);
                        otherText.setGravity(CENTER);
                        otherText.setTextSize(20);

                        TableRow tableRow = new TableRow(getActivity());
                        tableRow.addView(timeText);
                        tableRow.addView(amountText);
                        tableRow.addView(otherText);

                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = tableRow;
                        handler.sendMessage(message);

                        SharedPreferences.Editor editor = sp.edit();
                        CurrentMoney = sp.getInt("CurrentMoney", 0);
                        int NewCurrentMoney = Integer.parseInt(amount) + CurrentMoney;
                        editor.putInt("CurrentMoney", NewCurrentMoney);
                        editor.commit();

                    }
                }.start();
                break;

        }
    }
}
