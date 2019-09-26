package us.dev.fs.textdetection.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.googlecode.leptonica.android.GrayQuant;
import com.googlecode.leptonica.android.Pix;

import us.dev.fs.textdetection.R;
import us.dev.fs.textdetection.helper.OtsuThresholder;

public class BinarizationAc extends AppCompatActivity  {
    public static Bitmap umbralization;
    private Pix pix;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binarization);

        img = (ImageView) findViewById(R.id.croppedImage);

        pix = com.googlecode.leptonica.android.ReadFile.readBitmap(CropAc.croppedImage);
        OtsuThresholder otsuThresholder = new OtsuThresholder();
        int threshold = otsuThresholder.doThreshold(pix.getData());
        /* Increasing value of threshold is better */
        threshold += 20;
        umbralization = com.googlecode.leptonica.android.WriteFile.writeBitmap
                (GrayQuant.pixThresholdToBinary(pix, threshold));
        img.setImageBitmap(umbralization);
        SeekBar seekBar =  findViewById(R.id.umbralization);
        seekBar.setProgress((50 * threshold) / 254);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                umbralization = com.googlecode.leptonica.android.WriteFile.writeBitmap(
                        GrayQuant.pixThresholdToBinary(pix, ((254 * seekBar.getProgress()) / 50)));
                img.setImageBitmap(umbralization);
            }
        });
    }


    public void onClick(View view) {
            startActivity(new Intent(BinarizationAc.this, RecognizerAc.class));

    }
}
