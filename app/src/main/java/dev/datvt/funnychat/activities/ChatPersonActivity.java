package dev.datvt.funnychat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.adapters.ChatPersonAdapter;
import dev.datvt.funnychat.controls.EmojiHandler;
import dev.datvt.funnychat.controls.RequestHandler;
import dev.datvt.funnychat.controls.TransparentProgressDialog;
import dev.datvt.funnychat.models.Message;
import dev.datvt.funnychat.models.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by datvt on 4/8/2016.
 */
public class ChatPersonActivity extends Activity implements View.OnClickListener {

    private static final String ID_ROOM_URL = "http://chatappphp-dq0812.rhcloud.com/pages/GetRoomId.php";
    private static final String TYPE_MESSAGE = "message";
    private static final String TYPE_IMAGE = "img";
    private static final int REQUEST_CODE = 100;

    private EditText etMessage;
    private ImageView ivSend, ivBack, ivReload, ivSelectImage;
    private TextView tvAccount;
    private ListView lvMessage;
    private View toastView;
    private Toast toast;

    private String urlImage;
    private User me, friend;
    private int id_room;
    private boolean checkSendImg = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private Bitmap bitmap;
    private EmojiHandler emojiHandler;

    private ArrayList<Message> messageArrayList;
    private ChatPersonAdapter chatPersonAdapter;
    private Message message;

    private Socket mSocket;
    /**
     * Listen event server emit
     */
    private Emitter.Listener onResponseServer = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String s = args[0].toString();
                    Log.i("JSON", s);
                    if (!s.isEmpty()) {
                        getMessageFromServer(s);
                    } else {
                        showToast("Không nhận được tin nhắn từ máy chủ");
                    }
                }
            });
        }
    };

    /**
     * Connection socket to server
     */ {
        try {
            mSocket = IO.socket("http://chatapp-dq0812.rhcloud.com:8000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_person_activity);

        mSocket.connect();
        mSocket.on("output", onResponseServer);

        getForWidgets();
        addEvents();
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
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("output", onResponseServer);
    }

    /**
     * Get id control
     */
    private void getForWidgets() {
        etMessage = (EditText) findViewById(R.id.etMessage);
        ivSend = (ImageView) findViewById(R.id.ivSend);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivReload = (ImageView) findViewById(R.id.ivReload);
        ivSelectImage = (ImageView) findViewById(R.id.ivSelectImage);
        tvAccount = (TextView) findViewById(R.id.tvAccount);
        lvMessage = (ListView) findViewById(R.id.lvChat);

        etMessage.setEnabled(false);
        ivSend.setEnabled(false);
        ivSelectImage.setEnabled(false);

        emojiHandler = new EmojiHandler();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        me = (User) bundle.getSerializable("info_me");
        friend = (User) bundle.getSerializable("info_friend");

        tvAccount.setText(friend.getName());

        messageArrayList = new ArrayList<Message>();
        chatPersonAdapter = new ChatPersonAdapter(this, messageArrayList);
        lvMessage.setAdapter(chatPersonAdapter);
    }

    /**
     * Add Event
     */
    private void addEvents() {
        ivSend.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivReload.setOnClickListener(this);
        ivSelectImage.setOnClickListener(this);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivSend.setImageResource(R.drawable.btn_send_normal);
                } else {
                    ivSend.setImageResource(R.drawable.btn_send_disable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                emojiHandler.getSmiledText(ChatPersonActivity.this, s);
                Log.e("EMOJI", s.toString());
            }
        });

        getIDRoom(Integer.parseInt(me.getId()), Integer.parseInt(friend.getId()));
    }

    /**
     * Get id room
     *
     * @param id_user
     * @param id_friend
     */
    private void getIDRoom(int id_user, int id_friend) {
        class GetIDRoom extends AsyncTask<Integer, Void, String> {
            TransparentProgressDialog loading;
            RequestHandler connectServer = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new TransparentProgressDialog(ChatPersonActivity.this, R.drawable.icon_progress_bar);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int res = jsonObject.getInt("chatroomid");
                    id_room = res;
                    showToast("Bắt đầu trò chuyện với " + friend.getName() + " tại phòng " + id_room);
                } catch (JSONException e) {
                    showToast("Kết nối máy chủ lỗi");
                }
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Integer... params) {
                HashMap<String, Integer> data = new HashMap<String, Integer>();
                data.put("userid", params[0]);
                data.put("friendid", params[1]);
                String result = connectServer.sendPostRequestInt(ID_ROOM_URL, data);
                return result;
            }
        }

        GetIDRoom getID = new GetIDRoom();
        getID.execute(id_user, id_friend);
    }

    /**
     * Receiver result from activity SendImage
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            urlImage = bundle.getString("url");
            etMessage.setText(urlImage);
            checkSendImg = true;
            ivSelectImage.setImageResource(R.drawable.icon_photo_disable);
        }
    }


    /**
     * Display notification
     *
     * @param msg
     */
    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
    }

    /**
     * Receiver message from server send to
     *
     * @param msg
     */
    private void getMessageFromServer(String msg) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(msg);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = new JSONObject(jsonArray.get(i).toString());
                    message = new Message();
                    message.setName(json.getString("name"));
                    message.setDateTime(sdf.format(new Date()));
                    if (json.getString("name").equals(me.getName())) {
                        message.setMe(true);
                    } else {
                        message.setMe(false);
                    }

                    if (json.getString("type").equals(TYPE_IMAGE)) {
                        message.setMessage("");
                        bitmap = new LoadImage().execute(json.getString("message")).get();
                        message.setImage(bitmap);
                    } else {
                        message.setMessage(json.getString("message"));
                        message.setImage(null);
                    }

                    messageArrayList.add(message);
                    chatPersonAdapter.notifyDataSetChanged();
                }
            } else {
                showToast("Đây là lần đầu bạn trò chuyện với người này");
            }
        } catch (JSONException e) {
            showToast("Không thể đọc tin nhắn này");
        } catch (InterruptedException e) {
            showToast("Không thể đọc tin nhắn này");
        } catch (ExecutionException e) {
            showToast("Không thể đọc tin nhắn này");
        }
    }

    /**
     * Send message to server
     */
    private void sendMessage() {
        if (!etMessage.getText().toString().isEmpty()) {
            JSONObject object = new JSONObject();
            try {
                object.put("name", me.getName());
                object.put("message", etMessage.getText().toString());
                if (checkSendImg) {
                    object.put("type", TYPE_IMAGE);
                    checkSendImg = false;
                } else {
                    object.put("type", TYPE_MESSAGE);
                }
                mSocket.emit("input", object);
                etMessage.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bạn chưa nhập tin nhắn");
            etMessage.requestFocus();
        }
    }

    /**
     * Join chat room
     */
    private void joinChatRoom() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("chatid", id_room);
            mSocket.emit("chatid", jsonObject);
        } catch (JSONException e) {
            showToast("Không thể gửi mã phòng");
        }
        showToast("Đang tải các tin nhắn cũ ...");
        ivReload.setEnabled(false);
    }

    /**
     * Select image from device
     */
    private void selectImage() {
        Intent intent_img = new Intent(ChatPersonActivity.this, SendImageActivity.class);
        startActivityForResult(intent_img, REQUEST_CODE);
    }

    /**
     * Event
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ivSend:
                if (hasConnection()) {
                    sendMessage();
                } else  {
                    showToast("Vui lòng kiểm tra kết nối internet");
                }
                break;
            case R.id.ivReload:
                if (hasConnection()) {
                    ivReload.setImageResource(R.drawable.history);
                    joinChatRoom();
                    etMessage.setEnabled(true);
                    ivSend.setEnabled(true);
                    ivSelectImage.setEnabled(true);
                } else {
                    showToast("Vui lòng kiểm tra kết nối internet");
                }
                break;
            case R.id.ivSelectImage:
                ivSelectImage.setImageResource(R.drawable.icon_photo_normal);
                selectImage();
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (hasConnection()) {
                sendMessage();
            } else  {
                showToast("Vui lòng kiểm tra kết nối internet");
            }
            return true;
        }
        return super.dispatchKeyEvent(e);
    }

    /**
     * Load image from url
     */
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        TransparentProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new TransparentProgressDialog(ChatPersonActivity.this, R.drawable.icon_progress_bar);
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            dialog.dismiss();
        }
    }
}
