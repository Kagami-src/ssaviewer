package com.kagami.subplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static int REQUESTCODE_SUBFILE = 1;
	public final static int REQUESTCODE_MEDIAFILE = 2;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private SubApplication mSubApplication;
	private Menu mMenu;
	private int mThemeid;
	private TypedArray mThemeAttrs;
	private SublistFragment mSubListFragment;
	private int mExitCount;
	private boolean isFullScreen = false;
	private Uri mSubUri;
	private Uri mAudioUri;
	private List<FavInfo> mFavList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.getInt("theme", -1) != -1) {
				isFullScreen = savedInstanceState.getBoolean("fullscreen",
						false);
				mThemeid = savedInstanceState.getInt("theme");
				this.setTheme(mThemeid);
				fullScreen(isFullScreen);
			}
		}
		mSubApplication = (SubApplication) getApplication();
		mSubApplication.mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
		mThemeAttrs = obtainStyledAttributes(R.styleable.AppTheme);
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null) {
			((TextView) findViewById(R.id.drawer_loadaudio_sub))
					.setText(savedInstanceState
							.getString("drawer_loadaudio_sub"));
			((TextView) findViewById(R.id.drawer_loadsub_sub))
					.setText(savedInstanceState.getString("drawer_loadsub_sub"));
		}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle("cl");
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle("op");
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		final LinearLayout ll = (LinearLayout) findViewById(R.id.left_ll);
		ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		ll.findViewById(R.id.drawer_loadsub).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("*/*");
						intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						startActivityForResult(intent, REQUESTCODE_SUBFILE);
					}
				});
		ll.findViewById(R.id.drawer_loadaudio).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("*/*");
						intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						startActivityForResult(intent, REQUESTCODE_MEDIAFILE);
					}
				});
		ll.findViewById(R.id.changetheme).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mThemeid == R.style.AppTheme_Dark)
							mThemeid = R.style.AppTheme_Light;
						else
							mThemeid = R.style.AppTheme_Dark;
						recreate();

					}
				});
		ll.findViewById(R.id.add_fav).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSubUri == null || mAudioUri == null)
					return;
				FavInfo info = new FavInfo();
				info.subUri = Uri.decode(mSubUri.toString());
				info.audioUri = Uri.decode(mAudioUri.toString());
				info.name = ((TextView) findViewById(R.id.drawer_loadsub_sub))
						.getText().toString()
						+ "\n"
						+ ((TextView) findViewById(R.id.drawer_loadaudio_sub))
								.getText().toString();
				mFavList.add(info);
				saveFavList();
				refFavListView();
			}
		});

		mSubListFragment = (SublistFragment) getFragmentManager()
				.findFragmentById(R.id.viewcontent_fragment);
		initFavList();
		refFavListView();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mMenu = menu;

		if (isFullScreen) {
			MenuItem m = menu.findItem(R.id.fullscreen);
			m.setIcon(mThemeAttrs
					.getDrawable(R.styleable.AppTheme_actionBarOutFullScreen));
		}
		if (mSubApplication.getMediaPlayer() != null
				&& mSubApplication.getMediaPlayer().isPlaying()) {
			MenuItem m = menu.findItem(R.id.media_play);
			m.setIcon(mThemeAttrs
					.getDrawable(R.styleable.AppTheme_actionBarPause));
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null)
			Log.d("kagami", data.getData().toString());
		if (requestCode == REQUESTCODE_SUBFILE && data != null
				&& data.getData().toString().endsWith(".ass")) {
			setSub(data.getData());
			return;
		}
		if (requestCode == REQUESTCODE_MEDIAFILE
				&& data != null
				&& (data.getData().toString().endsWith(".mp4")
						|| data.getData().toString().endsWith(".aac")
						|| data.getData().toString().endsWith(".mp3") || data
						.getData().toString().endsWith(".m4a"))) {
			setAudio(data.getData());
			return;
		}
		if (data != null)
			Toast.makeText(this, R.string.format_error, Toast.LENGTH_SHORT)
					.show();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		switch (id) {
		case R.id.media_play:
			if (mSubApplication.getMediaPlayer() != null
					&& mSubApplication.getMediaPlayer().isPlaying()) {
				pause();
			} else {
				play();
			}
			break;
		// case R.id.location:
		// if (mThemeid == R.style.AppTheme_Dark)
		// mThemeid = R.style.AppTheme_Light;
		// else
		// mThemeid = R.style.AppTheme_Dark;
		// recreate();
		// break;
		case R.id.fullscreen:
			fullScreen(!isFullScreen);
			isFullScreen = !isFullScreen;
			if (isFullScreen)
				item.setIcon(mThemeAttrs
						.getDrawable(R.styleable.AppTheme_actionBarOutFullScreen));
			else
				item.setIcon(mThemeAttrs
						.getDrawable(R.styleable.AppTheme_actionBarFullScreen));
			break;
		case R.id.location:
			/*
			 * mSubListFragment.smoothScrollByTime(mSubApplication
			 * .getMediaPlayer().getCurrentPosition());
			 */
			mSubListFragment
					.setAutoScrollMode(!mSubListFragment.isAutoScrollMode);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void play() {
		try {
			if (!mSubApplication.getMediaPlayer().isPlaying()) {
				mSubApplication.getMediaPlayer().start();
				mMenu.findItem(R.id.media_play)
						.setIcon(
								mThemeAttrs
										.getDrawable(R.styleable.AppTheme_actionBarPause));
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
		}
	}

	public void pause() {
		try {
			if (mSubApplication.getMediaPlayer().isPlaying()) {
				mSubApplication.getMediaPlayer().pause();
				mMenu.findItem(R.id.media_play)
						.setIcon(
								mThemeAttrs
										.getDrawable(R.styleable.AppTheme_actionBarPlay));
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
		}
	}

	public void seekTo(int ms) {
		try {
			mSubApplication.getMediaPlayer().seekTo(ms);
		} catch (Exception e) {
			Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", mThemeid);
		outState.putBoolean("fullscreen", isFullScreen);
		String tmp = ((TextView) findViewById(R.id.drawer_loadsub_sub))
				.getText().toString();
		outState.putString("drawer_loadsub_sub", tmp);
		tmp = ((TextView) findViewById(R.id.drawer_loadaudio_sub)).getText()
				.toString();
		outState.putString("drawer_loadaudio_sub", tmp);
	}

	/**
	 * 
	 * @param b
	 *            :'true' in fullScreen mode and 'false' return from fullScreen
	 *            mode
	 */
	private void fullScreen(boolean b) {
		int fullop = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		if (b) {
			int uiop = getWindow().getDecorView().getSystemUiVisibility();
			int newuiop = uiop | fullop;
			getWindow().getDecorView().setSystemUiVisibility(newuiop);
		} else {
			int uiop = getWindow().getDecorView().getSystemUiVisibility();
			int newuiop = ~fullop & uiop;
			getWindow().getDecorView().setSystemUiVisibility(newuiop);
		}
	}

	@Override
	public void finish() {
		// super.finish();
		switch (mExitCount) {
		case 0:
			Handler h = new Handler();
			h.postDelayed(new Runnable() {

				@Override
				public void run() {
					mExitCount = 0;
				}
			}, 2000);
			Toast.makeText(this, R.string.willexit, Toast.LENGTH_SHORT).show();
			mExitCount++;
			break;
		case 1:
			super.finish();
			mSubApplication.finish();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		fullScreen(isFullScreen);
		// mSubListFragment.setSubFile(Uri.parse("content://com.speedsoftware.explorer.content/storage/emulated/0/Download/rb.ass"));
	}

	public void setSub(Uri uri) {
		mSubListFragment.setSubFile(uri);
		String[] ss = uri.getPath().split("/");
		((TextView) findViewById(R.id.drawer_loadsub_sub))
				.setText(ss[ss.length - 1]);
		mSubUri = uri;
	}

	public void setAudio(Uri uri) {
		mSubApplication.setMidiaUri(uri);
		String[] ss = uri.getPath().split("/");
		((TextView) findViewById(R.id.drawer_loadaudio_sub))
				.setText(ss[ss.length - 1]);
		mAudioUri = uri;
	}

	private void initFavList() {
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		mFavList = new ArrayList<MainActivity.FavInfo>();
		try {
			JSONArray array = new JSONArray(sp.getString("favdata", ""));
			for (int i = 0; i < array.length(); i++) {
				JSONObject ob = array.optJSONObject(i);
				FavInfo tmp = new FavInfo();
				tmp.name = ob.optString("name");
				tmp.audioUri = ob.optString("audio");
				tmp.subUri = ob.optString("sub");
				mFavList.add(tmp);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveFavList() {
		JSONArray array = new JSONArray();
		try {
			for (FavInfo item : mFavList) {
				JSONObject map = new JSONObject();
				map.put("name", item.name);
				map.put("sub", item.subUri);
				map.put("audio", item.audioUri);
				array.put(map);
			}
		} catch (Exception ex) {

		}
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("favdata", array.toString());
		editor.commit();
		Log.d("kagami", "save:" + array.toString());
	}

	private void refFavListView() {
		OnClickListener load = new OnClickListener() {

			@Override
			public void onClick(View v) {
				FavInfo info = (FavInfo) v.getTag();
				refSubAndAudio(Uri.parse(info.subUri), Uri.parse(info.audioUri));
			}
		};
		OnClickListener delet = new OnClickListener() {

			@Override
			public void onClick(View v) {
				mFavList.remove(v.getTag());
				saveFavList();
				refFavListView();
			}
		};
		LinearLayout favcontent = (LinearLayout) findViewById(R.id.fav_content);
		favcontent.removeAllViews();
		for (FavInfo item : mFavList) {
			View view = LayoutInflater.from(this).inflate(R.layout.item_fav,
					null);
			((TextView) view.findViewById(R.id.subname)).setText(item.name);
			view.findViewById(R.id.subname).setTag(item);
			view.findViewById(R.id.subname).setOnClickListener(load);
			view.findViewById(R.id.icon).setTag(item);
			view.findViewById(R.id.icon).setOnClickListener(delet);
			favcontent.addView(view);
		}

	}

	private class FavInfo {
		public String name;
		public String subUri;
		public String audioUri;
	}

	private void refSubAndAudio(Uri sub, Uri audio) {
		if (mSubApplication.getMediaPlayer() != null)
			pause();
		mSubListFragment.setAutoScrollMode(false);
		setSub(sub);
		setAudio(audio);
	}

}
