package cn.dennishucd;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;

@SuppressWarnings("serial")
public class FilterManager {

	private static final HashMap<String, PorterDuff.Mode> modeMap = 
		new HashMap<String, PorterDuff.Mode>() {{
		put("darken", PorterDuff.Mode.DARKEN);
		put("lighten", PorterDuff.Mode.LIGHTEN);
		put("multiply", PorterDuff.Mode.MULTIPLY);
		put("screen", PorterDuff.Mode.SCREEN);
		}};

	public class Filter {
		private ColorMatrixColorFilter colorFilter;
		private int[] holoColor;
		private float[] holoPos;
		private PorterDuffXfermode holoMode;
		
		public Filter(FilterParser.FilterInfo filterinfo) {
			
			if(filterinfo.filterMatrix != null) {
				ColorMatrix cm = new ColorMatrix(filterinfo.filterMatrix);
				colorFilter = new ColorMatrixColorFilter(cm);
			}
			
			if(filterinfo.holoColorArray != null) {
				holoColor = filterinfo.holoColorArray.clone();
			}
			
			if(filterinfo.holoPosArray != null) {
				holoPos = filterinfo.holoPosArray.clone();
			}
			
			if(filterinfo.holoModeName != null) {
				holoMode = new PorterDuffXfermode(modeMap.get(filterinfo.holoModeName));
			}
			
		}
		
		public ColorMatrixColorFilter getColorFilter() {
			return colorFilter;
		}
		public int[] getHoloColor() {
			return holoColor;
		}
		public float[] getHoloPos() {
			return holoPos;
		}
		public PorterDuffXfermode getHoloMode() {
			return holoMode;
		}

	}
	
	private Context context;
	private HashMap<String, Filter> filterMap;
	private HashMap<String, Filter> exFilterMap;
	private Filter currFilter;
	private BitmapManager bitmapManager;
		
	public FilterManager(Context context, HashMap<String, FilterParser.FilterInfo> idfilterinfo) {
		this.context = context;
		filterMap = new HashMap<String, Filter>();
		for(String id : idfilterinfo.keySet()) {
			if(idfilterinfo.get(id) != null) {
				Filter filter = new Filter(idfilterinfo.get(id));
				filterMap.put(id, filter);
			}
		}
		exFilterMap = new HashMap<String, Filter>();
		currFilter = null;
		bitmapManager = new BitmapManager();	
	}

	public void setFilter(String id) {
		if(filterMap.containsKey(id)) {
			currFilter = filterMap.get(id);
		}
		else if(exFilterMap.containsKey(id)){
			currFilter = exFilterMap.get(id);
		}
		else {
			currFilter = null;
		}
	}

	public Bitmap getFilterBmp(Bitmap bmp) {
		if(bmp == null)
			return null;
		
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		Canvas canvas = new Canvas(bmp); 
        Paint paint = new Paint();

		if(currFilter != null) {

			if(currFilter.getColorFilter() != null) {
		        paint.setColorFilter(currFilter.getColorFilter());
		        Bitmap bmp1 = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		        Canvas canvas1 = new Canvas(bmp1);
		        canvas1.drawBitmap(bmp, 0, 0, paint);
		        bmp.recycle();
		        bmp = bmp1;
		        canvas = canvas1;
			}

			if(currFilter.getHoloColor() != null) {
				paint.reset();
				if(currFilter.getHoloMode() != null) {
					paint.setXfermode(currFilter.getHoloMode());
				}
				RadialGradient rg = new RadialGradient(
						(float)width / 2, (float)height / 2, (float)height, 
						currFilter.getHoloColor(), currFilter.getHoloPos(), Shader.TileMode.CLAMP);
				paint.setShader(rg);
				canvas.drawRect(0.0f, 0.0f, width, height, paint);
			}

		}	
		return bmp;
	}
	


}
