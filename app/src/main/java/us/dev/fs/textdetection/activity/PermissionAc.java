package us.dev.fs.textdetection.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import us.dev.fs.textdetection.R;

public class PermissionAc extends AppCompatActivity {

    public static String TITLE;
    public static int TOTAL_PAGES;
    public static String CONTENT;
    public static int CURRENT_PAGE;
    public static String DIRECTORY_PATH = "/Android/data";
    public static ArrayList<String> FILE_NAMES = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }


        ImageView image = findViewById(R.id.imageView);

        image.animate().rotation(1000f).setDuration(2000);
        image.animate().scaleY(0.5f).scaleX(0.5f);


                    TOTAL_PAGES = 22;
                    CURRENT_PAGE = 0;
                    CONTENT = "";
                    if (CURRENT_PAGE < TOTAL_PAGES) {
                        if (ContextCompat.checkSelfPermission(PermissionAc.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Request Camera Permissions
                            requestCameraPermission();
                        } else if (ContextCompat.checkSelfPermission(PermissionAc.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Request External Storage Permissions
                            requestExternalStoragePermission();
                        }  else {
                            startCamera();
                        }
                    }

    }



    private void requestExternalStoragePermission() {
        ActivityCompat.requestPermissions(PermissionAc.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(PermissionAc.this,
                new String[]{Manifest.permission.CAMERA}, 1);
    }

    private void startCamera() {
        long TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(PermissionAc.this, CaptureAc.class));
                            finish();

            }
        }, TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestExternalStoragePermission();
                } else {
                    Toast.makeText(getApplicationContext(), "Camera permission not granted.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermission();
                } else {
                    Toast.makeText(getApplicationContext(), "External Storage permission not granted.",
                            Toast.LENGTH_SHORT).show();
                }
                return;

        }
    }

}
