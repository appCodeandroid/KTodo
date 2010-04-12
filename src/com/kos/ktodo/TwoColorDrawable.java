package com.kos.ktodo;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class TwoColorDrawable extends Drawable {
	private TwoColorState s;
	private final Paint p = new Paint();
	private int percent;

	public TwoColorDrawable() {
		this(null);
	}

	public TwoColorDrawable(final TwoColorState s) {
		this.s = new TwoColorState(s);
		p.setStyle(Paint.Style.FILL);
	}

	public TwoColorDrawable(final int c1, final int c2) {
		this.s = new TwoColorState(c1, c2);
	}

	public void setPercent(final int percent) {
		this.percent = percent;
	}

	@Override
	public void draw(final Canvas canvas) {
		if (percent == 100) {
			canvas.drawColor(s.c2);
		} else if (percent == 0) {
			canvas.drawColor(s.c1);
		} else {
			final int height = canvas.getHeight();
			final int width = canvas.getWidth();
			final int d = (width * percent) / 100;
			p.setColor(s.c2);
			canvas.drawRect(0, 0, d, height, p);
			p.setColor(s.c1);
			canvas.drawRect(d, 0, width, height, p);
		}
	}

	@Override
	public void setAlpha(final int alpha) {
	}

	@Override
	public void setColorFilter(final ColorFilter cf) {
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
	public ConstantState getConstantState() {
		s.changingConf = super.getChangingConfigurations();
		return s;
	}

//	@Override
//	public void inflate(final Resources r, final XmlPullParser parser, final AttributeSet attrs) throws XmlPullParserException, IOException {
//		super.inflate(r, parser, attrs);
//
//		final TypedArray a = r.obtainAttributes(attrs, R.styleable.TwoColorDrawable);
//		s.c1 = a.getColor(R.styleable.TwoColorDrawable_color1, Color.BLACK);
//		s.c2 = a.getColor(R.styleable.TwoColorDrawable_color1, Color.GRAY);
//
//		a.recycle();
//	}

	final class TwoColorState extends ConstantState {
		int c1, c2;
		int changingConf;

		TwoColorState(final int c1, final int c2) {
			this.c1 = c1;
			this.c2 = c2;
		}

		TwoColorState(final TwoColorState s) {
			if (s != null) {
				c1 = s.c1;
				c2 = s.c2;
			}
		}

		@Override
		public int getChangingConfigurations() {
			return changingConf;
		}

		@Override
		public Drawable newDrawable() {
			return new TwoColorDrawable(this);
		}
	}
}