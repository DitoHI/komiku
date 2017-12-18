package com.example.sinichi.komiku;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Image_view extends Activity {
    ImageView imageView;
    ImageButton scan;
    ImageButton save;
    ImageButton edit;
    ImageButton shareBtn;
    static final int CAM_REQUEST = 1;
    final int PIC_CROP = 2;
    final int REQUEST_SHARE_ACTION = 3;
    public Bitmap cam_img;
    public FileOutputStream output;
    Intent share;
    File file;
    Drawable d;
    String localAbsoluteFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_gambar);
        imageView = (ImageView) findViewById(R.id.pic_view);
        shareBtn = (ImageButton)findViewById(R.id.share);
        edit = (ImageButton)findViewById(R.id.edit);

        /*Intent camera_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_Intent, CAM_REQUEST);
        */
        if (getIntent().hasExtra("photoResult"))
            cam_img = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("photoResult"), 0, getIntent().getByteArrayExtra("photoResult").length);
        else if (getIntent().hasExtra("photoTaken"))
            cam_img = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("photoTaken"), 0, getIntent().getByteArrayExtra("photoTaken").length);

        imageView.setImageBitmap(cam_img);
        btnClicked();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cam_img = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(cam_img);
    } */

    public void btnClicked() {
//
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                cam_img.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Image_view.this, "Edit the image", Toast.LENGTH_SHORT).show();
                Intent goToEditClass = new Intent(Image_view.this, Edit.class);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                cam_img.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                goToEditClass.putExtra("bitmapOwned", bs.toByteArray());

                startActivity(goToEditClass);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        //get the returned data
          Bundle extras = data.getExtras();
//        //get the cropped bitmap
          cam_img = data.getExtras().getParcelable("data");
          imageView.setImageBitmap(cam_img);
    }
}