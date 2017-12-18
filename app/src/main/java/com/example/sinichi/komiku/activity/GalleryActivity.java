package com.example.sinichi.komiku.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sinichi.komiku.CustomGallery;
import com.example.sinichi.komiku.GalleryAdapter;
import com.example.sinichi.komiku.R;
import com.example.sinichi.komiku.layer.InstagramImage;
import com.example.sinichi.komiku.layer.LayoutCollage;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class GalleryActivity extends Activity implements OnClickListener {
	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgNoMedia;
	Button btnGalleryOk;

	String action;
	private ImageLoader imageLoader;

	public static final String TAG = "GALLERY";
	//public static final String EXTRA_AUTHOR_ID = TAG + "_authorID";
	public static final String EXTRA_LAYOUT = TAG + "_layout";

	//private String mUserId;
	private LayoutCollage mLayout;
	private int mCount;
	private ArrayList<InstagramImage> mImages;
	public static ArrayList<CustomGallery> selected;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery2);

		if (savedInstanceState == null) {

//			mUserId = getIntent().getExtras().getString(EXTRA_AUTHOR_ID);
			mLayout = (LayoutCollage) getIntent().getExtras().getSerializable(EXTRA_LAYOUT);
		} else {
			//mUserId = savedInstanceState.getString(EXTRA_AUTHOR_ID);
			mLayout = (LayoutCollage) savedInstanceState.getSerializable(EXTRA_LAYOUT);
		}
		mCount = mLayout.getCount();

		TextView stepView = (TextView) findViewById(R.id.title_step3);
		stepView.setText(getString(R.string.pattern_title_step, "3"));
		TextView titleView = (TextView) findViewById(R.id.title_step3_select_photos);
		titleView.setText(getString(R.string.pattern_title_step3_info, mCount));

		initImageLoader();
		init();
	}

	private void initImageLoader() {
		try {
			String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.temp_tmp";
			new File(CACHE_DIR).mkdirs();

			File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(), CACHE_DIR);

			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(getBaseContext()).defaultDisplayImageOptions(defaultOptions).discCache(new UnlimitedDiscCache(cacheDir)).memoryCache(new WeakMemoryCache());

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

		} catch (Exception e) {

		}
	}

	private void init() {

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, true, true);
		gridGallery.setOnScrollListener(listener);

		gridGallery.setOnItemClickListener(mItemMulClickListener);
		adapter.setMultiplePick(true);

		gridGallery.setAdapter(adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		btnGalleryOk = (Button) findViewById(R.id.button_generate_collage);
		btnGalleryOk.setOnClickListener(mOkClickListener);

		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.addAll(getGalleryPhotos());
						//checkImageStatus();
					}
				});
				Looper.loop();
			};

		}.start();

	}

	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			selected = adapter.getSelected();
			generate();
		}
	};
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			adapter.changeSelection(v, position);
		}
	};

	private ArrayList<CustomGallery> getGalleryPhotos() {
		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;

			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);

			if (imagecursor != null && imagecursor.getCount() > 0) {

				while (imagecursor.moveToNext()) {
					CustomGallery item = new CustomGallery();

					int dataColumnIndex = imagecursor
							.getColumnIndex(MediaStore.Images.Media.DATA);

					item.sdcardPath = imagecursor.getString(dataColumnIndex);

					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}

	protected void onSaveInstanceState(Bundle outState) {
		//outState.putString(EXTRA_AUTHOR_ID, mUserId);
		outState.putSerializable(EXTRA_LAYOUT, mLayout);
		super.onSaveInstanceState(outState);
	}

	private void generate(){
		Intent intent = new Intent(this, FinalActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(FinalActivity.EXTRA_LAYOUT, mLayout);
		intent.putExtras(bundle);

		startActivity(intent);
	}

	@Override
	public void onClick(View v) {

	}
}