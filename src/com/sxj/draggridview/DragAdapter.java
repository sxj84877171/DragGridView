package com.sxj.draggridview;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 
 * @author Elvis
 *
 */
public class DragAdapter extends BaseAdapter {
	private List<HashMap<String, String>> list;
	private LayoutInflater mInflater;

	public DragAdapter(Context context, List<HashMap<String, String>> list) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 由于复用convertView导致某些item消失了，所以这里不复用item，
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.grid_item, null);
		ImageView mImageView = (ImageView) convertView
				.findViewById(R.id.item_image);
		if(position % 6 ==0){
			mImageView.setImageResource(R.drawable.c);
		}else if(position % 6 ==1){
			mImageView.setImageResource(R.drawable.d);
		}else if(position % 6 ==2){
			mImageView.setImageResource(R.drawable.e);
		}else if(position % 6 ==3){
			mImageView.setImageResource(R.drawable.f);
		}else if(position % 6 ==4){
			mImageView.setImageResource(R.drawable.g);
		}else if(position % 6 ==5){
			mImageView.setImageResource(R.drawable.h);
		}

		return convertView;
	}

}
