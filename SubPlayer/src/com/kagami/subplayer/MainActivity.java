package com.kagami.subplayer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
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
	private boolean isStart = false;
	private Menu mMenu;
	private int mThemeid;
	private TypedArray mThemeAttrs;
	private SublistFragment mSubListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.getInt("theme", -1) != -1) {
				mThemeid = savedInstanceState.getInt("theme");
				this.setTheme(mThemeid);
			}
		}
		mThemeAttrs = obtainStyledAttributes(R.styleable.AppTheme);
		setContentView(R.layout.activity_main);
		mSubApplication = (SubApplication) getApplication();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
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
		LinearLayout ll = (LinearLayout) findViewById(R.id.left_drawer);
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

		mSubListFragment = (SublistFragment) getFragmentManager()
				.findFragmentById(R.id.viewcontent_fragment);
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
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE_SUBFILE && data != null
				&& data.getData().toString().endsWith(".ass")) {
			mSubListFragment.setSubFile(data.getData());
			return;
		}
		if (requestCode == REQUESTCODE_MEDIAFILE
				&& data != null
				&& (data.getData().toString().endsWith(".mp4")
						|| data.getData().toString().endsWith(".aac")
						|| data.getData().toString().endsWith(".mp3") || data
						.getData().toString().endsWith(".m4a"))) {
			mSubApplication.setMidiaUri(data.getData());
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
			if (isStart) {
				pause();
			} else {
				play();
			}
			break;
		case R.id.media_pause:
			if (mThemeid == R.style.AppTheme_Dark)
				mThemeid = R.style.AppTheme_Light;
			else
				mThemeid = R.style.AppTheme_Dark;
			recreate();
			break;
		case R.id.location:
			mSubListFragment.smoothScrollByTime(0);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void play() {
		try {
			if (!isStart) {
				mSubApplication.getMediaPlayer().start();
				mMenu.findItem(R.id.media_play)
						.setIcon(
								mThemeAttrs
										.getDrawable(R.styleable.AppTheme_actionBarPause));
				isStart = !isStart;
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
		}
	}

	public void pause() {
		try {
			if (isStart) {
				mSubApplication.getMediaPlayer().pause();
				mMenu.findItem(R.id.media_play)
						.setIcon(
								mThemeAttrs
										.getDrawable(R.styleable.AppTheme_actionBarPlay));
				isStart = !isStart;
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
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		pause();
	}
}
