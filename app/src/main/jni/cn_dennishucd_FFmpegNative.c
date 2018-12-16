#include <math.h>
#include <libavutil/opt.h>
#include <libavcodec/avcodec.h>
#include <libavutil/channel_layout.h>
#include <libavutil/common.h>
#include <libavformat/avformat.h>
#include <libavutil/imgutils.h>
#include <libavutil/mathematics.h>
#include <libavutil/samplefmt.h>
#include <libswscale/swscale.h>
#include <android/bitmap.h>
#include <yuvrgb/yuv2rgb.h>
#include <yuvrgb/yuv2rgb16tab.c>
#include "cn_dennishucd_FFmpegNative.h"

/* ****************************************
 * FFMPEG For Android 视频编解码
 *
 * Version 3.0
 * 改进：汇编和OPENGL实现颜色空间转换
 * 福州大学  李其柄 版权所有
 * ****************************************/

#define  LOG_TAG    "FFMPEGSample"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#ifdef __cplusplus
extern "C" {
#endif

//全局变量

//输入全局变量
AVFormatContext *FmtCtx = NULL;
AVCodecContext *VideoCodecCtx = NULL;
AVCodecContext *AudioCodecCtx = NULL;
AVCodec *VideoCodec = NULL;
AVCodec *AudioCodec = NULL;
int IsOpened = 0;				//已打开标记
int VideoStream = 0;			//视频流编号
int AudioStream = 0;			//音频流编号
int VideoWidth, VideoHeight;	//视频宽高
double FrameRate;				//平均帧率
double SampleRate;				//音频采样率
int Duration = 0;				//时长(毫秒)
AVFrame * FrameYUV = NULL;		//视频帧(YUV)
AVFrame * FrameRGB = NULL;		//视频帧(RGB)
AVFrame * FrameAudio = NULL;	//音频帧
int IsEnd = 0;					//输入结束标记
static struct SwsContext *img_convert_ctx;

//输出全局变量
AVFormatContext *OutFmtCtx = NULL;
AVCodecContext *OutVideoCodecCtx = NULL;
AVOutputFormat *OutFmt;
int OutIsOpened = 0;				//已打开标记
int OutVideoWidth, OutVideoHeight;	//输出视频宽高
AVStream *VideoST;					//输出视频流
AVFrame * OutFrameYUV = NULL;		//视频帧(YUV)
AVFrame * OutFrameRGB = NULL;		//视频帧(RGB)
static struct SwsContext *img_convert_ctx_out;

AVPacket pkt;

static uint8_t *video_outbuf;
static int frame_count, video_outbuf_size;
AVPacket avpkt;
int outbuf_size=100000;
uint8_t * outbuf;

//添加视频流
static AVStream *add_video_stream(AVFormatContext *oc, enum AVCodecID codec_id)
{
    AVCodecContext *c;
    AVStream *st;
    AVCodec *codec;

    st = avformat_new_stream(oc, NULL);
    if (!st) {
        exit(1);
    }
    c = st->codec;

    //查找编码器
    codec = avcodec_find_encoder(codec_id);
    if (!codec) {
        fprintf(stderr, "codec not found\n");
        exit(1);
    }
    avcodec_get_context_defaults3(c, codec);
    c->codec_id = codec_id;

    c->bit_rate = 400000;
    c->width = OutVideoWidth;
    c->height = OutVideoHeight;
    c->time_base.den = 24;
    c->time_base.num = 1;
    c->gop_size = 12;
    c->pix_fmt = VideoCodecCtx->pix_fmt;
    if (c->codec_id == CODEC_ID_MPEG2VIDEO) {
        c->max_b_frames = 2;
    }
    if (c->codec_id == CODEC_ID_MPEG1VIDEO){
        c->mb_decision=2;
    }
    if (oc->oformat->flags & AVFMT_GLOBALHEADER)
        c->flags |= CODEC_FLAG_GLOBAL_HEADER;
    return st;
}


//分配视频帧空间
AVFrame *alloc_picture(enum PixelFormat pix_fmt, int width, int height)
{
    AVFrame *picture;
    uint8_t *picture_buf;
    int size;

    picture = av_frame_alloc();
    if (!picture)
        return NULL;
    size = avpicture_get_size(pix_fmt, width, height);
    picture_buf = (uint8_t *)av_malloc(size);
    if (!picture_buf) {
        av_free(picture);
        return NULL;
    }
    avpicture_fill((AVPicture *)picture, picture_buf,pix_fmt, width, height);
    return picture;
}

static void open_video(AVFormatContext *oc, AVStream *st)
{
    AVCodec *codec;
    AVCodecContext *c;
    c = st->codec;

    //查找解码器
    codec = avcodec_find_encoder(c->codec_id);
    if (!codec) {
        exit(1);
    }

    //打开解码器
    if (avcodec_open2(c, codec, NULL) < 0) {
        exit(1);
    }

    video_outbuf = NULL;
    if (!(oc->oformat->flags & AVFMT_RAWPICTURE)) {
        video_outbuf_size = 200000;
        video_outbuf = av_malloc(video_outbuf_size);
    }
}

//打开视频(输出)
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_OpenOut(JNIEnv *env, jobject jobj, jstring arg0) {
	//return 998;

	OutIsOpened = 0;
    AVOutputFormat *ofmt1 = NULL;
    AVFormatContext *ifmt_ctx1 = NULL, *ofmt_ctx1 = NULL;
    AVPacket pkt;
    const char *in_filename, *out_filename1;

	char *out_filename = (char*) (*env)->GetStringUTFChars(env, arg0, NULL);
    int ret, i;

    av_register_all();

    //输出（Output）
    avformat_alloc_output_context2(&OutFmtCtx, NULL, NULL, out_filename);
    if (!OutFmtCtx) {
        return 1;
    }
//640*480
    OutFmt = OutFmtCtx->oformat;
    for (i = 0; i < FmtCtx->nb_streams; i++) {
    	//根据输入流创建输出流（Create output AVStream according to input AVStream）
    	if(FmtCtx->streams[i]->codec->codec_type ==  AVMEDIA_TYPE_AUDIO){
			AVStream *in_stream = FmtCtx->streams[i];
			AVStream *out_stream = avformat_new_stream(OutFmtCtx, in_stream->codec->codec);
			if (!out_stream) {
				return 2;
			}
			//复制AVCodecContext的设置（Copy the settings of AVCodecContext）
			ret = avcodec_copy_context(out_stream->codec, in_stream->codec);
			out_stream->codec->codec_type == in_stream->codec->codec_type;
			if (ret < 0) {
				return 3;
			}
			out_stream->codec->codec_tag = 0;
			if (OutFmtCtx->oformat->flags & AVFMT_GLOBALHEADER){
				out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
			}
        }
    }

	VideoST = NULL;
	if (OutFmt->video_codec != CODEC_ID_NONE) {
		VideoST = add_video_stream(OutFmtCtx, OutFmt->video_codec);
    }

	 //输出一下格式------------------
	 //av_dump_format(OutFmtCtx, 0, out_filename, 1);
	 //打开输出文件（Open output file）
    if (!(OutFmt->flags & AVFMT_NOFILE)) {
        ret = avio_open(&OutFmtCtx->pb, out_filename, AVIO_FLAG_WRITE);
        if (ret < 0) {
            return 4;
        }
    }
	if(VideoST){
		open_video(OutFmtCtx, VideoST);
	}

	//写文件头（Write file header）
    ret = avformat_write_header(OutFmtCtx, NULL);
    if (ret < 0) {
        return 5;
    }

	img_convert_ctx_out = sws_getContext(VideoWidth,VideoHeight, PIX_FMT_RGB24,VideoWidth,VideoHeight, PIX_FMT_YUV420P, SWS_POINT, NULL, NULL, NULL);

	OutFrameYUV = alloc_picture(PIX_FMT_YUV420P, OutVideoWidth, OutVideoHeight);
	OutFrameRGB = alloc_picture(PIX_FMT_RGB24, OutVideoWidth, OutVideoHeight);

	OutIsOpened = 1;
    return 0;
}

//添加一帧视频(输出)
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_AddFrame(JNIEnv *env, jobject jobj, jstring bitmap) {
//return 0;
	if(!OutIsOpened)
	{
		return 1;
	}

	//初始化
	void* pixels;
	uint8_t * tmp;
	AndroidBitmapInfo info;

	//锁定位图
	if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
		return 2;
	} else if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
		return 3;
	}


	int got_packet_ptr;
	av_init_packet(&avpkt);

	// 6s/4
	int i,tc=0,tt=0;
	tmp = (uint8_t *)pixels;
	for(i=info.height*info.width;i>0;i--)
	{
		OutFrameRGB->data[0][tc] = tmp[tt];
		OutFrameRGB->data[0][tc+1] = tmp[tt+1];
		OutFrameRGB->data[0][tc+2] = tmp[tt+2];
		tc+=3;
		tt+=4;
	}

	// 7s/4
	sws_scale(img_convert_ctx_out,(const uint8_t* const *) OutFrameRGB->data,OutFrameRGB->linesize, 0, VideoHeight, OutFrameYUV->data, OutFrameYUV->linesize);

	// 11s/4
	/*tmp = (uint8_t *)pixels;
	uint8_t Y=0,U=0,V=0,R,G,B;
	for(i=info.height*info.width;i>0;i--)
	{
		R = tmp[tt];
		G = tmp[tt+1];
		B = tmp[tt+2];

		OutFrameRGB->data[0][tc] = tmp[tt];
		OutFrameRGB->data[1][tc+1] = tmp[tt+1];
		OutFrameRGB->data[2][tc+2] = tmp[tt+2];
		Y= 0.299*R + 0.587*G + 0.114*B;
		//U= 0.492*(B- Y);
		//V= 0.877*(R- Y);
		OutFrameYUV->data[0][tc] = Y;
		if(tc%2==0){
		//OutFrameYUV->data[1][tc/2] = U;
		}else{
		//OutFrameYUV->data[2][tc/2] = V;
		}
		tc++;
		tt+=4;
	}*/

	avpkt.data = outbuf;
	avpkt.size = outbuf_size;
	avpkt.stream_index = 1;
	OutFrameYUV->pts = pkt.pts;
	avcodec_encode_video2(VideoST->codec, &avpkt, OutFrameYUV, &got_packet_ptr);

	avpkt.stream_index = VideoST->index;
    avpkt.pts = pkt.pts;
    avpkt.dts = pkt.dts;
    avpkt.duration = pkt.duration;
	if(got_packet_ptr)
	{
		av_write_frame(OutFmtCtx, &avpkt);
	}else{
		return 66;
	}
	//15s/4
	av_free_packet(&pkt);
	av_free_packet(&avpkt);

	//解锁位图
	AndroidBitmap_unlockPixels(env, bitmap);
	return 0;
}

//关闭输出视频
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_CloseOut(JNIEnv *env, jobject jobj) {
	if(OutIsOpened){
		OutIsOpened = 0;
		av_write_trailer(OutFmtCtx);
		if (OutFmtCtx && !(OutFmt->flags & AVFMT_NOFILE)){
			avio_close(OutFmtCtx->pb);
		}
		avformat_free_context(OutFmtCtx);
	}
	return 0;
}

//关闭输入视频
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_Close(JNIEnv *env, jobject jobj) {
	if(IsOpened){
		int i;
		avformat_close_input(&FmtCtx);
		av_free(FrameRGB);
		av_free(FrameYUV);
		av_free(FrameAudio);
		avformat_free_context(FmtCtx);
		IsOpened = 0;
		IsEnd = 1;
	}
	return 0;
}

//将视频帧填充至位图bitmap
void fill_bitmap(AndroidBitmapInfo* info, void *pixels, AVFrame *pFrame) {
	uint8_t *frameLine;

	int yy;
	for (yy = 0; yy < info->height; yy++) {
		uint8_t* line = (uint8_t*) pixels;
		frameLine = (uint8_t *) pFrame->data[0] + (yy * pFrame->linesize[0]);

		int xx;
		for (xx = 0; xx < info->width; xx++) {
			int out_offset = xx * 4;
			int in_offset = xx * 3;
			line[out_offset] = frameLine[in_offset];
			line[out_offset + 1] = frameLine[in_offset + 1];
			line[out_offset + 2] = frameLine[in_offset + 2];
			line[out_offset + 3] = 0;
		}
		pixels = (char*) pixels + info->stride;
	}
}

//打开视频(输入)
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_Open(JNIEnv *env, jobject jobj, jstring arg0) {

	//初始化
	char *filename = (char*) (*env)->GetStringUTFChars(env, arg0, NULL);
	IsOpened = 0;

	//注册复用器、解码器
	av_register_all();

	//打开媒体
	FmtCtx = avformat_alloc_context();
	if (avformat_open_input(&FmtCtx, filename, NULL, NULL) != 0) {
		return 1;
	}
	//查找音视频流
	if (avformat_find_stream_info(FmtCtx, NULL) < 0) {
		return 2;
	}
	int i;
    //av_dump_format(FmtCtx, 0, filename, 0);

	VideoStream = -1;
	AudioStream = -1;
	for (i = 0; i < FmtCtx->nb_streams; i++) {
		if (FmtCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			VideoWidth = FmtCtx->streams[i]->codec->width;
			VideoHeight = FmtCtx->streams[i]->codec->height;
			OutVideoWidth = VideoWidth;
			OutVideoHeight = VideoHeight;
			FrameRate = FmtCtx->streams[i]->avg_frame_rate.num / FmtCtx->streams[i]->avg_frame_rate.den;
			Duration = FmtCtx->duration / 1000;
			VideoStream = i;
		}
		if (FmtCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
			SampleRate = FmtCtx->streams[i]->codec->sample_rate;
			AudioStream = i;
		}
	}
	if (VideoStream == -1) {
		return 3;
	}

	//查找视频解码器
	VideoCodecCtx = FmtCtx->streams[VideoStream]->codec;
	VideoCodec = avcodec_find_decoder(VideoCodecCtx->codec_id);
	if (VideoCodec == NULL) {
		return 4;
	}

	//打开视频编解码器
	AVDictionary *optionsDict = NULL;
	if (avcodec_open2(VideoCodecCtx, VideoCodec, &optionsDict) < 0) {
		return 5;
	}

	//查找音频解码器
	if(AudioStream!=-1){
		AudioCodecCtx = FmtCtx->streams[AudioStream]->codec;
		AudioCodec = avcodec_find_decoder(AudioCodecCtx->codec_id);
		if (AudioCodec == NULL) {
			return 6;
		}

		//打开音频编解码器
		AVDictionary *optionsDict1 = NULL;
		if (avcodec_open2(AudioCodecCtx, AudioCodec, &optionsDict1) < 0) {
			return 7;
		}
	}
	img_convert_ctx = sws_getContext(VideoWidth,VideoHeight, VideoCodecCtx->pix_fmt,VideoWidth,VideoHeight, PIX_FMT_RGB24, SWS_POINT, NULL, NULL, NULL);

	//分配帧空间
	FrameYUV = av_frame_alloc();
	FrameAudio = av_frame_alloc();
	FrameRGB = alloc_picture(PIX_FMT_RGB24, VideoWidth, VideoHeight);

	//设置打开状态
	IsOpened = 1;
	IsEnd = 0;
	return 0;
}

//获取一帧视频
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_GetFrame(JNIEnv *env, jobject jobj, jstring bitmap) {
	//初始化
	void* pixels;
	AndroidBitmapInfo info;

	if(!IsOpened)
	{
		return 1;
	}

	//锁定位图
	if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
		return 2;
	} else if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
		return 3;
	}

	int ret,rett=-100;
    AVStream *in_stream, *out_stream;
    while (1) {
        //获取一个AVPacket（Get an AVPacket）
        ret = av_read_frame(FmtCtx, &pkt);
        if (ret < 0){
        	IsEnd = 1;
        	return -1;
        }
        in_stream  = FmtCtx->streams[pkt.stream_index];
		if(OutIsOpened){
			out_stream = OutFmtCtx->streams[pkt.stream_index];
			pkt.pts = av_rescale_q_rnd(pkt.pts, in_stream->time_base, out_stream->time_base, (AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        	pkt.dts = av_rescale_q_rnd(pkt.dts, in_stream->time_base, out_stream->time_base, (AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        	pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base, out_stream->time_base);
		}
		if(in_stream->codec->codec_type == AVMEDIA_TYPE_VIDEO){
			int frameFinished = 0;
			avcodec_decode_video2(VideoCodecCtx, FrameYUV, &frameFinished,&pkt);
			if(frameFinished)
			{
				if (img_convert_ctx != NULL) {
					/*  汇编实现
						前提：拿到解码以后的AVFrame *frame,frame里面存放解码后的yuv数据。下面是需要用到的变量：
						AVCodecContext *codec_ctx;
						uint8_t    *fill_buffer;
						struct SwsContext     *img_convert_ctx;
						AVFrame   *frame_rgb;
						AVFrame *frame；
						yuv420_2_rgb565(frame_rgb.data[0], frame->data[0], frame->data[1],frame->data[2], codec_ctx->width, codec_ctx->height, frame->linesize[0],frame->linesize[1], codec_ctx->width << 1, yuv2rgb565_table,3);
					*/
					//yuv420_2_rgb888(FrameRGB->data[0], FrameYUV->data[0], FrameYUV->data[1],FrameYUV->data[2], VideoWidth, VideoHeight, FrameYUV->linesize[0],
												//FrameYUV->linesize[1],  VideoWidth << 1, yuv2rgb565_table,3);
					sws_scale(img_convert_ctx,(const uint8_t* const *) FrameYUV->data,FrameYUV->linesize, 0, VideoHeight, FrameRGB->data, FrameRGB->linesize);
					fill_bitmap(&info, pixels, FrameRGB);
					break;
				}
			}
		}
		if(in_stream->codec->codec_type == AVMEDIA_TYPE_AUDIO && pkt.duration>0){
			if(OutIsOpened){
				rett = av_write_frame(OutFmtCtx, &pkt)+pkt.duration;
			}
		}
        av_free_packet(&pkt);
    }

	//解锁位图
	AndroidBitmap_unlockPixels(env, bitmap);
	return rett;
}

//获取视频宽度
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_GetWidth(JNIEnv *env, jobject jobj) {
	if(IsOpened){
		return VideoWidth;
	}else{
		return 0;
	}
}

//获取视频高度
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_GetHeight(JNIEnv *env, jobject jobj) {
	if(IsOpened){
		return VideoHeight;
	}else{
		return 0;
	}
}

//获取视频帧率
JNIEXPORT jdouble JNICALL Java_cn_dennishucd_VideoEngine_GetFrameRate(JNIEnv *env, jobject jobj) {
	if(IsOpened){
		return FrameRate;
	}else{
		return 0;
	}
}

//获取音频采样率
JNIEXPORT jdouble JNICALL Java_cn_dennishucd_VideoEngine_GetSampleRate(JNIEnv *env, jobject jobj) {
	if(IsOpened && AudioStream!=-1){
		return SampleRate;
	}else{
		return 0;
	}
}

//获取视频时长
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_GetDuration(JNIEnv *env, jobject jobj) {
	if(IsOpened){
		return Duration;
	}else{
		return 0;
	}
}

//获取视频时长
JNIEXPORT jint JNICALL Java_cn_dennishucd_VideoEngine_HasAudio(JNIEnv *env, jobject jobj) {
	if(IsOpened && AudioStream!=-1){
		return 1;
	}else{
		return 0;
	}
}

#ifdef __cplusplus
}
#endif
