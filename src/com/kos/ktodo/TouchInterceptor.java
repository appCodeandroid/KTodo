/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kos.ktodo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class TouchInterceptor extends ListView {
	public static final String TAG = "TouchInterceptor";

	private ImageView mDragView;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private int mDragPos;      // which item is being dragged
	private int mFirstDragPos; // where was the dragged item originally
	private int mDragItemY;
	private int mDragPointY, mDragPointX;    // at what offset inside the item did the user grab it
	private int mCoordOffsetY, mCoordOffsetX;  // the difference between screen coordinates and coordinates in this view
	private DragListener mDragListener;
	private DropListener mDropListener;
	private RemoveListener mRemoveListener;
//	private int mUpperBound;
//	private int mLowerBound;
	//	private int mHeight;
//	private GestureDetector mGestureDetector;
//	private static final int FLING = 0;
	private static final int SLIDE = 1;
	private int mRemoveMode = 1;
	private Rect mTempRect = new Rect();
	private Bitmap mDragBitmap;
//	private final int mTouchSlop;
//	private int mItemHeightNormal;
//	private int mItemHeightExpanded;

	public TouchInterceptor(final Context context, final AttributeSet attrs) {
		super(context, attrs);
//        SharedPreferences pref = context.getSharedPreferences("Music", 3);
//        mRemoveMode = pref.getInt("deletemode", -1);
//		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		final Resources res = getResources();
//		mItemHeightNormal = 16; //res.getDimensionPixelSize(R.dimen.normal_height);
//		mItemHeightExpanded = 16; //res.getDimensionPixelSize(R.dimen.expanded_height);

//	    setRemoveListener(new RemoveListener() {
//		    public void remove(int which) {
//			    Log.i(TAG, "remove:" + which);
//		    }
//	    });
		setDragListener(new DragListener() {
			public void drag(final int from, final int to) {
//				Log.i(TAG, "drag from " + from + " to " + to);
			}
		});
		setDropListener(new DropListener() {
			public void drop(final int from, final int to) {
//				Log.i(TAG, "drop from " + from + " to " + to);
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent ev) {
//		if (mRemoveListener != null && mGestureDetector == null) {
//			if (mRemoveMode == FLING) {
//				mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
//					@Override
//					public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX,
//					                       final float velocityY) {
//						if (mDragView != null) {
//							if (velocityX > 1000) {
//								final Rect r = mTempRect;
//								mDragView.getDrawingRect(r);
//								if (e2.getX() > r.right * 2 / 3) {
//									// fast fling right with release near the right edge of the screen
//									stopDragging();
//									mRemoveListener.remove(mFirstDragPos);
//									unExpandViews(true);
//								}
//							}
//							// flinging while dragging should have no effect
//							return true;
//						}
//						return false;
//					}
//				});
//			}
//		}
		if (mDragListener != null || mDropListener != null) {
			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					final int x = (int) ev.getX();
					final int y = (int) ev.getY();
					final int itemnum = pointToPosition(x, y);
					if (itemnum == AdapterView.INVALID_POSITION) {
						break;
					}
					//ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
					final View item = getChildAt(itemnum - getFirstVisiblePosition());
					mDragPointY = y - item.getTop();
					mDragPointX = x - item.getLeft();
					mCoordOffsetY = ((int) ev.getRawY()) - y;
					mCoordOffsetX = ((int) ev.getRawX()) - x;

					final int[] xy = new int[2];
					item.getLocationOnScreen(xy);
					mDragItemY = xy[1] - mCoordOffsetY / 2;
//					Log.i(TAG, "itemY=" + mDragItemY + ", dragX=" + mDragPointX + ", dragY=" + mDragPointY + ", offX=" + mCoordOffsetX + ", offY=" + mCoordOffsetY);
//					Log.i(TAG, "item top: " + item.getTop());
//                    View dragger = item.findViewById(R.id.icon);
//                    Rect r = mTempRect;
//                    dragger.getDrawingRect(r);
					// The dragger icon itself is quite small, so pretend the touch area is bigger
//                    if (x < r.right * 2) {
					item.setDrawingCacheEnabled(true);
					// Create a copy of the drawing cache so that it does not get recycled
					// by the framework when the list tries to clean up memory
					final Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
					//startDragging(bitmap, x, y);
					mDragPos = itemnum;
					mFirstDragPos = mDragPos;
//					mHeight = getHeight();
//					final int touchSlop = mTouchSlop;
//					mUpperBound = Math.min(y - touchSlop, mHeight / 3);
//					mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
					//item.setVisibility(View.INVISIBLE);
					//return false;
//                    }
//                    mDragView = null;
//                    break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	/*
		 * pointToPosition() doesn't consider invisible views, but we
		 * need to, so implement a slightly different version.
		 */
	private int myPointToPosition(final int x, final int y) {
		final Rect frame = mTempRect;
		final int count = getChildCount();
		for (int i = count - 1; i >= 0; i--) {
			final View child = getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y)) {
				return getFirstVisiblePosition() + i;
			}
		}
		return INVALID_POSITION;
	}

	private int getItemForPosition(final int y) {
		final int adjustedy = y - mDragPointY - 32;
		int pos = myPointToPosition(0, adjustedy);
		if (pos >= 0) {
			if (pos <= mFirstDragPos) {
				pos += 1;
			}
		} else if (adjustedy < 0) {
			pos = 0;
		}
		return pos;
	}

//	private void adjustScrollBounds(final int y) {
//		if (y >= mHeight / 3) {
//			mUpperBound = mHeight / 3;
//		}
//		if (y <= mHeight * 2 / 3) {
//			mLowerBound = mHeight * 2 / 3;
//		}
//	}

	/*
		 * Restore size and visibility for all listitems
		 */
	private void unExpandViews(final boolean deletion) {
		for (int i = 0; ; i++) {
			View v = getChildAt(i);
			if (v == null) {
				if (deletion) {
					// HACK force update of mItemCount
					final int position = getFirstVisiblePosition();
					final int y = getChildAt(0).getTop();
					setAdapter(getAdapter());
					setSelectionFromTop(position, y);
					// end hack
				}
				layoutChildren(); // force children to be recreated where needed
				v = getChildAt(i);
				if (v == null) {
					break;
				}
			}
			final ViewGroup.LayoutParams params = v.getLayoutParams();
			//params.height = mItemHeightNormal;
			params.height = v.getHeight();
			v.setLayoutParams(params);
			v.setVisibility(View.VISIBLE);
		}
	}

	/* Adjust visibility and size to make it appear as though
		 * an item is being dragged around and other items are making
		 * room for it:
		 * If dropping the item would result in it still being in the
		 * same place, then make the dragged listitem's size normal,
		 * but make the item invisible.
		 * Otherwise, if the dragged listitem is still on screen, make
		 * it as small as possible and expand the item below the insert
		 * point.
		 * If the dragged item is not on screen, only expand the item
		 * below the current insertpoint.
		 */
	private void doExpansion() {
//		int childnum = mDragPos - getFirstVisiblePosition();
//		if (mDragPos > mFirstDragPos) {
//			childnum++;
//		}
//
//		final View first = getChildAt(mFirstDragPos - getFirstVisiblePosition());
//
//		for (int i = 0; ; i++) {
//			final View vv = getChildAt(i);
//			if (vv == null) {
//				break;
//			}
//			int height = mItemHeightNormal;
//			int visibility = View.VISIBLE;
//			if (vv.equals(first)) {
//				// processing the item that is being dragged
//				if (mDragPos == mFirstDragPos) {
//					// hovering over the original location
//					visibility = View.INVISIBLE;
//				} else {
//					// not hovering over it
//					height = 1;
//				}
//			} else if (i == childnum) {
//				if (mDragPos < getCount() - 1) {
//					height = mItemHeightExpanded;
//				}
//			}
//			height = vv.getHeight();
//			final ViewGroup.LayoutParams params = vv.getLayoutParams();
//			params.height = height;
//			vv.setLayoutParams(params);
//			vv.setVisibility(visibility);
//		}
	}

	@Override
	public boolean onTouchEvent(final MotionEvent ev) {
//		if (mGestureDetector != null) {
//			mGestureDetector.onTouchEvent(ev);
//		}
		if ((mDragListener != null || mDropListener != null) && mDragView != null) {
			boolean processed = false;
			final int action = ev.getAction();
			switch (action) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					final Rect r = mTempRect;
					mDragView.getDrawingRect(r);
					stopDragging();
					if (mRemoveMode == SLIDE && ev.getX() > r.right * 3 / 4) {
						if (mRemoveListener != null) {
							mRemoveListener.remove(mFirstDragPos);
						}
						unExpandViews(true);
					} else {
						if (mDropListener != null && mDragPos >= 0 && mDragPos < getCount()) {
							mDropListener.drop(mFirstDragPos, mDragPos);
						}
						unExpandViews(false);
					}
					processed = true;
					break;

				//case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					final int x = (int) ev.getX();
					final int y = (int) ev.getY();
					dragView(x, y);

					if (action == MotionEvent.ACTION_MOVE) {
						final View first = getChildAt(mFirstDragPos - getFirstVisiblePosition());
						first.setVisibility(View.INVISIBLE);
					}

					final int itemnum = getItemForPosition(y);
					if (itemnum >= 0) {
						if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
							if (mDragListener != null) {
								mDragListener.drag(mDragPos, itemnum);
							}
							mDragPos = itemnum;
							doExpansion();
						}
//						int speed = 0;
//						adjustScrollBounds(y);
//						if (y > mLowerBound) {
//							// scroll the list up a bit
//							speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
//						} else if (y < mUpperBound) {
//							// scroll the list down a bit
//							speed = y < mUpperBound / 2 ? -16 : -4;
//						}
//						if (speed != 0) {
//							int ref = pointToPosition(0, mHeight / 2);
//							if (ref == AdapterView.INVALID_POSITION) {
//								//we hit a divider or an invisible view, check somewhere else
//								ref = pointToPosition(0, mHeight / 2 + getDividerHeight() + 64);
//							}
//							final View v = getChildAt(ref - getFirstVisiblePosition());
//							if (v != null) {
//								final int pos = v.getTop();
//								setSelectionFromTop(ref, pos - speed);
//							}
//						}
					}
					processed = true;
					break;
			}
			if (processed) {
				Log.i(TAG, "ev: " + ev);
				return true;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void startDragging(final Bitmap bm, final int x, final int y) {
		stopDragging();

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP;
		//mWindowParams.x = 0;
		mWindowParams.x = x - mDragPointX + mCoordOffsetX;
		//mWindowParams.y = y - mDragPointY + mCoordOffsetY;
		mWindowParams.y = mDragItemY;

		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		                      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		                      | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		                      | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		//| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		final Context mContext = getContext();
		final ImageView v = new ImageView(mContext);
		final int backGroundColor = mContext.getResources().getColor(R.color.dragndrop_background);
		v.setBackgroundColor(backGroundColor);
		v.setImageBitmap(bm);
		v.setVisibility(View.VISIBLE);
		mDragBitmap = bm;

		mWindowManager = (WindowManager) mContext.getSystemService("window");
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	private void dragView(final int x, final int y) {
//		if (mRemoveMode == SLIDE) {
//			float alpha = 1.0f;
//			final int width = mDragView.getWidth();
//			if (x > width / 2) {
//				alpha = ((float) (width - x)) / (width / 2);
//			}
//			mWindowParams.alpha = alpha;
//		}
		mWindowParams.x = x - mDragPointX + mCoordOffsetX;
		if (mWindowParams.x < 0)
			mWindowParams.x = 0;
		//mWindowParams.y = y - mDragPointY + mCoordOffsetY;
		//Log.i(TAG, "sub: "+x);
		mWindowParams.y = mDragItemY;
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
	}

	private void stopDragging() {
		if (mDragView != null) {
			final Context mContext = getContext();
			final WindowManager wm = (WindowManager) mContext.getSystemService("window");
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
	}

	public void setDragListener(final DragListener l) {
		mDragListener = l;
	}

	public void setDropListener(final DropListener l) {
		mDropListener = l;
	}

	public void setRemoveListener(final RemoveListener l) {
		mRemoveListener = l;
	}

	public interface DragListener {
		void drag(int from, int to);
	}

	public interface DropListener {
		void drop(int from, int to);
	}

	public interface RemoveListener {
		void remove(int which);
	}
}
