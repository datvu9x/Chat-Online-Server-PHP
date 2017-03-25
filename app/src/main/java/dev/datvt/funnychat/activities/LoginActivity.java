package dev.datvt.funnychat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.controls.RequestHandler;
import dev.datvt.funnychat.controls.TransparentProgressDialog;
import dev.datvt.funnychat.models.User;

/**
 * Created by datvt on 4/11/2016.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String LOGIN_URL = "http://chatappphp-dq0812.rhcloud.com/pages/Login.php";

    private Button login, move_register;
    private EditText email, password;
    private TextView title, tvCheck;
    private CheckBox remember;
    private Typeface typeface;
    private View toastView = null;
    private Toast toast = null;
    private TransparentProgressDialog progressDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        getForWidgets();
        addEvents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreInfo();
    }

    private void getForWidgets() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/fiolex_girl.ttf");
        title = (TextView) findViewById(R.id.tvTitle);
        title.setTypeface(typeface);
        title.setTextSize(50);

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/rix_love_fool.ttf");
        login = (Button) findViewById(R.id.btnLogin);
        login.setTypeface(typeface);
        login.setTextSize(25);

        remember = (CheckBox) findViewById(R.id.cbRememberMe);
        tvCheck = (TextView) findViewById(R.id.tvCheckbox);
        tvCheck.setTypeface(typeface);

        move_register = (Button) findViewById(R.id.btnMoveRegister);
        move_register.setTypeface(typeface);
    }

    private void addEvents() {
        login.setOnClickListener(this);
        move_register.setOnClickListener(this);
    }

    private void saveInfo() {
        SharedPreferences pre = getSharedPreferences
                ("my_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        String user = email.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        boolean check = remember.isChecked();
        if (!check) {
            editor.clear();
        } else {
            editor.putString("user", user);
            editor.putString("pwd", pwd);
            editor.putBoolean("checked", check);
        }
        editor.commit();
    }

    private void restoreInfo() {
        SharedPreferences pre = getSharedPreferences
                ("my_data", MODE_PRIVATE);
        boolean check = pre.getBoolean("checked", false);
        if (check) {
            String user = pre.getString("user", "");
            String pwd = pre.getString("pwd", "");
            email.setText(user);
            password.setText(pwd);
        }
        remember.setChecked(check);
    }

    private void loginUser() {
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (!mail.isEmpty() && !pass.isEmpty()) {
            login(mail, pass);
        } else {
            showToast("Bạn chưa nhập đầy đủ thông tin");
        }

    }

    private void login(String mail, String pass) {
        class LoginUser extends AsyncTask<String, Void, String> {
            RequestHandler connectServer = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new TransparentProgressDialog(LoginActivity.this, R.drawable.icon_progress_bar);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String res = jsonObject.getString("Success");
                    int checkLogin;
                    if (!res.isEmpty()) {
                        checkLogin = Integer.parseInt(res);
                        if (checkLogin == 1) {
                            user = new User();
                            user.setId(jsonObject.getString("UserId"));
                            user.setName(jsonObject.getString("UserName"));
                            user.setEmail(jsonObject.getString("UserMail"));
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Email hoặc mật khẩu không đúng. Xin kiểm tra lại");
                            progressDialog.dismiss();
                        }
                    } else {
                        showToast("Kết nối máy chủ lỗi");
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    showToast("Không thể lấy dữ liệu từ server");
                }
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("UserMailLogin", params[0]);
                data.put("UserPasswordLogin", params[1]);
                String result = connectServer.sendPostRequest(LOGIN_URL, data);
                return result;
            }
        }

        LoginUser loginUser = new LoginUser();
        loginUser.execute(mail, pass);
    }

    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnLogin:
                if (hasConnection()) {
                    loginUser();
                } else  {
                    showToast("Vui lòng kiểm tra kết nối internet");
                }
                break;
            case R.id.btnMoveRegister:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (hasConnection()) {
                loginUser();
            } else  {
                showToast("Vui lòng kiểm tra kết nối internet");
            }
            return true;
        }
        return super.dispatchKeyEvent(e);
    }

}
