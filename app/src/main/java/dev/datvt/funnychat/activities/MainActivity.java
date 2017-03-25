package dev.datvt.funnychat.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.adapters.StatusAdapter;
import dev.datvt.funnychat.models.User;
import dev.datvt.funnychat.services.MyService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigation;

    private TextView name, gender, birthday, address, music;
    private Button btnPost;
    private EditText etStatus;
    private Typeface typeface;
    private View toastView;
    private Toast toast;

    private User user;
    private ListView lvStatus;
    private ArrayList<String> status;
    private StatusAdapter statusAdapter;

    private MyService myService;
    private boolean isBound = false;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstances();
        getForWidgets();
        addEvents();
        getInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    private void initInstances() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,
                R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);

        navigation = (NavigationView) findViewById(R.id.navigation_view);
        navigation.setNavigationItemSelectedListener(this);
        navigation.setCheckedItem(R.id.nav_home);
    }

    private void getForWidgets() {
        name = (TextView) findViewById(R.id.name);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/rix_love_fool.ttf");
        name.setTypeface(typeface);
        gender = (TextView) findViewById(R.id.gender);
        gender.setTypeface(typeface);
        birthday = (TextView) findViewById(R.id.birthday);
        birthday.setTypeface(typeface);
        address = (TextView) findViewById(R.id.address);
        address.setTypeface(typeface);
        music = (TextView) findViewById(R.id.tvMusic);
        music.setTypeface(typeface);
        music.setSelected(true);

        btnPost = (Button) findViewById(R.id.btnPost);
        etStatus = (EditText) findViewById(R.id.etStatus);

        lvStatus = (ListView) findViewById(R.id.lvStatus);
        status = new ArrayList<String>();
        statusAdapter = new StatusAdapter(this, status);
        lvStatus.setAdapter(statusAdapter);
    }

    private void addEvents() {
        btnPost.setOnClickListener(this);

        connection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyService.MyBinder binder = (MyService.MyBinder) service;
                myService = binder.getService();
                isBound = true;
            }
        };

        Intent intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        music.setText(new StringBuilder().append(" See You Again -  Wiz Khalifa & Charlie Puth .........\n")
                .append("It's been a long day without you my friend\n")
                .append(" And I'll tell you all about it when I see you again\n")
                .append(" We've come a long way from where we began\n")
                .append("Oh I'll tell you all about it when I see you again\n")
                .append(" When I see you again\n")
                .append("\n Damn who knew all the planes we flew\n")
                .append(" Good things we've been through\n")
                .append("That I'll be standing right here\n")
                .append("Talking to you about another path I\n")
                .append("Know we loved to hit the road and laugh\n")
                .append(" But something told me that it wouldn't last\n")
                .append("Had to switch up look at things different see the bigger picture\n")
                .append(" Those were the days hard work forever pays now I see you in a better place\n")
                .append(" How could we not talk about family when family's all that we got?\n")
                .append(" Everything I went through you were standing there by my side\n")
                .append(" And now you gonna be with me for the last ride\n")
                .append(" It's been a long day without you my friend\n")
                .append(" And I'll tell you all about it when I see you again\n")
                .append(" We've come a long way from where we began\n")
                .append(" Oh I'll tell you all about it when I see you again\n")
                .append(" when I see you again\n")
                .append(" First you both go out your way\n")
                .append(" And the vibe is feeling strong and what's\n")
                .append(" Small turn to a friendship a friendship\n")
                .append(" Turn into a bond and that bond will never\n")
                .append(" Be broke and the love will never get lost\n")
                .append(" And when brotherhood come first then the line \n")
                .append(" Will never be crossed established it on our own\n")
                .append(" When that line had to be drawn and that line is what\n")
                .append(" We reach so remember me when I'm gone\n")
                .append(" How could we not talk about family when family's all that we got?\n")
                .append(" Everything I went through you were standing there by my side\n")
                .append("And now you gonna be with me for the last ride\n")
                .append(" So let the light guide your way hold every memory\n")
                .append(" As you go and every road you take will always lead you home\n")
                .append(" It's been a long day without you my friend\n")
                .append(" And I'll tell you all about it when I see you again\n")
                .append(" We've come a long way from where we began\n")
                .append(" Oh I'll tell you all about it when I see you again\n")
                .append(" When I see you again").toString());
    }

    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    private void getInfo() {
        Intent i = getIntent();
        Bundle b = i.getExtras();
        user = (User) b.getSerializable("user");
        name.setText(user.getName());

        initFakeStatus();
    }

    private void initFakeStatus() {
        status.clear();
        status.add("Hôm nay mệt vãi ra..:'( ");
        status.add("Đệt mai lại đi học rồi (^^)");
        status.add("Trời nắng đẹp... Go..:))");
        status.add("Lần đầu làm chuyện ấy :3.");
        status.add("Fighting !!");
        status.add("Team vip...:v");
        status.add("Sapa ngày nắng đẹp...<3");
        status.add("Có gấu rồi..:)). La la la.....Hehehe...Yolo...................................." +
                "Mai đi chơi đê ae ^^" + "................Ai muốn đi nào comment..:3");
        statusAdapter.notifyDataSetChanged();
    }

    private void searchUser() {
        Intent itent_search = new Intent(MainActivity.this, SearchActivity.class);
        Bundle bundle_search = new Bundle();
        bundle_search.putSerializable("account", user);
        itent_search.putExtras(bundle_search);
        startActivity(itent_search);
    }

    private void viewFriendList() {
        Intent intent_view = new Intent(MainActivity.this, FriendsActivity.class);
        Bundle bundle_view = new Bundle();
        bundle_view.putSerializable("account", user);
        intent_view.putExtras(bundle_view);
        startActivity(intent_view);
    }

    private void logout() {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        aBuilder.setMessage("Bạn có muốn đăng xuất không?");
        aBuilder.setCancelable(false);
        aBuilder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aBuilder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        aBuilder.create().show();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (hasConnection()) {
            switch (id) {
                case R.id.nav_home:
                    item.setChecked(true);
                    break;
                case R.id.nav_friends:
                    item.setChecked(true);
                    viewFriendList();
                    break;
                case R.id.nav_add_friends:
                    item.setChecked(true);
                    searchUser();
                    break;
                case R.id.nav_exit:
                    item.setChecked(true);
                    logout();
                    break;
                case R.id.nav_author:
                    item.setChecked(true);
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    break;
            }
        } else {
            showToast("Vui lòng kiểm tra kết nối internet");
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendStatus() {
        if (!etStatus.getText().toString().isEmpty()) {
            status.add(0, etStatus.getText().toString());
            statusAdapter.notifyDataSetChanged();
        } else {
            showToast("Bạn chưa nhập ý tưởng của mình");

        }
        etStatus.setText("");
        etStatus.requestFocus();
    }

    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            sendStatus();
            return true;
        }
        return super.dispatchKeyEvent(e);
    }

    @Override
    public void onClick(View v) {
        if (v == btnPost) {
            sendStatus();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            final AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
            aBuilder.setMessage("Bạn có muốn thoát ứng dụng không?");
            aBuilder.setCancelable(false);
            aBuilder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            aBuilder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            aBuilder.create().show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
