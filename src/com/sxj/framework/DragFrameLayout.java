package com.sxj.framework;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class DragFrameLayout extends FrameLayout {

	private GridView mGridView;

	/**
	 * DragGridView��item������Ӧ��ʱ�䣬 Ĭ����1000���룬Ҳ������������
	 */
	private long dragResponseMS = 1000;
	private long animationTime = 500 ;
	private List<ViewIndex> list = new ArrayList<ViewIndex>();

	/**
	 * �Ƿ������ק��Ĭ�ϲ�����
	 */
	private boolean isDrag = false;

	private int mDownX;
	private int mDownY;
	private float mfDownX;
	private float mfDownY;
	/**
	 * ������ק��position
	 */
	private int mDragPosition;

	/**
	 * ����
	 */
	private Vibrator mVibrator;

	private boolean mAnimationEnd = true;

	private Context mContext;

	private Handler mHandler = new Handler();

	public DragFrameLayout(Context context) {
		this(context, null);
	}

	public DragFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;
		mGridView = new GridView(context, attrs, defStyle);
		addView(mGridView);
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) (ev.getX() - mGridView.getX());
			mDownY = (int) (ev.getY() - mGridView.getY());
			// ���ݰ��µ�X,Y�����ȡ�����item��position
			mDragPosition = mGridView.pointToPosition(mDownX, mDownY);
			if (mDragPosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(ev);
			}
			// ʹ��Handler�ӳ�dragResponseMSִ��mLongClickRunnable
			mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
			mfDownX = ev.getRawX();
			mfDownY = ev.getRawY();
//			System.out.println("ACTION_DOWN mDownX" + ev.getRawX() + ",mDownY"
//					+ ev.getRawY());

			break;
		case MotionEvent.ACTION_MOVE:
			// ��������ڰ��µ�item�����ƶ���ֻҪ������item�ı߽����ǾͲ��Ƴ�mRunnable
			float x2 = ev.getX();
			float y2 = ev.getY();
//			System.out.println("ACTION_MOVE mDownX" + ev.getRawX() + ",mDownY"
//					+ ev.getRawX());
			if (!isInTouchView(x2, y2) && !isDrag) {
				mHandler.removeCallbacks(mLongClickRunnable);
			} else if (isDrag) {
				if (onTouchEvent(ev)) {
					return true;
				}

			}
			break;
		case MotionEvent.ACTION_UP:
			int upX = (int) ev.getX();
			int upY = (int) ev.getY();
			mHandler.removeCallbacks(mLongClickRunnable);
			if (isDrag) {
				if (onTouchEvent(ev)) {
					return true;
				}
			}
			break;
		}
		return super.dispatchTouchEvent(ev);

	}

	private boolean isInTouchView(float x, float y) {
//		System.out.println("x=" + x + ",y=" + y + ", downx=" + mfDownX + ",downy=" + mfDownY);
		if (abs(x - mfDownX) > 100) {
			return false;
		}
		if (abs(y - mfDownY) > 100) {
			return false;
		}
		return true;
	}

	private float abs(float x) {
		return x > 0 ? x : -x;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isDrag) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				onDragItem( ev.getRawX(), ev.getRawY());
				break;
			case MotionEvent.ACTION_UP:
				onStopDrag( ev.getRawX(),  ev.getRawY());

				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	public void setAdapter(ListAdapter adapter) {
		mGridView.setAdapter(adapter);
	}

	/**
	 * ֹͣ��ק���ǽ�֮ǰ���ص�item��ʾ���������������Ƴ�
	 */
	private void onStopDrag(final float moveX,final float moveY) {
		if (mAnimationEnd) {
			isDrag = false;
			float dx = (moveX - mfDownX);
			float dy = (moveY - mfDownY);
			mfDownX = moveX;
			mfDownY = moveY;
			for (int i = 0; i < list.size(); i++) {
				ImageView imageView = list.get(i).imageView;
				list.get(i).x = imageView.getTranslationX() + dx;
				list.get(i).y = imageView.getTranslationY() + dy;
				imageView.setTranslationX(list.get(i).x);
				imageView.setTranslationY(list.get(i).y);
			}
			animateBack();
		} else {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					onStopDrag(moveX,moveY);

				}
			}, dragResponseMS);
		}
	}

	/**
	 * �϶�item��������ʵ����item�����λ�ø��£�item���໥�����Լ�GridView�����й���
	 * 
	 * @param x
	 * @param y
	 */
	private void onDragItem(float moveX, float moveY) {
		if (mAnimationEnd) {
			float dx = (moveX - mfDownX);
			float dy = (moveY - mfDownY);
			mfDownX = moveX;
			mfDownY = moveY;
			for (int i = 0; i < list.size(); i++) {
				ImageView imageView = list.get(i).imageView;
				list.get(i).x = imageView.getTranslationX() + dx;
				list.get(i).y = imageView.getTranslationY() + dy;
				imageView.setTranslationX(list.get(i).x);
				imageView.setTranslationY(list.get(i).y);
			}
		}else{
			
		}
	}

	// ���������Ƿ�Ϊ������Runnable
	private Runnable mLongClickRunnable = new Runnable() {

		@Override
		public void run() {
			isDrag = true; // ���ÿ�����ק
//			mVibrator.vibrate(50); // ��һ��
			// �������ǰ��µĵ���ʾitem����
			animateReorder(mDragPosition);
		}
	};

	@Override
	public void removeAllViews() {
		for (int i = 0; i < list.size(); i++) {
			removeView(list.get(i).imageView);
		}
		list.clear();
	}

	public List<ViewIndex> getChoosedView() {

		removeAllViews();
		list.clear();
		for (int index = mGridView.getFirstVisiblePosition(); index <= mGridView
				.getLastVisiblePosition(); index++) {
			View temp = mGridView.getChildAt(index
					- mGridView.getFirstVisiblePosition());
			if (temp != null) {
				temp.setDrawingCacheEnabled(true);
				Bitmap bitmap = Bitmap.createBitmap(temp.getDrawingCache());
				ImageView imageView = new ImageView(mContext);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				int left = (int) (temp.getX() - mGridView.getX());
				int top =  (int) (temp.getY() - mGridView.getY());
				int right = 0 ;
				int bottom = 0 ;
				if(left + bitmap.getWidth() > getWidth()){
					right = getWidth() - bitmap.getWidth() - left ;
				}
				if(top + bitmap.getHeight() > getHeight()){
					bottom = getHeight() - bitmap.getHeight() - top ;
				}
				layoutParams.setMargins(left, top,
						right, bottom);
				System.out.println("width:" + bitmap.getWidth() + ",height:" + bitmap.getHeight());
				imageView.setImageBitmap(bitmap);
				addView(imageView, layoutParams);
				ViewIndex viewIndex = new ViewIndex();
				viewIndex.imageView = imageView;
				viewIndex.x = temp.getX();
				viewIndex.y = temp.getY();
				list.add(viewIndex);
				temp.destroyDrawingCache();
			}
		}
		return list;
	}

	private void animateReorder(int postison) {
		List<Animator> resultList = new LinkedList<Animator>();
		final List<ViewIndex> viewList = getChoosedView();
		View indexView = mGridView.getChildAt(postison
				- mGridView.getFirstVisiblePosition());

		int size = viewList.size();
		for (int i = 0; i < size; i++) {
			ViewIndex viewIndex = viewList.get(i);
			resultList.add(createTranslationAnimations(viewIndex.imageView, 0,
					indexView.getX() - viewIndex.x + ((size - i) * 3), 0,
					indexView.getY() - viewIndex.y - ((size - i) * 5), -2f * (size - i),(size - i)*10));
		}

		AnimatorSet resultSet = new AnimatorSet();
		resultSet.playTogether(resultList);
		resultSet.setDuration(animationTime);
		resultSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mAnimationEnd = false;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mAnimationEnd = true;
			}
		});
		resultSet.start();
	}

	private void animateBack() {
		List<Animator> resultList = new LinkedList<Animator>();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			ViewIndex viewIndex = list.get(i);
			resultList.add(createTranslationAnimations(viewIndex.imageView,
					viewIndex.x, 0, viewIndex.y, 0, 0,0));
		}

		AnimatorSet resultSet = new AnimatorSet();
		resultSet.playTogether(resultList);
		resultSet.setDuration(animationTime);
		resultSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mAnimationEnd = false;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mAnimationEnd = true;
				removeAllViews();
			}
		});
		resultSet.start();
	}

	/**
	 * �����ƶ�����
	 * 
	 * @param view
	 * @param startX
	 * @param endX
	 * @param startY
	 * @param endY
	 * @param rotation
	 * @return
	 */
	private AnimatorSet createTranslationAnimations(View view, float startX,
			float endX, float startY, float endY, float rotation,long startDelay) {
		ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
				startX, endX);
		ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
				startY, endY);
		ObjectAnimator animRotation = ObjectAnimator.ofFloat(view, "rotation",
				rotation);
		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.setStartDelay(startDelay);
		animSetXY.playTogether(animX, animY, animRotation);
		return animSetXY;
	}

	public class ViewIndex {
		public ImageView imageView;
		public float x;
		public float y;
	}

}
