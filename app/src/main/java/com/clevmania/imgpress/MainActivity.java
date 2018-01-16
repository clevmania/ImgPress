package com.clevmania.imgpress;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {
    private ImageView av,cv;
    private final static int REQ_CODE = 1;
    private File imageFile, compressedFile;
    private Uri imgUri;
    private TextView imgsize, compImgSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        av = findViewById(R.id.iv_actual_img);
        cv = findViewById(R.id.iv_compressed_img);
        imgsize = findViewById(R.id.tv_actual_img);
        compImgSize = findViewById(R.id.tv_compressed_img_size);

        av.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pixIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pixIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(pixIntent,
                        "Select Image to Compress"),REQ_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE && resultCode == RESULT_OK){
            imgUri = data.getData();
            av.setImageURI(imgUri);
            Log.i("Image Uri", imgUri.getPath());
            Log.i("Image Uri", imgUri.toString());
            compressImg();
        }
    }

    public void compressImg(){
        try {
            imageFile = FileUtils.from(this,imgUri);
            compressedFile  = new Compressor(this).compressToFile(imageFile);
            cv.setImageBitmap(BitmapFactory.decodeFile(compressedFile.getAbsolutePath()));
            imgsize.setText(String.format("Actual Image size : %s",
                    getReadableFileSize(imageFile.length())));
            compImgSize.setText(String.format("Compressed Image size : %s",
                    getReadableFileSize(compressedFile.length())));
//            Log.i("Compressed Img Location", compressedFile.getPath());
//            Log.i("Compressed AbsLocation", compressedFile.getAbsolutePath());
//            Log.i("Compressed ContentUri", Uri.parse(compressedFile.toString()).toString());
//            Log.i("Compressed ContentUriP", Uri.fromFile(compressedFile).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getPathWithContentResolver(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
        // For a bitmap, ensure type is bitmap
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        return bitmap;
    }


    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
