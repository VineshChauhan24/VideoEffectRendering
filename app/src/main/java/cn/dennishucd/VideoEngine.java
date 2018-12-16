package cn.dennishucd;

import android.graphics.Bitmap;
import android.widget.Button;
 
public class VideoEngine {
	static { 
		System.loadLibrary("avutil-52");
		System.loadLibrary("avcodec-55");
		System.loadLibrary("swresample-0");
		System.loadLibrary("avformat-55");
		System.loadLibrary("swscale-2");
		System.loadLibrary("avfilter-4");
		System.loadLibrary("avdevice-55");
		System.loadLibrary("ffmpeg_codec");
	}

	//从输入视频中获取帧
	public native int GetFrame(Bitmap bitmap);
	 
	//向输出视频写入帧
	public native int AddFrame(Bitmap bitmap);
	
	//打开输入视频
	public native int Open(String filePath);
	
	//打开输出视频
	public native int OpenOut(String filePath);
	
	//关闭输出视频
	public native int CloseOut(); 
	
	//关闭输入视频
	public native int Close(); 
	
	//获取视频宽度
	public native int GetWidth();
	
	//获取视频高度
	public native int GetHeight();
	
	//获取视频平均帧率
	public native double GetFrameRate();
	
	//获取音频采样率
	public native double GetSampleRate();
	
	//获取视频时长(毫秒)
	public native int GetDuration();  
	
	//检测视频是否包含音轨
	public native int HasAudio();
}
