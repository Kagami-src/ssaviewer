package com.kagami.subplayer;

import java.io.IOException;

import android.app.Application;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class SubApplication extends Application {
	private Uri mMediaFile;
	private MediaPlayer mPlayer;
	
	public void setMidiaUri(Uri uri){
		mMediaFile=uri;
		mPlayer=MediaPlayer.create(this, uri);
	}
	public void play(){
		mPlayer.start();
	}
	public void pause(){
		mPlayer.pause();
	}
	public void stop(){
		mPlayer.stop();
	}
	public void seekTo(long ms){
		mPlayer.stop();
		mPlayer.seekTo((int)ms);
	}

}
