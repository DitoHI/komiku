package com.example.sinichi.komiku.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.sinichi.komiku.Action;
import com.example.sinichi.komiku.CustomGallery;
import com.example.sinichi.komiku.GalleryAdapter;
import com.example.sinichi.komiku.R;
import com.example.sinichi.komiku.adapter.LayoutAdapter;
import com.example.sinichi.komiku.layer.LayoutCollage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class LayoutActivity extends Activity {
	public static final String TAG = "LAYOUT";
	public static final String EXTRA_AUTHOR_ID = TAG + "_authorID";
	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPick;
	Button btnGalleryPickMul;

	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;

	//private String mUserId;
	private Intent stepThreeIntent;
	private static final LayoutCollage[] mList = {
			new LayoutCollage(R.drawable.photo2v, 2, true),
			new LayoutCollage(R.drawable.photo2h, 2, false),
			new LayoutCollage(R.drawable.photo3l, 3, false),
			new LayoutCollage(R.drawable.photo3u, 3, true),
			new LayoutCollage(R.drawable.photo4, 4, true),
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);

		TextView stepView = (TextView) findViewById(R.id.title_step2);
		stepView.setText(getString(R.string.pattern_title_step, "2"));

	//	mUserId = "Dito Hafizh";

		GridView gridView = (GridView) findViewById(R.id.collage_list);
		gridView.setAdapter(new LayoutAdapter(this, mList));

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
									long id) {
				clickLayout(position);
			}
		});
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		outState.putString(EXTRA_AUTHOR_ID, mUserId);
//		super.onSaveInstanceState(outState);
//	}

	public void clickLayout(int position) {
		stepThreeIntent = new Intent(this, GalleryActivity.class);

		Bundle bundle = new Bundle();
		//bundle.putString(GalleryActivity.EXTRA_AUTHOR_ID, mUserId);
		bundle.putSerializable(GalleryActivity.EXTRA_LAYOUT, mList[position]);
		stepThreeIntent.putExtras(bundle);
		Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
		startActivityForResult(i, 200);

		startActivity(stepThreeIntent);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			adapter.clear();

			viewSwitcher.setDisplayedChild(1);
			String single_path = data.getStringExtra("single_path");
			imageLoader.displayImage("file://" + single_path, imgSinglePick);

		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;

				dataT.add(item);
			}

			viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataT);
		}
	}

}