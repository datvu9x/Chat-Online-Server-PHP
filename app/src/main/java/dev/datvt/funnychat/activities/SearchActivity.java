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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.adapters.UserAdapter;
import dev.datvt.funnychat.controls.RequestHandler;
import dev.datvt.funnychat.controls.TransparentProgressDialog;
import dev.datvt.funnychat.models.User;

/**
 * Created by datvt on 4/5/2016.
 */
public class SearchActivity extends Activity implements View.OnClickListener {

    private static final String SEARCH_URL = "http://chatappphp-dq0812.rhcloud.com/pages/Search.php";
    private static final String ADD_FRIEND_URL = "http://chatappphp-dq0812.rhcloud.com/pages/Add.php";

    private ImageView logout, ivSearch;
    private TextView tvMemberList;
    private EditText etSearch;
    private ListView lv_users;
    private Typeface typeface;
    private View toastView;
    private Toast toast;

    private User user, me, friend;
    private ArrayList<User> userArrayList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user_activity);

        getForWidgets();
        addEvents();
    }

    void getForWidgets() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/rix_love_fool.ttf");
        tvMemberList = (TextView) findViewById(R.id.tvTitle_memberList);
        tvMemberList.setTypeface(typeface);

        etSearch = (EditText) findViewById(R.id.etSearch);

        logout = (ImageView) findViewById(R.id.ivback);
        ivSearch = (ImageView) findViewById(R.id.btnSearch);

        lv_users = (ListView) findViewById(R.id.lvUser);
        userArrayList = new ArrayList<User>();
        userAdapter = new UserAdapter(this, userArrayList);
        lv_users.setAdapter(userAdapter);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        me = (User) b.getSerializable("account");
    }

    private void addEvents() {
        logout.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        registerForContextMenu(lv_users);

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

    private void searchUser() {
        String keyword = etSearch.getText().toString().trim();
        search(keyword);
        etSearch.setText("");
    }

    private void getInfoUser(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String res = jsonObject.getString("Result");
            int number;
            JSONObject object;
            if (!res.isEmpty()) {
                number = Integer.parseInt(res);
                if (number == 0) {
                    showToast("Không tìm thấy thành viên này");
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
                showToast("Lỗi kết nối máy chủ");
            }
        } catch (JSONException e) {
            showToast("Lỗi kết nối máy chủ");
        }

    }

    private void search(String keyword) {
        class SearchUser extends AsyncTask<String, Void, String> {
           TransparentProgressDialog loading;
            RequestHandler connectServer = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new TransparentProgressDialog(SearchActivity.this, R.drawable.icon_progress_bar);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                getInfoUser(s);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("search", params[0]);
                String result = connectServer.sendPostRequest(SEARCH_URL, data);

                return result;
            }
        }

        SearchUser loginUser = new SearchUser();
        loginUser.execute(keyword);
    }

    private void addFriend(int id_user, int id_friend) {
        class AddFriend extends AsyncTask<Integer, Void, String> {
            TransparentProgressDialog loading;
            RequestHandler connectServer = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new TransparentProgressDialog(SearchActivity.this, R.drawable.icon_progress_bar);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s == null) {
                    showToast("Bạn không thể tự thêm chính mình");
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String res = jsonObject.getString("result");
                        if (!res.isEmpty()) {
                            if (Integer.parseInt(res) == 0) {
                                showToast("Thêm bạn bè thành công");
                            } else if (Integer.parseInt(res) == 2) {
                                showToast("Bạn bè đã tồn tại");
                            }
                        } else {
                            showToast("Thêm bạn bè thất bại");
                        }
                    } catch (JSONException e) {
                        showToast("Thêm bạn bè thất bại");
                    }
                }
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Integer... params) {
                HashMap<String, Integer> data = new HashMap<String, Integer>();
                data.put("userid", params[0]);
                data.put("friendid", params[1]);

                String result = connectServer.sendPostRequestInt(ADD_FRIEND_URL, data);

                return result;
            }
        }

        AddFriend addFriend = new AddFriend();
        addFriend.execute(id_user, id_friend);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Setting");
        menu.setHeaderIcon(R.drawable.ic_setting);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = menuInfo.position;
        friend = (User) userAdapter.getItem(pos);

        if (hasConnection()) {
            switch (item.getItemId()) {
                case R.id.chat:
                    chatMsg();
                    break;
                case R.id.add:
                    addFriend(Integer.parseInt(me.getId()), Integer.parseInt(friend.getId()));
                    break;
                case R.id.information:
                    viewInformation();
                    break;
            }
        } else {
            showToast("Vui lòng kiểm tra kết nối internet");
        }
        return super.onContextItemSelected(item);
    }

    private void chatMsg() {
        Intent intent1 = new Intent(SearchActivity.this, ChatPersonActivity.class);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("info_friend", friend);
        bundle1.putSerializable("info_me", me);
        intent1.putExtras(bundle1);
        startActivity(intent1);
        finish();
    }

    private void viewInformation() {
        Intent intent = new Intent(SearchActivity.this, InfoActivity.class);
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
        int id = v.getId();
        switch (id) {
            case R.id.ivback:
                finish();
                break;
            case R.id.btnSearch:
                if (hasConnection()) {
                    searchUser();
                } else {
                    showToast("Vui lòng kiểm tra kết nối internet");
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (hasConnection()) {
                searchUser();
            } else {
                showToast("Vui lòng kiểm tra kết nối internet");
            }
            return true;
        }
        return super.dispatchKeyEvent(e);
    }
}
