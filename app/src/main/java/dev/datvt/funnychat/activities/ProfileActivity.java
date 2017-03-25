package dev.datvt.funnychat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import dev.datvt.funnychat.R;


/**
 * Created by datvt on 5/7/2016.
 */
public class ProfileActivity extends Activity implements View.OnClickListener {

    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnClose) {
            finish();
        }
    }
}
