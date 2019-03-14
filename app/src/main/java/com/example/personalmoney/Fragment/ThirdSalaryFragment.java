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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.personalmoney.FillMoneyActivity;
import com.example.personalmoney.MoneyDataBase;
import com.example.personalmoney.R;
import com.example.personalmoney.setTableRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.personalmoney.Fragment.FirstFundFragment.addDefaultData;

public class ThirdSalaryFragment extends Fragment implements View.OnClickListener {

    private AppCompatImageView addItem;
    private SQLiteDatabase sqLiteDatabase;
    private MoneyDataBase dbHelper;
    private AppCompatTextView showLastYearMoney, showCurrentYearMoney, showLastWeek, showCurrentWeek;
    private static float lastYearMoney, currentYearMoney, currentWeekMoney , lastWeekMoney;
    private Handler handler;
    private android.support.v7.widget.Toolbar initPayFor;
    private TableLayout tableLayout;
    private AppCompatTextView showAllCost;
    private static float allGet = 0.0f;
    private long CYAmount;

    private int CurrenMonth, LastMonth;
    private List mWeekList, mLastList;
    private static String CurrentWeekOne, CurrentWeekSeven;

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_payfor, container, false);

        CYAmount = System.currentTimeMillis()/365/24/60/60/1000 + 1970;

        CurrenMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
        LastMonth = CurrenMonth - 1;

        mWeekList = new ArrayList<>();
        mLastList = new ArrayList<>();

        mWeekList = getWeekDayList(formatDate(System.currentTimeMillis(), "yyyy-MM-dd EEEE"), "yyyy-MM-dd");

        mLastList = getWeekDayList(formatDate(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000, "yyyy-MM-dd EEEE"), "yyyy-MM-dd");

        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(this);
        dbHelper = new MoneyDataBase(getActivity());
        tableLayout = view.findViewById(R.id.tableLayout);
        showAllCost = view.findViewById(R.id.showAllCost);
        showLastYearMoney = view.findViewById(R.id.showLastYearMoney);
        showCurrentYearMoney = view.findViewById(R.id.showCurrentYearMoney);
        showLastWeek = view.findViewById(R.id.showLastWeek);
        showCurrentWeek = view.findViewById(R.id.showCurrentWeek);

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
                        showAllCost.setText(allGet * 16 + "");
                        showLastYearMoney.setText(lastYearMoney * 16 + "");
                        showCurrentYearMoney.setText(currentYearMoney * 16+"");

                        showLastWeek.setText(lastWeekMoney * 16 + "");
                        showCurrentWeek.setText(currentWeekMoney * 16 + "");
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


    public static List<Long> getWeekDayList(String date, String formatSrt) {
        // 存放每一天时间的集合
        List<Long> weekMillisList = new ArrayList<Long>();
        long dateMill = 0;
        try {
            // 获取date的毫秒值
            dateMill = getMillis(date, formatSrt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMill);
        // 本周的第几天
        int weekNumber = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println("wNun:"+weekNumber);
        // 获取本周一的毫秒值
        long mondayMill = dateMill - 86400000 * (weekNumber - 2);

        for (int i = 0; i < 7; i++) {
            weekMillisList.add(mondayMill + 86400000 * i);
        }
        return weekMillisList;
    }

    public static long getMillis(String time, String formatSrt) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(formatSrt);
        return format.parse(time).getTime();
    }

    public static String formatDate(Long date, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    private void setAllCost() {
        allGet = 0;
        lastYearMoney = 0;
        currentYearMoney = 0;
        currentWeekMoney = 0;
        lastWeekMoney = 0;

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

        Cursor cursor1 = null;
        if (LastMonth < 10)
        {
             cursor1 = sqLiteDatabase.rawQuery("select sum(amount) as lastyearmoney from payfor where date like  ?", new String[]{CYAmount + "-0" + LastMonth + "%"});
        }
        else if (LastMonth >= 10)
        {
             cursor1 = sqLiteDatabase.rawQuery("select sum(amount) as lastyearmoney from payfor where date like  ?", new String[]{CYAmount + "-" + LastMonth + "%"});
        }
        if (cursor1.moveToFirst())
        {
            do {
                lastYearMoney = cursor1.getFloat(cursor1.getColumnIndex("lastyearmoney"));
            }
            while (cursor1.moveToNext());
        }
        cursor1.close();

        Cursor cursor2 = null;
        if (CurrenMonth < 10)
        {
            cursor2 = sqLiteDatabase.rawQuery("select sum(amount) as currentyear from payfor where date like  ?", new String[]{CYAmount + "-0" + CurrenMonth + "%"});
        }
        else if (CurrenMonth > 10)
        {
            cursor2 = sqLiteDatabase.rawQuery("select sum(amount) as currentyear from payfor where date like  ?", new String[]{CYAmount + "-" + CurrenMonth + "%"});
        }
        if (cursor2.moveToFirst())
        {
            do {
                currentYearMoney = cursor2.getFloat(cursor2.getColumnIndex("currentyear"));
            }
            while (cursor2.moveToNext());
        }
        cursor2.close();

        Cursor cursor3 = sqLiteDatabase.rawQuery("select sum(amount) as currentweek from payfor where " +
                "date =  ? or " +
                "date = ? or " +
                "date = ? or " +
                "date = ? or " +
                "date = ? or " +
                "date = ? or " +
                "date = ?",
                new String[]
                        {       formatDate((Long) mWeekList.get(0), "yyyy-MM-dd"),
                                formatDate((Long) mWeekList.get(1), "yyyy-MM-dd"),
                                formatDate((Long) mWeekList.get(2), "yyyy-MM-dd"),
                                formatDate((Long) mWeekList.get(3), "yyyy-MM-dd"),
                                formatDate((Long) mWeekList.get(4), "yyyy-MM-dd"),
                                formatDate((Long) mWeekList.get(5), "yyyy-MM-dd"),
                                formatDate((Long) mWeekList.get(6), "yyyy-MM-dd")});

        if (cursor3.moveToFirst())
        {
            do {
                currentWeekMoney  = cursor3.getFloat(cursor3.getColumnIndex("currentweek"));

            }
            while (cursor3.moveToNext());
        }
        cursor3.close();

          Cursor cursor4 = sqLiteDatabase.rawQuery("select sum(amount) as lastweek from payfor where " +
                        "date =  ? or " +
                        "date = ? or " +
                        "date = ? or " +
                        "date = ? or " +
                        "date = ? or " +
                        "date = ? or " +
                        "date = ?",
                new String[]
                        {       formatDate((Long) mLastList.get(0), "yyyy-MM-dd"),
                                formatDate((Long) mLastList.get(1), "yyyy-MM-dd"),
                                formatDate((Long) mLastList.get(2), "yyyy-MM-dd"),
                                formatDate((Long) mLastList.get(3), "yyyy-MM-dd"),
                                formatDate((Long) mLastList.get(4), "yyyy-MM-dd"),
                                formatDate((Long) mLastList.get(5), "yyyy-MM-dd"),
                                formatDate((Long) mLastList.get(6), "yyyy-MM-dd")});

        if (cursor4.moveToFirst())
        {
            do {
               lastWeekMoney  = cursor4.getFloat(cursor4.getColumnIndex("lastweek"));
            }
            while (cursor4.moveToNext());
        }
        cursor4.close();

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
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-27", 3, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-27", 4, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-28", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-28", 4, "晚", 2);

            addDefaultData(contentValues, sqLiteDatabase,"2019-03-01", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-01", 4, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-02", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-02", 4, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-03", 0, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-03", 2, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-04", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-04", 6, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-05", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-05", 6, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-06", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-06", 4, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-07", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-07", 5, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-08", 0, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-08", 1, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-09", 2, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-09", 4, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-11", 3, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-11", 4, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-12", 4, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-12", 5, "晚", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-13", 6, "中", 2);
            addDefaultData(contentValues, sqLiteDatabase,"2019-03-13", 3, "晚", 2);


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

            Toast.makeText(getActivity(),"订餐数据库初始化完成！", Toast.LENGTH_LONG).show();
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

            } while (cursor.moveToNext());
        }
    }
}
