package com.example.sinichi.komiku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.sinichi.komiku.activity.LayoutActivity;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Created by Sinichi on 12/22/2015.
 */
public class Edit extends Activity{
    ImageView image_view;
    ImageButton cllgBtn;
    ImageButton fltrBtn;
    ImageButton cropBtn;
    ImageView color_9;
    ImageView color_10;
    ImageView greyscale;
    ImageView hue;
    ImageView sepia;
    ImageView sepia_blue;
    ImageView sepia_green;
    Image_view imgView = new Image_view();
    EditText textStick;
    String name = "";

    GridLayout editLayout;
    Bitmap sticker;
    Bitmap cam_img;
    Drawable d;

    GridView gridGallery;
    Handler handler;
    ViewSwitcher viewSwitcher;
    GalleryAdapter adapter;
    ImageLoader imageLoader;
    ImageButton sharebtn;
    ImageButton text;
    ImageButton stickerbtn;
    ImageButton saveBtn;
    Bitmap bm1;
    Bitmap newBitmap;
    FrameLayout layout;
    LinearLayout effectRow;
    boolean flag;
    boolean flag2;
    StickerImageView iv_sticker;
    StickerTextView iv_text;

    Canvas newCanvas;
    RelativeLayout mRlvViwImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        editLayout = (GridLayout) findViewById(R.id.editLayout);

        cllgBtn = (ImageButton) findViewById(R.id.collage);
        sharebtn = (ImageButton) findViewById(R.id.share);
        stickerbtn = (ImageButton) findViewById(R.id.sticker);
        fltrBtn = (ImageButton) findViewById(R.id.filter);
        color_9 = (ImageView) findViewById(R.id.effect_color_9);
        color_10 = (ImageView) findViewById(R.id.effect_color_10);
        greyscale = (ImageView) findViewById(R.id.effect_greyscale);
        hue = (ImageView) findViewById(R.id.effect_hue);
        sepia = (ImageView) findViewById(R.id.effect_sepia);
        sepia_blue = (ImageView) findViewById(R.id.effect_sepia_blue);
        sepia_green= (ImageView) findViewById(R.id.effect_sepia_green);
        text = (ImageButton) findViewById(R.id.popUpText);
        layout = (FrameLayout) findViewById(R.id.frameLayout);
        effectRow = (LinearLayout) findViewById(R.id.effectPhoto);
        saveBtn = (ImageButton) findViewById(R.id.save);
        effectRow.setVisibility(View.GONE);

        flag = false;

        if(getIntent().hasExtra("mCollage")){
            byte[] byteArray = getIntent().getByteArrayExtra("mCollage");
            cam_img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        else{
            cam_img = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("bitmapOwned"), 0, getIntent().getByteArrayExtra("bitmapOwned").length);
        }

        //cam_img = overlayBitmapToCenter(BitmapFactory.decodeResource(getResources(), R.drawable.border_clip), cam_img);
        cam_img = addBlackBorder(cam_img, 20);

        image_view = (ImageView)findViewById(R.id.layout_edit);
        image_view.setImageBitmap(cam_img);

        initImageLoader();
        init();
        btnClick();

    }

    private Bitmap addBlackBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

//    public static Bitmap overlayBitmapToCenter(Bitmap bitmap1, Bitmap bitmap2) {
//        int bitmap1Width = bitmap1.getWidth();
//        int bitmap1Height = bitmap1.getHeight();
//        int bitmap2Width = bitmap2.getWidth();
//        int bitmap2Height = bitmap2.getHeight();
//
//        float marginLeft = (float) (bitmap1Width * 0.5 - bitmap2Width * 0.5);
//        float marginTop = (float) (bitmap1Height * 0.5 - bitmap2Height * 0.5);
//
//        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
//        Canvas canvas = new Canvas(overlayBitmap);
//        canvas.drawBitmap(bitmap1, new Matrix(), null);
//        canvas.drawBitmap(bitmap2, marginLeft, marginTop, null);
//        return overlayBitmap;
//    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).memoryCache(new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void init() {

        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);

    }

    public void addStickers()
    {
        FrameLayout canvas = (FrameLayout) findViewById(R.id.frameLayout);

        iv_sticker = new StickerImageView(this);
        Bitmap sticker = BitmapFactory.decodeResource(getResources(), R.drawable.btextright);
        Drawable bubble = new BitmapDrawable(sticker);
        iv_sticker.setImageDrawable(bubble);

        canvas.addView(iv_sticker);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_sticker.setControlItemsHidden(true);
            }
        });
        iv_sticker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_sticker.setControlItemsHidden(false);;
            }
        });
        cam_img = viewToBitmap(canvas);
        //cam_img = canvas.getDrawingCache();
    }

    public void addText() {
        FrameLayout canvas = (FrameLayout) findViewById(R.id.frameLayout);

        iv_text = new StickerTextView(this);
        iv_text.setText(name);
        canvas.addView(iv_text);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_text.setControlItemsHidden(true);
            }
        });
        iv_sticker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_text.setControlItemsHidden(false);
                ;
            }
        });
        cam_img = viewToBitmap(canvas);
    }

    public Bitmap viewToBitmap (View v) {
        Bitmap photoEdited = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(photoEdited);
        v.draw(canvas);
        return photoEdited;
    }

    public void btnClick()
    {
        sharebtn.setOnClickListener(new OnClickListener() {
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

        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 1;
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/Komiku");

                if (!dir.isDirectory())
                    dir.mkdirs();

                while (new File(dir, "photoKomiku_" + i + ".jpeg").exists())
                    i++;

                File file = new File(dir, "photoKomiku_" + i + ".jpeg");

                Toast.makeText(Edit.this, "Image Saved to SD card", Toast.LENGTH_SHORT).show();

                try {
                    FileOutputStream output = new FileOutputStream(file);
                    cam_img.compress(Bitmap.CompressFormat.JPEG, 100, output);

                    output.flush();
                    output.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fltrBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag){
                    effectRow.setVisibility(View.GONE);
                    flag = false;
                }
                else{
                    effectRow.setVisibility(View.VISIBLE);
                    flag = true;
                }
            }
        });

        stickerbtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(Edit.this, "Give stickers ...", Toast.LENGTH_SHORT).show();
                addStickers();
                //addStickers();
            }
        });

        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit.this);
                builder.setTitle("Title");

// Set up the input
                final EditText input = new EditText(Edit.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = input.getText().toString();
                        addText();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


                  //addText();

            }
        });

        cllgBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Edit.this, "Collage", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Edit.this, LayoutActivity.class));
            }
        });
        color_9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFilters imgFilter = new ImageFilters();
                Toast.makeText(Edit.this, "Filtering ...", Toast.LENGTH_SHORT).show();
                image_view.setImageBitmap(imgFilter.applyDecreaseColorDepthEffect(cam_img, 32));//,"effect_color_depth_32");
            }
        });
        color_10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFilters imgFilter = new ImageFilters();
                Toast.makeText(Edit.this, "Filtering ...", Toast.LENGTH_SHORT).show();
                image_view.setImageBitmap(imgFilter.applyDecreaseColorDepthEffect(cam_img, 64));//,"effect_color_depth_64");
            }
        });
        greyscale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFilters imgFilter = new ImageFilters();
                Toast.makeText(Edit.this, "Filtering ...", Toast.LENGTH_SHORT).show();
                image_view.setImageBitmap(imgFilter.applyGreyscaleEffect(cam_img));//saveBitmap(,"effect_grayscale");
            }
        });
        hue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFilters imgFilter = new ImageFilters();
                Toast.makeText(Edit.this, "Filtering ...", Toast.LENGTH_SHORT).show();
                image_view.setImageBitmap(imgFilter.applyHueFilter(cam_img, 2));//, "effect_hue");
            }
        });
        sepia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFilters imgFilter = new ImageFilters();
                Toast.makeText(Edit.this, "Filtering ...", Toast.LENGTH_SHORT).show();
                image_view.setImageBitmap(imgFilter.applySepiaToningEffect(cam_img, 10, 1.5, 0.6, 0.12));//,"effect_sepia");
            }
        });
        sepia_blue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFilters imgFilter = new ImageFilters();
                Toast.makeText(Edit.this, "Filtering ...", Toast.LENGTH_SHORT).show();
                image_view.setImageBitmap(imgFilter.applySepiaToningEffect(cam_img, 10, 1.2, 0.87, 2.1));//, "effect_sepia_blue");
            }
        });
        sepia_green.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFilters imgFilter = new ImageFilters();
                Toast.makeText(Edit.this, "Filtering ...", Toast.LENGTH_SHORT).show();
                image_view.setImageBitmap(imgFilter.applySepiaToningEffect(cam_img, 10, 0.88, 2.45, 1.43));//, "effect_sepia_green");
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String[] all_path = data.getStringArrayExtra("all_path");

        ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

        for (String string : all_path) {
            CustomGallery item = new CustomGallery();
            item.sdcardPath = string;

            dataT.add(item);
        }

        adapter.addAll(dataT);
    }

    private void saveBitmap(Bitmap bmp,String fileName){
        try {
            image_view.setImageBitmap(bmp);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName +".png");
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG,90,fos);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));

    }

    public Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {
            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);

            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);

            float[] hsv = new float[3];
            Color.colorToHSV(pixel, hsv);
            hsv[1] = 0.0f;
            float top = 0.87f; //Between 0.0f .. 1.0f I use 0.87f
            if (hsv[2] <= top) {
                hsv[2] = 0.0f;
            } else {
                hsv[2] = 1.0f;
            }
            pixel = Color.HSVToColor(hsv);
            buffOut.put(pixel);
        }

        buffOut.rewind();

        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();

        return base;
    }


}
