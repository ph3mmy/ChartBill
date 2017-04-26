package com.jcedar.paperbag.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.jcedar.paperbag.R;

/**
 * Created by OLUWAPHEMMY on 4/18/2017.
 */
public class MsgActivity extends AppCompatActivity {

    private static final String TAG = "MsgActivity";
    private TextView msgSender, msg;
    String msgStr, sender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Order");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            msgStr = getIntent().getExtras().getString("value");
            sender = getIntent().getExtras().getString("sender");
        }

        msgSender = (TextView) findViewById(R.id.tvOrderMsgSender);
        msg = (TextView) findViewById(R.id.tvOrderMsgBody);

        msgSender.setText(sender);
        msg.setText(msgStr);


    }
}
