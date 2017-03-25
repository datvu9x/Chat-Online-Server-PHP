package dev.datvt.funnychat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.controls.TransparentProgressDialog;

/**
 * Created by datvt on 4/28/2016.
 */
public class SendImageActivity extends Activity implements View.OnClickListener {

    private static final String URL_UPLOAD_IMAGE = "http://chatappphp-dq0812.rhcloud.com/pages/UploadPicture.php";
    private static final int REQUEST_CODE_IMG = 100;

    private Uri selectedImage;
    private String imgPath;
    private String urlImage;
    private int serverResponseCode = 0;

    private boolean checkPickImage = false;

    private ImageView pickImage, imageView;
    private TextView nameImage, sendImage;
    private Typeface typeface;
    private Toast toast;
    private View toastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_image_activity);

        getForWidgets();
        addEvents();
    }

    private void getForWidgets() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/rix_love_fool.ttf");
        nameImage = (TextView) findViewById(R.id.tvTitle_img);
        nameImage.setTypeface(typeface);

        sendImage = (TextView) findViewById(R.id.tvSendImage);
        sendImage.setTypeface(typeface);
        pickImage = (ImageView) findViewById(R.id.ivPickImage);
        imageView = (ImageView) findViewById(R.id.ivImage);
    }

    private void addEvents() {
        sendImage.setOnClickListener(this);
        pickImage.setOnClickListener(this);
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

    private void uploadImage() {
        new UploadImage().execute(imgPath);
        checkPickImage = false;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), REQUEST_CODE_IMG);
    }

    /**
     * Get path file
     *
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            showToast(e.toString());
            return null;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMG && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
            checkPickImage = true;
            imgPath = getPath(selectedImage);
            nameImage.setText(imgPath);
        }
    }

    private void showToast(String msg) {
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.border_toast);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSendImage:
                if (hasConnection()) {
                    if (checkPickImage) {
                        uploadImage();
                    } else {
                        showToast("Bạn chưa chọn hình ảnh");
                    }
                } else  {
                    showToast("Vui lòng kiểm tra kết nối internet");
                }
                break;
            case R.id.ivPickImage:
                selectImage();
                break;
        }
    }

    /**
     * Upload image to server
     */
    private class UploadImage extends AsyncTask<String, Void, String> {

        TransparentProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new TransparentProgressDialog(SendImageActivity.this, R.drawable.icon_progress_bar);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.isEmpty() && result != null) {
                urlImage = result;
                Intent i = getIntent();
                Bundle b = new Bundle();
                b.putString("url", urlImage);
                i.putExtras(b);
                setResult(RESULT_OK, i);
                Log.i("URL_IMG", urlImage);
                finish();
            } else {
                showToast("Không nhận được hình ảnh từ máy chủ. Hãy lựa chọn lại hình ảnh");
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String sourceFileUri = params[0];
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(URL_UPLOAD_IMAGE);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("photo", sourceFileUri);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
                                + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();
                        Log.i("uploadFile", "HTTP Response is : "
                                + serverResponseMessage + ": " + serverResponseCode);
                        if (serverResponseCode == 200) {
                            StringBuilder sb = new StringBuilder();
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line).append("\n");
                            }
                            return sb.toString();
                        }
                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                    } catch (Exception e) {
                        Log.i("Upload file to server: ", e.getMessage());
                    }
                }
            } catch (Exception ex) {
                Log.i("Upload file to server: ", ex.getMessage());
            }
            return null;
        }
    }
}
