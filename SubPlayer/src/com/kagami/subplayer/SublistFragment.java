package com.kagami.subplayer;

import java.util.List;

import com.kagami.subplayer.AssParser.Dialogue;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SublistFragment extends Fragment {

	private ListView mListView;
	private SubApplication mSubApplication;
	private int mCurrentPosition = -1;
	public boolean isAutoScrollMode = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mSubApplication = (SubApplication) getActivity().getApplication();
		View v = inflater.inflate(R.layout.fragment_sublist, container, false);
		mListView = (ListView) v.findViewById(R.id.listView1);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((MainActivity) getActivity()).seekTo((int) id);
				((SubAdapter) parent.getAdapter()).setSelectPosition(position);

			}
		});
		if (mSubApplication.mDialogueList != null)
			mListView.setAdapter(new SubAdapter(mSubApplication.mDialogueList));
		return v;
	}

	public void smoothScrollByTime(long ms) {
		int[] p = new int[2];
		ListAdapter ada = mListView.getAdapter();
		p[0] = 0;
		p[1] = ada.getCount() - 1;
		while (p[1] - p[0] >= 2) {
			p = dichotomy(p, ms, ada);
		}
		int ret;
		if (ada.getItemId(p[0]) < ms)
			ret = p[0];
		else
			ret = p[1];
		mListView.smoothScrollToPosition(ret);
		mCurrentPosition = ret;
		((SubAdapter) ada).setSelectPosition(ret);
	}

	public int findPositionByTime(int ms) {
		int[] p = new int[2];
		ListAdapter ada = mListView.getAdapter();
		p[0] = 0;
		p[1] = ada.getCount() - 1;
		while (p[1] - p[0] >= 2) {
			p = dichotomy(p, ms, ada);
		}
		int ret;
		if (ada.getItemId(p[0]) < ms)
			ret = p[0];
		else
			ret = p[1];
		return ret;
	}

	private int[] dichotomy(int[] p, long v, ListAdapter ada) {
		int[] ret = new int[2];
		int midp = (p[0] + p[1]) / 2;
		long midv = ada.getItemId((p[0] + p[1]) / 2);
		if (v < midv) {
			ret[0] = p[0];
			ret[1] = midp;
		} else {
			ret[0] = midp;
			ret[1] = p[1];
		}
		return ret;
	}

	public void setSubFile(Uri file) {
		try {

			AssParser ap = new AssParser();
			ap.checkCharset(getActivity().getContentResolver().openInputStream(
					file));
			List<Dialogue> list = ap.parse(getActivity().getContentResolver()
					.openInputStream(file));
			ap.sortByStart(list);
			mSubApplication.mDialogueList = list;
			mListView.setAdapter(new SubAdapter(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAutoScrollMode(boolean b) {
		if (isAutoScrollMode == b)
			return;
		if (b) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					while (isAutoScrollMode) {
						try {
							final int p = findPositionByTime(mSubApplication
									.getMediaPlayer().getCurrentPosition());
							if (p != mCurrentPosition) {
								mListView.post(new Runnable() {

									@Override
									public void run() {
										mListView.smoothScrollToPosition(p);
										ListAdapter ada = mListView.getAdapter();
										((SubAdapter) ada).setSelectPosition(p);
									}
								});
								mCurrentPosition = p;
							}
							Thread.sleep(50);
						} catch (Exception e) {
							isAutoScrollMode=false;
							break;
						}
					}
				}
			});
			t.start();

		}
		isAutoScrollMode = b;
	}

}
