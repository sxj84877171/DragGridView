package com.sxj.draggridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.sxj.framework.DragFrameLayout;

/**
 * 
 */
public class MainActivity extends Activity {
	private List<HashMap<String, String>> dataSourceList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DragFrameLayout mDragGridView = (DragFrameLayout) findViewById(R.id.dragGridView);
		int all = 30 ;
		while (all-- > 0) {
			HashMap<String, String> itemHashMap = new HashMap<String, String>();
			dataSourceList.add(itemHashMap);
		}

		final DragAdapter mDragAdapter = new DragAdapter(this, dataSourceList);

		mDragGridView.setAdapter(mDragAdapter);
	}

}
