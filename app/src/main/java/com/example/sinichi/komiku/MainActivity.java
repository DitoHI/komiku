package com.example.sinichi.komiku;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {
    ImageButton button;
    private static final int CAM_REQUEST = 1313;
    private static final int SELECT_PHOTO = 1414;
    Uri imageUri;
    Bitmap thumbnail;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        imgView = (ImageView) findViewById(R.id.Logo_Home);

        btnClick();
    }

    /*private void setupProfileButton() {
        ImageButton profileButton = (ImageButton) findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "You clicked Profile Page", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
    }
    private void setupScanButton() {
        ImageButton scanButton = (ImageButton) findViewById(R.id.scan);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Scan Picture", Toast.LENGTH_LONG).show();
                //startActivity(new Intent(MainActivity.this, Image_view.class));
                Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraintent, CAM_REQUEST);
            }
        });
    }
    public void setupBrowse() {
        ImageButton browseButton = (ImageButton) findViewById(R.id.browse);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Browse the Gallery", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, Browse.class));

            }
        });
    } */

    public void btnClick() {
        ImageButton scanButton = (ImageButton) findViewById(R.id.scan);
        ImageButton profileButton = (ImageButton) findViewById(R.id.profile);
        ImageButton browseButton = (ImageButton) findViewById(R.id.browse);
        final Intent goToImageView = new Intent(this, Image_view.class);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "You clicked Profile Page", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Scan Picture", Toast.LENGTH_LONG).show();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                startActivityForResult(intent, CAM_REQUEST);
            }
        });
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Browse the Gallery", Toast.LENGTH_LONG).show();

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST) {
            Bitmap imageData = null;
            if (resultCode == RESULT_OK) {
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, Image_view.class);

                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                intent.putExtra("photoResult", bs.toByteArray());
                startActivity(intent);
            }
        }
        else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Intent j = new Intent(this, Image_view.class);
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            Bitmap bitmap = getScaledBitmap(imagePath, 300, 300);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            j.putExtra("photoTaken", bs.toByteArray());
            // Do something with the bitmap
            startActivity(j);
            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();

        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}

