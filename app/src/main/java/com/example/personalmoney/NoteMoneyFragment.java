package com.example.personalmoney;
/**统计购房花费金额
 * 包含父母装入与个人存款**/
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.Gravity.CENTER;

public class NoteMoneyFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private AppCompatImageView addItem;
    private TableLayout tableLayout;
    private SharedPreferences sharedPreferences;
    private Handler handler;
    private MoneyDataBase dbHelper;
    private android.support.v7.widget.Toolbar init;
    private SQLiteDatabase sqLiteDatabase;

    private AppCompatTextView showCurrentMoney, showParentMoney, showMyMoney;
    private static float parentMoney = 0;
    private static float myMoney = 0.0f;
    private static float currentMoney = 0.0f;
    private static float newAddMoney = 0;

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_notemoney, container, false);

        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(this);

        tableLayout = view.findViewById(R.id.tableLayout);
        init = view.findViewById(R.id.init);
        showCurrentMoney = view.findViewById(R.id.showCurrentMoney);
        showParentMoney = view.findViewById(R.id.showParentMoney);
        showMyMoney = view.findViewById(R.id.showMyMoney);

        dbHelper = new MoneyDataBase(getActivity());

        sharedPreferences = getActivity().getSharedPreferences("MyCurrentAllMoney", Context.MODE_PRIVATE);
        currentMoney = sharedPreferences.getFloat("account", 0.0f);
        showCurrentMoney.setOnClickListener(this);
        showCurrentMoney.setText(currentMoney+"");

        /**handler逻辑**/
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

                        currentMoney += newAddMoney;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("account", currentMoney);
                        editor.apply();

                        showCurrentMoney.setText(currentMoney+"");

                        myMoney = currentMoney - parentMoney;
                        showMyMoney.setText(myMoney+"");
                        break;

                    case 1:
                        currentMoney = Float.parseFloat(msg.obj.toString());
                        showCurrentMoney.setText(String.valueOf(currentMoney));
                        myMoney = Float.parseFloat(msg.obj.toString()) - parentMoney;
                        showMyMoney.setText(myMoney+"");
                        break;
                }

            }
        };

        initDataBaseData();
        initTableRow();

        showCurrentMoney.setOnClickListener(this);
        float initMyMoney = currentMoney - parentMoney;
        showMyMoney.setText(initMyMoney+"");

        init.setOnLongClickListener(this);

        return view;

    }

    public static void addDefaultData(ContentValues cs, SQLiteDatabase sd, String date, int amount, String other, int code)
    {
        cs.clear();
        cs.put("date", date);
        cs.put("amount", amount);
        cs.put("other", other);
        if (code == 0)
        sd.insert("mymoney", null, cs);
        else if (code == 2)
            sd.insert("payfor", null, cs);
    }

    private void initDataBaseData() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("mymoney", null, null, null, null, null, null);
        ContentValues contentValues = new ContentValues();
        if (!cursor.moveToFirst())
        {
            addDefaultData(contentValues, sqLiteDatabase,"2018-02-27", 125100, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-03-18", -5000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-03-26", -8000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-04-09", -2000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-04-11", -4000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-07-20", 60000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-08-07", -10000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-08-08", -20000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-08-09", -30000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2018-10-17", 4935, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-01", 140000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-02", -50000, "保证金", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-07", -2000, "担保费", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-08", 195000, "妈妈转入", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-09", 300000, "姐姐转入", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-09", 400000, "爸爸转入", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-11", -700000, "购房首付款", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-22", -5000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-01-23", -10000, "无", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-03", 40000, "爸爸转入", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-09", 115000, "农商行转入", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-11", 102107, "邮政储蓄转入", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-22", -530000, "首付款第二批", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-22", -10000, "中介费", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-22", -97413, "契税增值税", 0);
            addDefaultData(contentValues, sqLiteDatabase,"2019-02-22", -2500, "打点费", 0);

            cursor.close();

            Toast.makeText(getActivity(),"购房统计数据库初始化完成！", Toast.LENGTH_LONG).show();
        }
    }

    private void initTableRow() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("mymoney", null, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String amount = cursor.getString(cursor.getColumnIndex("amount"));
                String other = cursor.getString(cursor.getColumnIndex("other"));

                Message message = Message.obtain();
                message.what = 0;
                message.obj = new setTableRow().setTableRow(date, amount, other, getActivity());
                handler.sendMessage(message);

                parentMoney += Float.parseFloat(amount);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addItem:
                Intent intent = new Intent(getActivity(), FillMoneyActivity.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.showCurrentMoney:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                final EditText jdText = new EditText(getActivity());
                final EditText bankText = new EditText(getActivity());
                final TextView addText = new TextView(getActivity());
                jdText.setHint("理财产品总金额");
                jdText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                bankText.setHint("银行卡总金额");
                bankText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                addText.setText("+");
                addText.setTextSize(22);
                addText.setTextColor(Color.parseColor("#000000"));
                addText.setGravity(CENTER);
                linearLayout.addView(jdText);
                linearLayout.addView(addText);
                linearLayout.addView(bankText);
                builder.setTitle("设置当前账户总金额").setView(linearLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            final float AllMoney = Float.parseFloat(jdText.getText().toString()) + Float.parseFloat(bankText.getText().toString());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putFloat("account", AllMoney);
                            editor.commit();
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    Message message = Message.obtain();
                                    message.what = 1;
                                    message.obj = AllMoney;
                                    handler.sendMessage(message);
                                }
                            }.start();
                        }
                        catch (NumberFormatException e)
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                            alertDialog.setMessage("输入不能为空！").show();
                        }


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

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("date", time);
                        contentValues.put("amount", amount);
                        contentValues.put("other", other);
                        sqLiteDatabase.insert("mymoney", null, contentValues);

                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = new setTableRow().setTableRow(time, amount, other, getActivity());

                        parentMoney += Float.parseFloat(amount);
                        newAddMoney = Float.parseFloat(amount);

                        handler.sendMessage(message);

                    }
                }.start();
                break;

        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId())
        {
            case R.id.init:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("是否重置数据库？")
                        .setMessage("当前页面数据将恢复初始状态！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sqLiteDatabase = dbHelper.getWritableDatabase();
                                sqLiteDatabase.delete("mymoney", null,null);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putFloat("account", 0);
                                editor.commit();

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
                break;
        }
        return false;
    }
}