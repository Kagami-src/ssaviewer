package com.kagami.subplayer;

import java.io.IOException;
import java.util.List;

import com.kagami.subplayer.AssParser.Dialogue;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.TimedText;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class SubApplication extends Application {
	//private Uri mMediaFile;
	private MediaPlayer mPlayer;
	public List<Dialogue> mDialogueList;
	public SharedPreferences mSharedPreferences;
	@SuppressLint("NewApi")
	public void setMidiaUri(Uri uri){
		//mMediaFile=uri;
		if(mPlayer!=null)
			mPlayer.release();
		mPlayer=MediaPlayer.create(this, uri);
		
	
	}
	public MediaPlayer getMediaPlayer(){
		return mPlayer;
	}
	
	public void finish(){
		if(mPlayer!=null)
			mPlayer.release();
		mPlayer=null;
		mDialogueList=null;
	}
	

}
