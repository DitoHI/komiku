package com.example.sinichi.komiku.layer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.sinichi.komiku.CustomGallery;
import com.example.sinichi.komiku.R;
import com.example.sinichi.komiku.loaders.BitmapLoader;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Collage extends RelativeLayout {
	private Canvas mCollage;
	private Context mContext;
	ArrayList<Bitmap> mTasks;
	
	public Canvas getCollage(){
		return mCollage;
	}
	
	public Collage(Context aContext, Context context) {
		super(aContext);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.collage_layout, this);
		
	}
	
	public Bitmap setCollage(ArrayList<CustomGallery> list, LayoutCollage layout) throws InterruptedException, ExecutionException {
		ProgressDialog mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getText(R.string.title_generate));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.show();
		
		ImageView imgView = (ImageView) findViewById(R.id.item_photoview) ; 
		int width =  (int)(mContext.getResources().getDimension(R.dimen.img_size_big));
		imgView.setLayoutParams(new LayoutParams(width, width));

		if (layout != null){
			mTasks = new ArrayList<Bitmap>(layout.getCount());
			for(int i = 0; i < layout.getCount(); i++) {
				BitmapLoader bmpl = new BitmapLoader();
				//bmpl.execute(list.get(i).sdcardPath);
				mTasks.add(BitmapFactory.decodeFile(list.get(i).sdcardPath));
			}

			Bitmap resultBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);;
			Canvas cvs = new Canvas(resultBitmap);
			for(int i = 0; i < layout.getCount(); i++) {
				cvs.drawBitmap(
						layout.getScaledBitmap(mTasks.get(i), i, width),
						layout.getCoords(i, width).getX(),
						layout.getCoords(i, width).getY(),
						null
				);
			}

			mProgressDialog.dismiss();

			imgView.setImageBitmap(resultBitmap);
			return resultBitmap;
		}
		else{
			System.out.println("Layout is null, do not attempt any object to Layout");
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.balek);
		}
	}
	
}
