package dev.datvt.funnychat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.controls.RequestHandler;
import dev.datvt.funnychat.controls.TransparentProgressDialog;
import dev.datvt.funnychat.models.User;

/**
 * Created by datvt on 3/25/2016.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

    private static final String REGISTER_URL = "http://chatappphp-dq0812.rhcloud.com/pages/Register.php";

    private User user;

    private Typeface typeface;
    private EditText username, password, email;
    private Button register, move_login;
    private View toastView = null;
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        getForWidgets();
        addEvent();
    }

    private void getForWidgets() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/rix_love_fool.ttf");
        username = (EditText) findViewById(R.id.etUsername);
        password = (EditText) findViewById(R.id.etPassword);
        email = (EditText) findViewById(R.id.etEmail);
        register = (Button) findViewById(R.id.btnRegister);
        register.setTypeface(typeface);
        register.setTextSize(25);
        move_login = (Button) findViewById(R.id.btnMoveLogin);
        move_login.setTypeface(typeface);

        user = new User();
    }

    private void addEvent() {
        register.setOnClickListener(this);
        move_login.setOnClickListener(this);
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

    private void registerUser() {
        String name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String mail = email.getText().toString().trim();

        user.setName(name);
        user.setEmail(mail);
        user.setPassword(pass);

        if (!name.isEmpty() && !pass.isEmpty() && !mail.isEmpty()) {
            register(user);
            username.setText("");
            password.setText("");
            email.setText("");
        } else {
            showToast("Bạn chưa nhập đầy đủ thông tin");
        }
    }

    private void register(User user) {
        class RegisterUser extends AsyncTask<String, Void, String> {
            TransparentProgressDialog loading;
            RequestHandler connectServer = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new TransparentProgressDialog(RegisterActivity.this, R.drawable.icon_progress_bar);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String res = jsonObject.getString("Success");
                    int checkRegister;
                    if (!res.isEmpty()) {
                        checkRegister = Integer.parseInt(res);
                        if (checkRegister == 1) {
                            showToast("Đăng ký thành công");
                        } else {
                            showToast("Email đã đăng ký. Vui lòng chọn email khác");
                        }
                    } else {
                        showToast("Kết nối máy chủ lỗi");
                    }

                } catch (JSONException e) {
                    showToast("Kết nối máy chủ lỗi");
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String, String>();
                data.put("UserName", params[0]);
                data.put("UserMail", params[1]);
                data.put("UserPassword", params[2]);

                String result = connectServer.sendPostRequest(REGISTER_URL, data);

                return result;
            }
        }

        RegisterUser registerUser = new RegisterUser();
        registerUser.execute(user.getName(), user.getEmail(), user.getPassword());
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
            case R.id.btnRegister:
                if (hasConnection()) {
                    registerUser();
                } else {
                    showToast("Vui lòng kiểm tra kết nối internet");
                }
                break;
            case R.id.btnMoveLogin:
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (hasConnection()) {
                registerUser();
            } else {
                showToast("Vui lòng kiểm tra kết nối internet");
            }
            return true;
        }
        return super.dispatchKeyEvent(e);
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
