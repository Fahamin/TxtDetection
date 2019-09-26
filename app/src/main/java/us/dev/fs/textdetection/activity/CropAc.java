package us.dev.fs.textdetection.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import us.dev.fs.textdetection.R;

public class CropAc extends AppCompatActivity {
    public static Bitmap croppedImage;
    public static Uri uriImage;
    private CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        File filePath = new File(Environment.getExternalStorageDirectory() +
                PermissionAc.DIRECTORY_PATH);
        File fileImage = new File(filePath, PermissionAc.FILE_NAMES.get(PermissionAc.CURRENT_PAGE));
        uriImage = Uri.fromFile(fileImage);

        // Set URI image to display
        cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setImageUriAsync(Uri.parse(uriImage.toString()));


    }

    public void onClick(View view) {
        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                croppedImage = result.getBitmap();
                startActivity(new Intent(CropAc.this, BinarizationAc.class));

            }
        });
        cropImageView.getCroppedImageAsync();
    }
}
