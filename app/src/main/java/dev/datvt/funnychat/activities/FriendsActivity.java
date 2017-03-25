package dev.datvt.funnychat.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.controls.TransparentProgressDialog;
import dev.datvt.funnychat.models.User;
import dev.datvt.funnychat.controls.RequestHandler;
import dev.datvt.funnychat.adapters.UserAdapter;

/**
 * Created by datvt on 4/21/2016.
 */
public class FriendsActivity extends Activity implements View.OnClickListener {

    private static final String FRIENDS_URL = "http://chatappphp-dq0812.rhcloud.com/pages/GetFriend.php";

    private Typeface typeface;
    private TextView title;
    private ListView listView;
    private Button btnExit;
    private View toastView;
    private Toast toast;

    private User user, me, friend;
    private ArrayList<User> userArrayList;
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);

        getForWidgets();
        addEvents();
    }

    private void getForWidgets() {
        title = (TextView) findViewById(R.id.title_friends);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/rix_love_fool.ttf");
        title.setTypeface(typeface);

        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setTypeface(typeface);

        listView = (ListView) findViewById(R.id.lvFriends);
        userArrayList = new ArrayList<User>();
        userAdapter = new UserAdapter(this, userArrayList);
        listView.setAdapter(userAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        me = (User) bundle.getSerializable("account");
        if (hasConnection()) {
            getFriends(Integer.parseInt(me.getId()));
        } else {
            showToast("Vui lòng kiểm tra kết nối internet");
        }
    }

    private void addEvents() {
        btnExit.setOnClickListener(this);
        registerForContextMenu(listView);
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

    private void getListFriend(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String res = jsonObject.getString("Result");
            int number;
            JSONObject object;
            if (!res.isEmpty()) {
                number = Integer.parseInt(res);
                if (number == 0) {
                    showToast("Bạn chưa kết bạn với ai");
                } else {
                    userArrayList.clear();
                    for (int i = 0; i < number; i++) {
                        object = jsonObject.getJSONObject(i + "");
                        user = new User();
                        user.setId(object.getString("UserId"));
                        user.setName(object.getString("UserName"));
                        user.setEmail(object.getString("UserMail"));
                        userArrayList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            } else {
                showToast("Kết nối đến máy chủ lỗi");
            }
        } catch (JSONException e) {
            showToast("Kết nối đến máy chủ lỗi");
        }

    }

    private void getFriends(int user_id) {
        class Friends extends AsyncTask<Integer, Void, String> {
            TransparentProgressDialog loading;
            RequestHandler connectServer = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new TransparentProgressDialog(FriendsActivity.this, R.drawable.icon_progress_bar);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                getListFriend(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Integer... params) {
                HashMap<String, Integer> data = new HashMap<String, Integer>();
                data.put("userid", params[0]);

                String result = connectServer.sendPostRequestInt(FRIENDS_URL, data);

                return result;
            }
        }

        Friends friends = new Friends();
        friends.execute(user_id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Thao tác");
        menu.setHeaderIcon(R.drawable.ic_setting);
        getMenuInflater().inflate(R.menu.menu_friend, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = menuInfo.position;
        friend = (User) userAdapter.getItem(pos);

        switch (item.getItemId()) {
            case R.id.iChat:
                chatMsg();
                break;
            case R.id.iInfo:
                viewInfo();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void chatMsg() {
        Intent intent1 = new Intent(FriendsActivity.this, ChatPersonActivity.class);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("info_friend", friend);
        bundle1.putSerializable("info_me", me);
        intent1.putExtras(bundle1);
        startActivity(intent1);
        finish();
    }

    private void viewInfo() {
        Intent intent = new Intent(FriendsActivity.this, InfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", friend);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnExit) {
            finish();
        }
    }
}
