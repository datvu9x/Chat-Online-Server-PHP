package dev.datvt.funnychat.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.models.User;

/**
 * Created by datvt on 4/21/2016.
 */
public class InfoActivity extends Activity {

    private Typeface typeface;
    private TextView tvName, tvGender, tvBirthday, tvAddress;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        tvName = (TextView) findViewById(R.id.name_user);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/tabitha.ttf");
        tvName.setTypeface(typeface);
        tvGender = (TextView) findViewById(R.id.gender_user);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/rix_love_fool.ttf");
        tvGender.setTypeface(typeface);
        tvBirthday = (TextView) findViewById(R.id.birthday_user);
        tvBirthday.setTypeface(typeface);
        tvAddress = (TextView) findViewById(R.id.address_user);
        tvAddress.setTypeface(typeface);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("info");
        tvName.setText(user.getName());
    }
}
