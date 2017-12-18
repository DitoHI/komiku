package com.example.sinichi.komiku.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.sinichi.komiku.CustomGallery;
import com.example.sinichi.komiku.Edit;
import com.example.sinichi.komiku.R;
import com.example.sinichi.komiku.layer.Collage;
import com.example.sinichi.komiku.layer.LayoutCollage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class FinalActivity extends Activity implements OnClickListener {
	public static final String TAG = "FINAL";
	
    public static final String EXTRA_LAYOUT = TAG + "_layout";

    private LayoutCollage mLayout;
    private ArrayList<CustomGallery> mList;
    private Bitmap mCollage;
	ImageButton backToEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.final_collage);
		backToEdit = (ImageButton)findViewById(R.id.backToEdit);
		
		if (savedInstanceState == null) {
			mLayout = (LayoutCollage) getIntent().getExtras().getSerializable(EXTRA_LAYOUT);
		} else {
			mLayout = (LayoutCollage) savedInstanceState.getSerializable(EXTRA_LAYOUT);
		}
        mList = GalleryActivity.selected;
		
		LinearLayout collageLayout = (LinearLayout) findViewById(R.id.collage_layout);
		Collage frame = new Collage(getApplicationContext(), this);
		try {
			mCollage = frame.setCollage(mList, mLayout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		collageLayout.addView(frame);
		backToEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				mCollage.compress(CompressFormat.JPEG, 50, stream);
				byte[] byteArray = stream.toByteArray();

				Intent intent = new Intent(FinalActivity.this, Edit.class);
				intent.putExtra("mCollage", byteArray);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(EXTRA_LAYOUT, mLayout);
		super.onSaveInstanceState(outState);
	}

	private String saveCollage() {
		final String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path, getString(R.string.pattern_save_collage_name, Long.toString(Calendar.getInstance().getTimeInMillis())));
		try {			 
			FileOutputStream fOS = new FileOutputStream(file);
			mCollage.compress(CompressFormat.JPEG, 85, fOS);
			fOS.flush();
			fOS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return file.getAbsolutePath();
	}

	@Override
	public void onClick(View v) {

	}
}
