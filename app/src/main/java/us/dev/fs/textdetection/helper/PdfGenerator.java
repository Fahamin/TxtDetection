package us.dev.fs.textdetection.helper;

import android.os.Environment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import us.dev.fs.textdetection.activity.PermissionAc;


public class PdfGenerator {
    private static String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static void generateText(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateImagePdf() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(
                    dirpath + "/" +
                            PermissionAc.TITLE + "_image.pdf")); //  Change pdf's name.
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();

        for (int i = 0; i < PermissionAc.TOTAL_PAGES; i++) {
            Image img = null;  // Change image's name and extension.
            try {
                img = Image.getInstance(dirpath + PermissionAc.DIRECTORY_PATH + File.separator +
                        PermissionAc.FILE_NAMES.get(i));
            } catch (BadElementException | IOException e) {
                e.printStackTrace();
            }

            float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float height = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            if (img != null) {
                img.scaleToFit(width, height);
            }



            try {
                document.add(img);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        document.close();
    }

    public static void deleteImageFiles() {
        for (int i = 0; i < PermissionAc.TOTAL_PAGES; i++) {
            File file = new File(dirpath + PermissionAc.DIRECTORY_PATH + File.separator,
                    PermissionAc.FILE_NAMES.get(i));
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("File deleted on :" + file.getAbsolutePath());
                } else {
                    System.out.println("File not deleted on :" + file.getAbsolutePath());
                }
            }
        }
    }
}
