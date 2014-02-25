package com.kagami.subplayer;

import java.util.List;

import com.kagami.subplayer.AssParser.Dialogue;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SubAdapter extends BaseAdapter {

	private List<Dialogue> mList;
	public SubAdapter(List<Dialogue> list){
		mList=list;
	}
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return (long)(mList.get(position).Start*1000);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if(convertView==null)
			convertView=new TextView(parent.getContext());
		view=(TextView)convertView;
		view.setText(((Dialogue)getItem(position)).Text);
		return convertView;
	}

	

}
