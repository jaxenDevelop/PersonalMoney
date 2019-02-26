package com.example.personalmoney;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TableRow;
import android.widget.TextView;

import static android.view.Gravity.CENTER;

public class setTableRow {

    public TableRow setTableRow(String date, String amount, String other, Context context)
    {

        TextView timeText = new TextView(context);
        timeText.setText(date);
        timeText.setGravity(CENTER);
        timeText.setTextSize(22);


        TextView amountText = new TextView(context);
        amountText.setText(amount);
        amountText.setGravity(CENTER);
        amountText.setTextSize(22);

        TextView otherText = new TextView(context);
        other =  other == null ? "无":other;
        otherText.setText(other);
        otherText.setGravity(CENTER);
        otherText.setTextSize(22);

        TableRow tableRow = new TableRow(context);
        tableRow.addView(timeText);
        tableRow.addView(amountText);
        tableRow.addView(otherText);

        return tableRow;
    }
}
