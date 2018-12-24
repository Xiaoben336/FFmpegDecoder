package com.example.zjf.ffmpegdecoder;

public class FFmpeg {
	static {
		System.loadLibrary("avutil-55");
		System.loadLibrary("avcodec-57");
		System.loadLibrary("avformat-57");
		System.loadLibrary("avdevice-57");
		System.loadLibrary("swresample-2");
		System.loadLibrary("swscale-4");
		//System.loadLibrary("postproc-54");
		System.loadLibrary("avfilter-6");
	}

	/**
	 *
	 * @param inputurl
	 * @param outputurl
	 * @return
	 */
	public static native int decode(String inputurl, String outputurl);

	/**
	 * avi解码后SurfaceView显示
	 * @param filepath
	 * @param surface
	 * @return
	 */
	public static native int play(String filepath,Object surface);
}
