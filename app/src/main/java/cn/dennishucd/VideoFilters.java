package cn.dennishucd;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;


public class VideoFilters {

	FilterParser filterParser;
	FilterManager filterManager;
	private Context mContext;
	public VideoFilters(Context context)
	{
		 filterParser = new FilterParser(context, "filter");
		 filterManager = new FilterManager(context, filterParser.getIdFilterInfo());
		 mContext = context;
	}
	
	//Lemo
	public Bitmap lomoFilter(Bitmap srcBitmap)
	{
		filterManager.setFilter("filter001");
		return filterManager.getFilterBmp(srcBitmap);
	}
	
	//经典光照
	public Bitmap lightFilter(Bitmap srcBitmap)
	{
		filterManager.setFilter("filter002");
		return filterManager.getFilterBmp(srcBitmap);
	}
	
	//时尚
	public Bitmap fashionFilter(Bitmap srcBitmap)
	{
		filterManager.setFilter("filter003");
		return filterManager.getFilterBmp(srcBitmap);
	}
	
	//老照片
	public Bitmap oldFilter(Bitmap srcBitmap)
	{
		filterManager.setFilter("filter004");
		return filterManager.getFilterBmp(srcBitmap);
	}
	
	//黑白
	public Bitmap blackWhiteFilter(Bitmap srcBitmap)
	{
		filterManager.setFilter("filter005");
		return filterManager.getFilterBmp(srcBitmap);
	}
	
	//萤火虫
	public Bitmap fireflyFilter(Bitmap srcBitmap)
	{
		//filterManager.setFilter("filter006");
		//return filterManager.getFilterBmp(srcBitmap);
		
		if(srcBitmap == null)
			return null;
		
		int width = srcBitmap.getWidth();
		int height = srcBitmap.getHeight();

		Bitmap bmp1 = srcBitmap.copy(Bitmap.Config.ARGB_8888, true);

		srcBitmap.recycle();
			    
	    ImageEngine imageEngine = new ImageEngine();
	    int[] buf = new int[width * height];
	    bmp1.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
	    int[] result = imageEngine.toHeibai(buf, width, height);
	    bmp1 = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
	    result = null;

        
        srcBitmap = bmp1;
        
	    return srcBitmap;
	}
	
	
	/*
    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toGray(buf, width, height);
    bitmap = Bitmap.createBitmap(result, width, height, Config.RGB_565);
    result = null;

    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toFudiao(buf, width, height);
    bitmap = Bitmap.createBitmap(result, width, height, Config.RGB_565);
    result = null;


    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toMohu(buf, width, height, 10);
    bitmap = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
    result = null;

    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toHeibai(buf, width, height);
    bitmap = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
    result = null;


    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toDipian(buf, width, height);
    bitmap = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
    result = null;

    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    Math.pow(2, 1);
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toSunshine(buf, width, height, 100, 100, 20, 150);
    bitmap = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
    result = null;

    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    Math.pow(2, 1);
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toFangdajing(buf, width, height, width / 2, height / 2, 100, 2);
    bitmap = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
    result = null;


    ImageEngine imageEngine = new ImageEngine();
    Bitmap bitmap = new Bitmap();
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] buf = new int[width * height];
    Math.pow(2, 1);
    bitmap.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
    int[] result = imageEngine.toHahajing(buf, width, height, width / 2, height / 2, 100, 2);
    bitmap = Bitmap.createBitmap(result, width, height, Config.ARGB_8888);
    result = null;

*/
	
	//水印
	public Bitmap getlogo(int number,int framenumber){
		String picture_number= Integer.toString(number);
		number=number+10-framenumber;
		Resources res=mContext.getResources();
		int id= res.getIdentifier("water"+number,"drawable","cn.dennishucd");
		Bitmap bitmap=BitmapFactory.decodeResource(res, id);
		return bitmap;
	}	

	//摩登时代
	public Bitmap getpicture_black(int number)
	{
		String picture_number= Integer.toString(number);
		if(number<54)
		{
			if(number<27)
				number=number/3+1;
			else
				number=18-number/3;
		}
		else if(number>=54&&number<72)
		{
			if(number<64)
			{
				number=number/2-17;
			}
			else{
				number=28-(number/2-17);
			}
		}
		else{
			number=11;
		}
		Resources res=mContext.getResources();
		int id= res.getIdentifier("black"+number,"drawable","cn.dennishucd");
		Bitmap bitmap=BitmapFactory.decodeResource(res, id);
		return bitmap;
	}
	//格子世界
	public Bitmap getpicture_hexogon(int number,int framenumber)  
	{
		String picture_number= Integer.toString(number);
		if(number<=62) number=number/2;	
		else if(number>62&&number<framenumber-44) number=32;
		//else if(number>=framenumber-44&&number<framenumber-10) number=(number-framenumber+44)/2+framenumber-135;
		else if(number>=framenumber-10) number=1;
		else number=32;
		Resources res=mContext.getResources();
		int id= res.getIdentifier("hexogon_pattern_"+number,"drawable","cn.dennishucd");
		Bitmap bitmap=BitmapFactory.decodeResource(res, id);
		return bitmap;
	}

	//蔓藤花
	public Bitmap getpicture_flower(int number,int framenumber,int i) {
		String picture_number= Integer.toString(number);
		if(framenumber/24>=7)
		{
			if(number<78)
			{
				number=number/2;
			}
			else if(number>76&&number<framenumber-86)
				number=38;
			else if(number>=framenumber-86)
				number=framenumber/2-5-number/2;
		}
		if(i==0){
			Resources res=mContext.getResources();
			int id= res.getIdentifier("flower_"+number,"drawable","cn.dennishucd");
			Bitmap bitmap=BitmapFactory.decodeResource(res, id);
			return bitmap;
		}
		else{
			Resources res=mContext.getResources();
			int id= res.getIdentifier("rflower_"+number,"drawable","cn.dennishucd");
			Bitmap bitmap=BitmapFactory.decodeResource(res, id);
			return bitmap;
		}
	 }
	
	//彩蝶飞舞
	public Bitmap getpicture_butterfly(int number) {
		String picture_number= Integer.toString(number);
		number=number/2;
		number=number%56;
		if(number==0) number=56;
		Resources res=mContext.getResources();
		int id= res.getIdentifier("butterfly_"+number,"drawable","cn.dennishucd");
		Bitmap bitmap=BitmapFactory.decodeResource(res, id);
		return bitmap;
	}
	
     /*
      * 
		//摩登时代
		public Bitmap getpicture_black(int number)
		{
			String picture_number= Integer.toString(number);
			if(number<54)
			{
				if(number<27)
					number=number/3+1;
				else
					number=18-number/3;
			}
			else if(number>=54&&number<72)
			{
				if(number<64)
				{
					number=number/2-17;
				}
				else{
					number=28-(number/2-17);
				}
			}
			else{
				number=11;
			}
			InputStream inStream;
			try {
				inStream = mContext.getAssets().open("MV/black_skip/black"+number+".png");
				 Resources res=mContext.getResources();
				 int id= res.getIdentifier("black"+number,"drawable","cn.dennishucd");
				Bitmap bitmap=BitmapFactory.decodeResource(res, id);
				//Bitmap bitmap=BitmapFactory.decodeStream(inStream);
				return bitmap;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return null;
		}
		//格子世界
		public Bitmap getpicture_hexogon(int number,int framenumber)  
		{
			String picture_number= Integer.toString(number);
			if(number<=62) number=number/2;	
			else if(number>62&&number<framenumber-44) number=32;
			//else if(number>=framenumber-44&&number<framenumber-10) number=(number-framenumber+44)/2+framenumber-135;
			else if(number>=framenumber-10) number=1;
			else number=32;
			InputStream inStream;
			try {
				inStream = mContext.getAssets().open("MV/hexogon_pattern/hexogon_pattern_"+number+".png");
				Bitmap bitmap=BitmapFactory.decodeStream(inStream);
				return bitmap;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		//蔓藤花
		public Bitmap getpicture_flower(int number,int framenumber,int i) {
			String picture_number= Integer.toString(number);
			if(framenumber/24>=7)
			{
				if(number<78)
				{
					number=number/2;
				}
				else if(number>76&&number<framenumber-86)
					number=38;
				else if(number>=framenumber-86)
					number=framenumber/2-5-number/2;
			}
			try {
				 InputStream inStream = null;
				 if(i==0) {
					inStream = mContext.getAssets().open("MV/flower_surroud/flower_"+number+".png");
				}
				else{ 
					inStream= mContext.getAssets().open("MV/flower_surroud/rflower_"+number+".png");
				}
				Bitmap bitmap=BitmapFactory.decodeStream(inStream);
				return bitmap;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;		   
		 }
		//彩蝶飞舞
		public Bitmap getpicture_butterfly(int number) {
			String picture_number= Integer.toString(number);
			number=number/2;
			number=number%56;
			if(number==0) number=56;
			InputStream inStream;
			try {
				inStream = mContext.getAssets().open("MV/butterfly_flying/butterfly_"+number+".png");
				Bitmap bitmap=BitmapFactory.decodeStream(inStream);
				return bitmap;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
			//水印
	public Bitmap getlogo(int number,int framenumber){
		String picture_number= Integer.toString(number);
		number=number+10-framenumber;
		InputStream inStream;
		try {
			inStream = mContext.getAssets().open("MV/logo_water/water"+number+".png");
			Bitmap bitmap=BitmapFactory.decodeStream(inStream);
			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
		*/
}
