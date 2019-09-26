package us.dev.fs.textdetection.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import us.dev.fs.textdetection.R;
import us.dev.fs.textdetection.helper.ContactWriter;
import us.dev.fs.textdetection.helper.PdfGenerator;

public class RecognizerAc extends AppCompatActivity implements View.OnClickListener {

    private TextView textExtracted;
    private ProgressDialog progressCopy;
    private ProgressDialog progressOcr;
    private String textScanned;     // textScanned has extracted text output
    private String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            PermissionAc.DIRECTORY_PATH + File.separator;
    private AsyncTask<Void, Void, Void> copy = new copyTask();
    private AsyncTask<Void, Void, Void> ocr = new ocrTask();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizer);

        initActivity();

        copy.execute();
        ocr.execute();
    }

    private void initActivity() {
        textExtracted = findViewById(R.id.textExtracted);

        textExtracted.setMovementMethod(new ScrollingMovementMethod());

        progressCopy = new ProgressDialog(RecognizerAc.this);
        progressCopy.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressCopy.setIndeterminate(true);
        progressCopy.setCancelable(false);
        progressCopy.setTitle("Detection");
        progressCopy.setMessage("Text Detection Ready");
        // Setting progress dialog for ocr job.
        progressOcr = new ProgressDialog(this);
        progressOcr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressOcr.setIndeterminate(true);
        progressOcr.setCancelable(false);
        progressOcr.setTitle("OCR");
        progressOcr.setMessage(" please wait Text Scanning");
        textScanned = "";
        Button mFab = findViewById(R.id.nextStep);
        mFab.setOnClickListener(this);
    }

    // Copy assets trainneddata to tessdata in External Storage
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("trainneddata");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) {
            for (String filename : files) {
                Log.i("files", filename);
                InputStream in;
                OutputStream out;
                String dirout = DATA_PATH + "tessdata/";
                File outFile = new File(dirout, filename);
                if (!outFile.exists()) {
                    try {
                        in = assetManager.open("trainneddata/" + filename);
                        (new File(dirout)).mkdirs();
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                        in.close();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        Log.e("tag", "Error creating files", e);
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void recognizeText() {
        String language = "eng";
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(DATA_PATH, language, TessBaseAPI.OEM_TESSERACT_ONLY);
        baseApi.setImage(BinarizationAc.umbralization);
        textScanned = baseApi.getUTF8Text();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextStep) {
            if (PermissionAc.CURRENT_PAGE < PermissionAc.TOTAL_PAGES) {
                // if pages scanned are less than total pages
                startActivity(new Intent(RecognizerAc.this, CaptureAc.class));
            } else {
                // if all pages scanned, then genereate pdf's and save contact
                PdfGenerator.generateImagePdf();
                PdfGenerator.generateText(PermissionAc.TITLE,
                        PermissionAc.CONTENT);
                PdfGenerator.deleteImageFiles();
                Toast.makeText(this, "PDF Generated", Toast.LENGTH_SHORT).show();
                ContactWriter contactWriter = new ContactWriter(this);
                contactWriter.addContact(PermissionAc.TITLE, PermissionAc.CONTENT);
                Toast.makeText(this, " Saved as " + PermissionAc.TITLE, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RecognizerAc.this, PermissionAc.class));
            }
        }
    }

    public void Emailsend(View view) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
            intent.putExtra(Intent.EXTRA_SUBJECT, "OCR");
            intent.putExtra(Intent.EXTRA_TEXT, textScanned);
            intent.setType("email/rfc822");
            startActivity(Intent.createChooser(intent, "Select Email Sending Message:"));

    }

    private class copyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressCopy.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressCopy.cancel();
            progressOcr.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("CopyTask", "copying..");
            copyAssets();
            return null;
        }
    }

    private class ocrTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressOcr.cancel();
            textExtracted.setText(textScanned);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("OCRTask", "extracting..");
            recognizeText();
            PermissionAc.CONTENT += textScanned;
            PermissionAc.CURRENT_PAGE++;
            return null;
        }
    }
}
