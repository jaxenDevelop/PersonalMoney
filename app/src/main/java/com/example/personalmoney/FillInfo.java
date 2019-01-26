package com.example.personalmoney;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;


public class FillInfo extends AppCompatActivity implements View.OnClickListener {

    private EditText train_online, train_offline, other;
    private Intent intent;
    private String TITLE;
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

        /**控件初始化**/
        train_online = findViewById(R.id.train_online);
        train_offline = findViewById(R.id.train_offline);
        train_online.setOnClickListener(this);
        train_offline.setOnClickListener(this);

        ok = findViewById(R.id.ok);
        ok.setOnClickListener(this);
        intent = new Intent();
        train_online = findViewById(R.id.train_online);
        train_offline = findViewById(R.id.train_offline);
        other = findViewById(R.id.other);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ok:
                intent.putExtra("TIME", train_online.getText().toString());
                intent.putExtra("AMOUNT", train_offline.getText().toString());
                intent.putExtra("OTHER", other.getText().toString());
                setResult(1, intent);
                finish();
                break;

        }
    }
}