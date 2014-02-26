package com.kagami.subplayer;

import java.io.IOException;
import java.util.List;

import com.kagami.subplayer.AssParser.Dialogue;

import android.app.Application;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.util.Log;

public class SubApplication extends Application {
	private Uri mMediaFile;
	private MediaPlayer mPlayer;
	public List<Dialogue> mDialogueList;
	
	public void setMidiaUri(Uri uri){
		mMediaFile=uri;
		mPlayer=MediaPlayer.create(this, uri);
	
	}
	public MediaPlayer getMediaPlayer(){
		return mPlayer;
	}

}
