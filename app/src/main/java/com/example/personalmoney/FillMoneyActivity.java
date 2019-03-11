package com.example.personalmoney;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class FillMoneyActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText train_online, train_offline, other;
    private RadioGroup choose;
    private RadioButton mf, zx;
    private Intent intent;
    private AppCompatButton ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(100, 0, 100, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        /**设置点击周围Activity消失**/
        setFinishOnTouchOutside(true);

        setContentView(R.layout.layout_train_fillinfo);
        intent = new Intent();
        choose = findViewById(R.id.choose);

        Intent intent1 = getIntent();
//        if (intent1.getIntExtra("from" , 0) == 1)
//            flag = 0;
//        else if (intent1.getIntExtra("from" , 0) == 2)
//            flag = 1;

//        choose.setOnCheckedChangeListener(this);

        /**控件初始化**/
        train_online = findViewById(R.id.train_online);
        train_offline = findViewById(R.id.train_offline);
        train_online.setOnClickListener(this);
        train_offline.setOnClickListener(this);

        ok = findViewById(R.id.ok);
        ok.setOnClickListener(this);

        train_online = findViewById(R.id.train_online);
        train_offline = findViewById(R.id.train_offline);
        other = findViewById(R.id.other);

        mf = findViewById(R.id.mf);
        zx = findViewById(R.id.zx);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ok:
                if (train_online.getText().toString().equals("") || train_offline.getText().toString().equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("日期和金额不能为空").setPositiveButton("ok", null).show();
                }
                else {
                    intent.putExtra("TIME", train_online.getText().toString());
                    intent.putExtra("AMOUNT", train_offline.getText().toString());
                    intent.putExtra("OTHER", other.getText().toString());

                    setResult(1, intent);
                    finish();
                }

                break;

        }
    }
}