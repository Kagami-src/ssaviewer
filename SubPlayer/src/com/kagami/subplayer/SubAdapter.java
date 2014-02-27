package com.kagami.subplayer;

import java.util.List;

import com.kagami.subplayer.AssParser.Dialogue;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SubAdapter extends BaseAdapter {

	private List<Dialogue> mList;
	private int mSelectPosition=-1;
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
			convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sublist, null);
		view=(TextView)convertView.findViewById(R.id.textView1);
		view.setText(((Dialogue)getItem(position)).Text);
		if(mSelectPosition==position)
			view.setSelected(true);
		else
			view.setSelected(false);
		return convertView;
	}
	
	public void setSelectPosition(int p){
		mSelectPosition=p;
		this.notifyDataSetChanged();
	}

	

}
