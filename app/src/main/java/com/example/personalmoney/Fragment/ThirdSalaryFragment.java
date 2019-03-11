package com.example.personalmoney.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.personalmoney.FillMoneyActivity;
import com.example.personalmoney.MoneyDataBase;
import com.example.personalmoney.R;
import com.example.personalmoney.setTableRow;

import static com.example.personalmoney.Fragment.FirstFundFragment.addDefaultData;

public class ThirdSalaryFragment extends Fragment implements View.OnClickListener {

    private AppCompatImageView addItem;
    private SQLiteDatabase sqLiteDatabase;
    private MoneyDataBase dbHelper;
    private AppCompatTextView showLastYearMoney, showCurrentYearMoney;
    private static float lastYearMoney, currentYearMoney;
    private Handler handler;
    private android.support.v7.widget.Toolbar initPayFor;
    private TableLayout tableLayout;
    private AppCompatTextView showAllCost;
    private static float allGet = 0.0f;
    private long CYAmount;
    private long LYAmount;
    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_payfor, container, false);

        CYAmount = System.currentTimeMillis()/365/24/60/60/1000 + 1970;
        LYAmount = CYAmount - 1;
        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(this);
        dbHelper = new MoneyDataBase(getActivity());
        tableLayout = view.findViewById(R.id.tableLayout);
        showAllCost = view.findViewById(R.id.showAllCost);
        showLastYearMoney = view.findViewById(R.id.showLastYearMoney);
        showCurrentYearMoney = view.findViewById(R.id.showCurrentYearMoney);
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
                        showAllCost.setText(allGet+"");
                        break;
                    case 1:
                        showAllCost.setText(allGet+"");
                        showLastYearMoney.setText(lastYearMoney+"");
                        showCurrentYearMoney.setText(currentYearMoney+"");
                        break;
                }

            }
        };

        initDataBaseData();
        setAllCost();
        initPayFor = view.findViewById(R.id.initPayFor);
        initPayFor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("是否重置数据库？")
                        .setMessage("当前页面数据将恢复初始状态！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sqLiteDatabase = dbHelper.getWritableDatabase();
                                sqLiteDatabase.delete("payfor", null,null);

                                new AlertDialog.Builder(getActivity())
                                        .setTitle("重置已完成，应用将自动退出！")
                                        .setMessage("重新打开将导入预置数据").setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getActivity(), Object.class);

                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                            }
                        }).show();
                return false;
            }
        });
        return view;
    }

    private void setAllCost() {
        allGet = 0;
        lastYearMoney = 0;
        currentYearMoney = 0;
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("payfor", null,null, null,null, null,null);
        if (cursor.moveToFirst())
        {
            do {
                allGet += cursor.getFloat(cursor.getColumnIndex("amount"));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        Cursor cursor1 = sqLiteDatabase.rawQuery("select sum(amount) as lastyearmoney from payfor where date like  ?", new String[]{LYAmount+"%"});
        if (cursor1.moveToFirst())
        {
            do {
                lastYearMoney = cursor1.getFloat(cursor1.getColumnIndex("lastyearmoney"));
            }
            while (cursor1.moveToNext());
        }
        cursor1.close();

        Cursor cursor2 = sqLiteDatabase.rawQuery("select sum(amount) as currentyear from payfor where date like  ?", new String[]{CYAmount+"%"});
        if (cursor2.moveToFirst())
        {
            do {
                currentYearMoney = cursor2.getFloat(cursor2.getColumnIndex("currentyear"));
            }
            while (cursor2.moveToNext());
        }
        cursor2.close();

        new Thread()
        {
            @Override
            public void run() {
                super.run();
                Message message = Message.obtain();
                message.what = 1;
                message.obj = allGet;
                handler.sendMessage(message);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addItem:
                Intent intent = new Intent(getActivity(), FillMoneyActivity.class);
                intent.putExtra("from", 2);
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
                        sqLiteDatabase.insert("payfor", null, contentValues);

                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = new setTableRow().setTableRow(time, amount, other, getActivity());

                        handler.sendMessage(message);

                        setAllCost();

                    }
                }.start();
                break;
        }
    }

    private void initDataBaseData() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("payfor", null, null, null, null, null, null);
        ContentValues contentValues = new ContentValues();
        if (!cursor.moveToFirst())
        {
            cursor.close();
            addDefaultData(contentValues, sqLiteDatabase,"2018-05-04", 4888, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-06-05", 3000, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-07-01", 500, "双过半奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-07-30", 5427, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-08-01", 776, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-08-30", 6798, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-09-05", 1538, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-09-29", 6980, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-10-08", 960, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-11-01", 7399, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-11-05", 918, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-11-30", 7230, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2018-12-05", 935, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-02", 24173, "年终奖", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-04", 1068, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-09", 4850, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-11", 2250, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-02", 997, "工资", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-28", 4470, "奖金", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-05", 1071.99, "工资", 2);

            /**读数据**/
            final Cursor cursorNew = sqLiteDatabase.query("payfor", null, null, null, null, null, null);

            if (cursorNew.moveToFirst())
            {
                do {
                    String date = cursorNew.getString(cursorNew.getColumnIndex("date"));
                    String amount = cursorNew.getString(cursorNew.getColumnIndex("amount"));
                    String other = cursorNew.getString(cursorNew.getColumnIndex("other"));

                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = new setTableRow().setTableRow(date, amount, other, getActivity());
                    handler.sendMessage(message);

                    setAllCost();

                }while (cursorNew.moveToNext());
            }

            Toast.makeText(getActivity(),"工资统计数据库初始化完成！", Toast.LENGTH_LONG).show();
        }

        else if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String amount = cursor.getString(cursor.getColumnIndex("amount"));
                String other = cursor.getString(cursor.getColumnIndex("other"));

                Message message = Message.obtain();
                message.what = 0;
                message.obj = new setTableRow().setTableRow(date, amount, other, getActivity());
                handler.sendMessage(message);

                setAllCost();
            } while (cursor.moveToNext());
        }
    }
}
