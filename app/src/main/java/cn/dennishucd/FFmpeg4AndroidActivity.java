package cn.dennishucd;


import java.io.IOException;
import cn.dennishucd.activity.VideoUploadActivity;
import cn.dennishucd.utils.PhotoUtil;
import cn.dennishucd.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;


public class FFmpeg4AndroidActivity extends  Activity implements SurfaceHolder.Callback{
	
	private Button mCancel,mNext;
	private ImageView video_lj,video_mv,video_redo;
	private ImageButton video_mv01,video_mv02,video_mv03,video_mv04;
	private ImageButton video_lj01,video_lj02,video_lj03,video_lj04,video_lj05,video_lj06;
	private HorizontalScrollView video_handle_LJ,video_handle_MV;  
	private ImageView play; 
	private MediaPlayer player;  
    private SurfaceHolder surfaceHolder;
    private SurfaceView surface;
    
	private VideoEngine ffmpeg;
	private VideoFilters filters;    
	private Bitmap bitmap = null;
	private Bitmap bitmapTemp;
	private Bitmap bitmapAltered;
	private Bitmap sBitmap;
	private Bitmap tBitmap;
	private Bitmap rBitmap;
	private int frameIndex = 0;
	private int frameNumber;    
	private String inVideo=Environment.getExternalStorageDirectory().getPath()+"/"+"VID_IN.mp4";
	private String outVideo=Environment.getExternalStorageDirectory().getPath()+"/"+"VID_OUT.mp4";
	private String videoPath = Environment.getExternalStorageDirectory().getPath()+"/"+"VID_IN.mp4";
	private boolean IsFilted = false;
	private boolean IsPlay = false;
    
	@Override        
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);               
		setContentView(R.layout.video_ffmpeg_activity);
		findViewById();
		setListener();
		init();	
	}
	
	private void findViewById(){
		
		mCancel = (Button)findViewById(R.id.video_filter_return);
		mNext = (Button)findViewById(R.id.video_filter_next);
		surface=(SurfaceView)findViewById(R.id.surface);
		video_handle_LJ=(HorizontalScrollView)findViewById(R.id.video_handle_LJ);
		video_handle_MV=(HorizontalScrollView)findViewById(R.id.video_handle_MV);
		video_redo=(ImageView)findViewById(R.id.video_redo);
		video_mv=(ImageView)findViewById(R.id.video_MV);
		video_mv01= (ImageButton)findViewById(R.id.video_MV01);
		video_mv02= (ImageButton)findViewById(R.id.video_MV02);
		video_mv03= (ImageButton)findViewById(R.id.video_MV03);
		video_mv04= (ImageButton)findViewById(R.id.video_MV04);
		video_lj= (ImageView)findViewById(R.id.video_LJ);
		video_lj01=(ImageButton)findViewById(R.id.video_LJ01);
		video_lj02=(ImageButton)findViewById(R.id.video_LJ02);
		video_lj03=(ImageButton)findViewById(R.id.video_LJ03);
		video_lj04=(ImageButton)findViewById(R.id.video_LJ04);
		video_lj05=(ImageButton)findViewById(R.id.video_LJ05);
		video_lj06=(ImageButton)findViewById(R.id.video_LJ06);	
		play=(ImageView)findViewById(R.id.video_start);                       
        video_handle_MV.setVisibility(View.VISIBLE);
		video_handle_LJ.setVisibility(View.GONE);
	}
	
	private void setListener(){
		
		mCancel.setOnClickListener(new OnClickListener(){           
            @Override           
            public void onClick(View v) {     
            	finish();          
            }});        
		mNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String filePath = null;
				if(IsFilted ==true){
					 filePath = Environment.getExternalStorageDirectory()+"/VID_OUT.mp4";
				}
				else{
					filePath = Environment.getExternalStorageDirectory()+"/VID_IN.mp4";
				}
				Bitmap bitmap = PhotoUtil.getVideoThumbnail(filePath,480,480, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
				PhotoUtil.saveMyBitmap(bitmap, Environment.getExternalStorageDirectory()+"/"+"a.jpg");
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("path",filePath);
				intent.putExtras(bundle);
				intent.setClass(FFmpeg4AndroidActivity.this, VideoUploadActivity.class);
				startActivity(intent);
			}});
		
		  surface.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(IsPlay == true){
						player.pause();
						IsPlay = false;
						play.setVisibility(View.VISIBLE);
					}
				}		 
		    });
		  
		 play.setOnClickListener(new OnClickListener(){           
             @Override           
             public void onClick(View v) {     
            	 player.start(); 
            	 IsPlay = true;
            	 play.setVisibility(View.GONE);
             }});              
		 
	   video_mv.setOnClickListener(new OnClickListener(){
            
			@Override
			public void onClick(View v) {
				video_handle_MV.setVisibility(View.VISIBLE);
				video_handle_LJ.setVisibility(View.GONE);			
			}         
		});   
	   
	   video_lj.setOnClickListener(new OnClickListener(){
           
			@Override
			public void onClick(View v) {
				video_handle_MV.setVisibility(View.GONE);
				video_handle_LJ.setVisibility(View.VISIBLE);			
			}         
		});   
      
	   video_redo.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = false;
				Video_redo();
			}         
		}); 
	   
	   video_lj01.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				LJ_Lomo(inVideo,outVideo);
			}         
		}); 
	   
	   video_lj02.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				LJ_lightFilter(inVideo,outVideo);
			}         
		}); 
	   
	   video_lj03.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				LJ_fashionFilter(inVideo,outVideo);
			}         
		}); 
	   
	   video_lj04.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				IsFilted = true;
				LJ_oldFilter(inVideo,outVideo);
			}         
		}); 
	   
	   
	   video_lj05.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				LJ_blackWhiteFilter(inVideo,outVideo);
			}         
		}); 
	   
	   video_lj06.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				LJ_fireflyFilter(inVideo,outVideo);
			}         
		}); 

	   video_mv01.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted =false;
			}         
		});   
	   
       video_mv01.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				MV_hexogon_pattern(inVideo,outVideo);
			}         
		});   
	   
       video_mv02.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				MV_flower_surroud(inVideo,outVideo);
			}         
		});   
	   
       video_mv03.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				MV_black_skip(inVideo,outVideo);
			}         
		});   
	   
       video_mv04.setOnClickListener(new OnClickListener(){         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IsFilted = true;
				MV_butterfly_flying(inVideo,outVideo);
			}         
		});                    
	}
	
	private void init(){
		
		Utils.ActivityList.add(this);
		filters = new VideoFilters(this);
		ffmpeg = new VideoEngine();
		int codecID = 28;           
		surfaceHolder=surface.getHolder();//SurfaceHolder是SurfaceView的控制接口        
	    surfaceHolder.addCallback(this); //实现了SurfaceHolder.Callback接口，回调参数直接this  
	    surfaceHolder.setFixedSize(640, 480);//显示的分辨率,不设置为视频默认         
	    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//Surface类型  
	}
	
	private void Video_redo(){
		if(IsFilted ==true){
			videoPath = Environment.getExternalStorageDirectory().getPath()+"/"+"VID_OUT.mp4";
		}
		else{
			videoPath = Environment.getExternalStorageDirectory().getPath()+"/"+"VID_IN.mp4";
		}
		if(player.isPlaying()){
	       	 player.stop();
	     }
	     player.reset();
	     player.setAudioStreamType(AudioManager.STREAM_MUSIC);    
		 player.setDisplay(surfaceHolder); 
		 player.setLooping(true);
		  try {
		    	 player.setDataSource(videoPath);
		    	 player.prepare();    
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		} 
	     play.setVisibility(View.VISIBLE);
	}
	
	//处理视频LJ  萤光
	private void LJ_fireflyFilter(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
		     if(ret==-1)
		     {  
		     	Close();
		     	return;
		     } 	     

		     
			Paint paint1 = new Paint(); 
			Canvas canvas = new Canvas(bitmapTemp);
			canvas.drawARGB(0xFF, 0,0,0);//分辨率
			canvas.drawBitmap(bitmap, 0, 0, paint1);	
			
		    int width = bitmapTemp.getWidth();
		    int height = bitmapTemp.getHeight();
		    int[] buf = new int[width * height];
		    ImageEngine imageEngine = new ImageEngine();
		    Math.pow(2, 1);
		    bitmapTemp.getPixels(buf, 0, width, 1, 1, width - 1, height - 1);
		    int[] result = imageEngine.toFangdajing(buf, width, height, width / 2, height / 2, 100, 2);
 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapAltered);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率			    
			canvas111.drawBitmap(result, 0, width, 0, 0, width, height, false, paint);
			result = null;
				
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;  
		} 
		Close();
	}
	
	//处理视频LJ  黑白
	private void LJ_blackWhiteFilter(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			 int ret = ffmpeg.GetFrame(bitmap);     
		     if(ret==-1)
		     {  
		     	Close();
		     	return;
		     } 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);
		    Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);
			bitmapTemp = filters.blackWhiteFilter(bitmapTemp);
			canvas.drawBitmap( bitmapTemp, 0, 0, paint);  
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;  
		} 
		Close();
	}

	//处理视频LJ  怀旧
	private void LJ_oldFilter(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
		    if(ret==-1)
		     {  
		     	Close();
		     	return;
		     } 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);
		    Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);
			bitmapTemp = filters.oldFilter(bitmapTemp);
		    canvas.drawBitmap( bitmapTemp, 0, 0, paint);  
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;  
		} 
		Close();
	}
	
	//处理视频LJ  时尚
	private void LJ_fashionFilter(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
		     if(ret==-1)
		     {  
		     	Close();
		     	return;
		     } 
		     Paint paint = new Paint(); 
		     Canvas canvas111 = new Canvas(bitmapTemp);
			 canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			 canvas111.drawBitmap(bitmap, 0, 0, paint);
			 Canvas canvas = new Canvas(bitmapAltered);
			 canvas.drawARGB(0xFF, 0,0,0);
			 bitmapTemp = filters.fashionFilter(bitmapTemp);
			 canvas.drawBitmap( bitmapTemp, 0, 0, paint);  
			 ffmpeg.AddFrame(bitmapAltered);
			 frameIndex ++;  
		} 
		Close();
	}
	
	//处理视频LJ  经典光照
	private void LJ_lightFilter(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
			if(ret==-1){  
				Close();
				return;
			} 
			Paint paint = new Paint(); 	
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);                    
			Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);
			bitmapTemp = filters.lightFilter(bitmapTemp);
	        canvas.drawBitmap( bitmapTemp, 0, 0, paint);  
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;  
		} 
		Close();
	}
	
	private void LJ_Lomo(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
		     if(ret==-1)
		     {  
		     	Close();
		     	return;
		     } 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);
			Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);
			bitmapTemp = filters.lomoFilter(bitmapTemp);
			canvas.drawBitmap( bitmapTemp, 0, 0, paint);  
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;  
		} 
		Close();
	}
	

	//处理MV_01  hexogon
	private void MV_hexogon_pattern(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
	        if(ret==-1)
	        {  
	        	Close();
	        	return;
	        } 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);                          
			Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);                
			bitmapTemp = filters.lomoFilter(bitmapTemp);
		    sBitmap=filters.getpicture_hexogon(frameIndex+1,frameNumber);
		    if(frameIndex>230)  
		    	tBitmap=filters.getlogo(frameIndex,frameNumber);
	        canvas.drawBitmap( bitmapTemp, 0, 0, paint);
	        canvas.drawBitmap( sBitmap, 0, 0, paint);
	        if(frameIndex>230)
	        canvas.drawBitmap( tBitmap, 180, 180, paint);	 
	        paint.reset(); 	       
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;
		} 
		Close();
	}
	
	
	//处理MV_02  flower
	private void MV_flower_surroud(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
	        if(ret==-1)
	        {  
	        	Close();
	        	return;
	        } 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);                       
			Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);
			if(frameIndex<=frameNumber-10){
			sBitmap=filters.getpicture_flower(frameIndex,frameNumber,0);
		    rBitmap=filters.getpicture_flower(frameIndex,frameNumber,1);}
		    if(frameIndex>frameNumber-10)  
		    	tBitmap=filters.getlogo(frameIndex,frameNumber);
	       canvas.drawBitmap( bitmapTemp, 0, 0, paint);
	       if(frameIndex<=frameNumber-10){
	       canvas.drawBitmap( sBitmap, 470, 0, paint);
	       canvas.drawBitmap( rBitmap, 0, 195, paint);}
	       if(frameIndex>frameNumber-10)
	           canvas.drawBitmap( tBitmap, 180, 180, paint);
	        ffmpeg.AddFrame(bitmapAltered);
		    frameIndex ++;  
		} 
		Close();
	}
	
	//处理MV_03  black
	private void MV_black_skip(String in,String out){
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
	        if(ret==-1){  
	        	Close();
	        	return;
	        } 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);                        
			Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);		                   
			bitmapTemp = filters.blackWhiteFilter(bitmapTemp);
		    sBitmap=filters.getpicture_black(frameIndex);
		    if(frameIndex>frameNumber-10)  
		    	tBitmap=filters.getlogo(frameIndex,frameNumber);
	       canvas.drawBitmap( bitmapTemp, 0, 0, paint);
	       canvas.drawBitmap( sBitmap, 0, 0, paint);
	       if(frameIndex>frameNumber-10)
	       canvas.drawBitmap( tBitmap, 180, 180, paint);
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;  
		} 
		Close();
	}
	
    //处理MV_04 butterfly
	private void MV_butterfly_flying(String in,String out) {
		frameNumber=Open(in,out);
		for(int i=0;i<frameNumber;i++){
			int ret = ffmpeg.GetFrame(bitmap);     
	        if(ret==-1)
	        {  
	        	Close();
	        	return;
	        } 
			Paint paint = new Paint(); 
			Canvas canvas111 = new Canvas(bitmapTemp);
			canvas111.drawARGB(0xFF, 0,0,0);//分辨率
			canvas111.drawBitmap(bitmap, 0, 0, paint);                        
			Canvas canvas = new Canvas(bitmapAltered);
			canvas.drawARGB(0xFF, 0,0,0);
			bitmapTemp = filters.oldFilter(bitmapTemp);		
			sBitmap= filters.getpicture_butterfly(frameIndex);
		    if(frameIndex>frameNumber-10)  
			    	tBitmap=filters.getlogo(frameIndex,frameNumber);
	       canvas.drawBitmap( bitmapTemp, 0, 0, paint);
	       canvas.drawBitmap( sBitmap,0, 0, paint);
	       if(frameIndex>frameNumber-10)
	           canvas.drawBitmap( tBitmap, 180, 180, paint);
			ffmpeg.AddFrame(bitmapAltered);
			frameIndex ++;  
		} 
		Close();
	}
	
	
	protected int Open(String in,String out)
	{		       
		frameIndex = 1;
		int ret = ffmpeg.Open(in);
		ffmpeg.OpenOut(out);	
		bitmap = Bitmap.createBitmap(ffmpeg.GetWidth(), ffmpeg.GetHeight() , Config.ARGB_8888); 
		bitmapTemp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig()); 
		bitmapAltered = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig()); 
		int i=ffmpeg.GetDuration()/1000;
		int j=(int) ffmpeg.GetFrameRate();
		return i*j ;
	}
	
	protected void Close()
	{		
		ffmpeg.Close();
		ffmpeg.CloseOut();
		Video_redo();
	}
	
	
	 public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {    
     }     
	 @Override     
	 public void surfaceCreated(SurfaceHolder arg0) { 
		  player=new MediaPlayer();        
		  player.setAudioStreamType(AudioManager.STREAM_MUSIC);    
		  player.setDisplay(surfaceHolder); 
		  player.setLooping(true);
		  try {
		    	 player.setDataSource(videoPath);
		    	 player.prepare();    
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} 
     }    
     
    @Override     
    public void surfaceDestroyed(SurfaceHolder arg0) {      
       // TODO Auto-generated method stub 
       if(player.isPlaying()){
       	  player.stop();
        }
        player.release();  
    	play.setVisibility(View.VISIBLE);
     }
 
   //处理视频
	protected void GetFrame() throws IOException
	{      

		//ffmpeg.GetFrame(bitmap);  
		//imageView1.setImageBitmap(bitmap); 
		
		//读到末尾结束
		int ret = ffmpeg.GetFrame(bitmap);     
     if(ret==-1)
     {  
     	Close();
     	return;
     } 
     
     //处理视频
		Paint paint = new Paint(); 
		
		Canvas canvas111 = new Canvas(bitmapTemp);
		canvas111.drawARGB(0xFF, 0,0,0);//分辨率
		canvas111.drawBitmap(bitmap, 0, 0, paint);
		              
		//新建页面                            
		Canvas canvas = new Canvas(bitmapAltered);
		canvas.drawARGB(0xFF, 0,0,0);
		                   
		  
//  paint.setColor(Color.GRAY);
	
		bitmapTemp = filters.oldFilter(bitmapTemp);
//		
//		sBitmap=getpicture_butterfly(frameIndex);
//	    
//	    if(frameIndex>frameNumber-10)  
//	    	tBitmap=getlogo(frameIndex,frameNumber);
	 // bitmapTemp=ImageUtil.doodle(Filters.featherFilter(bitmapTemp), sBitmap);
		
	    // paint.setAlpha(0x50);//通道、颜色、分辨率设置
    canvas.drawBitmap( bitmapTemp, 0, 0, paint);
    
//    canvas.drawBitmap( sBitmap,0, 0, paint);
//    if(frameIndex>frameNumber-10)
//        canvas.drawBitmap( tBitmap, 180, 180, paint);
    
   // paint.setAlpha(0xE0); 
  
    
   // Bitmap tmpBitmap = new IceFilter(bitmapTemp).imageProcess().getDstBitmap();
   //Bitmap tmpBitmap = new SoftGlowFilter(bitmapTemp, 10, 0.1f, 0.1f).imageProcess().getDstBitmap();
    
   //canvas.drawBitmap( ImageTool.FeatherFilter(bitmapTemp), 0, 0, paint);
   //canvas.drawBitmap( ImageUtil.oldRemeber(ImageUtil.zoomBitmap(bitmapTemp,240,180)), 0, 0, paint);
    //缩放图片并放入离顶（0）高（0）的canvas中
   /*
    paint.reset(); 
    
    //字处理
    paint.setColor( Color.WHITE);
    paint.setTypeface(Typeface.SERIF);
    paint.setTextSize( 35);   
    canvas.drawText("三寸天堂 - 视频特效DEMO", 100+frameIndex, 40, paint);
	*/
		//ffmpeg.AddFrame(bitmapAltered); 
		//tv.setText("GetFrame Success! " + frameIndex);
		  
		ffmpeg.AddFrame(bitmapAltered);
		frameIndex ++;  
	}

}
