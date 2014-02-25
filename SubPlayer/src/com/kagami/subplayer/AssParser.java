package com.kagami.subplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class AssParser {
	private Pattern pEvents;
	private Pattern pFormat;
	private Pattern pDialogue;
	private Pattern pDialogueContent;
	private int tag = 0;
	private final static int tagEventsStart = 1;
	private final static int tagFormatStart = 2;
	private Map<String, Integer> mFormatMap;
	private String charset;

	public AssParser() {
		pEvents = Pattern.compile("^\\[Events\\]");
		pFormat = Pattern.compile("^Format:(.*)");
		pDialogue = Pattern.compile("^Dialogue:(.*)");

	}



	public void checkCharset(InputStream is) throws IOException{
		int i1 = is.read();
		int i2 = is.read();
		int i3 = is.read();
		if (i1 == 0xef && i2 == 0xbb && i3 == 0xbf)
			charset = "utf8";
		if (i1 == 0xff && i2 == 0xfe)
			charset = "utf16";
		is.close();
	}
	public List<Dialogue> parse(InputStream is) throws Exception {
		List<Dialogue> retList = new ArrayList<Dialogue>();
		/**
		 * 1 . UTF7 所有字节的内容不会大于127,也就是不大于&HFF 2 . UTF8 起始三个字节为"EF BB BF" 3 .
		 * UTF16BigEndian 起始三个字节为"FE FF" 4 . UTF16SmallEndian 起始三个字节为"FF FE"
		 */
		
		
		InputStreamReader isr = new InputStreamReader(is,charset);
		BufferedReader br = new BufferedReader(isr);
		String s = br.readLine();
		Matcher mat;
		while (s != null) {
			switch (tag) {
			case 0:
				mat = pEvents.matcher(s);
				if (mat.find())
					tag = tagEventsStart;
				break;
			case tagEventsStart:
				mat = pFormat.matcher(s);
				if (mat.find()) {
					String fs = mat.group(1);
					initFormatMap(fs);
					tag = tagFormatStart;
				}
				break;
			case tagFormatStart:
				mat = pDialogue.matcher(s);
				if (mat.find()) {
					retList.add(createDialogue(s));
				}
				break;
			default:
				break;
			}
			s = br.readLine();
		}
		br.close();
		return retList;

	}

	private void initFormatMap(String formats) {
		mFormatMap = new HashMap<String, Integer>();
		String[] ss = formats.split(",");
		for (int i = 0; i < ss.length; i++) {
			mFormatMap.put(ss[i].trim(), i);
		}
		String ps = "^Dialogue:\\s*";
		for (int i = 0; i < ss.length - 1; i++)
			ps += "(.*?),";
		ps += "(.*)";
		pDialogueContent = Pattern.compile(ps);
	}

	private Dialogue createDialogue(String d) {
		Dialogue di = new Dialogue();
		Matcher mat = pDialogueContent.matcher(d);
		if (mat.find()) {
			di.Start = timeToFloat(mat.group(mFormatMap.get("Start") + 1));
			di.End = mat.group(mFormatMap.get("End") + 1);
			String text = mat.group(mFormatMap.get("Text") + 1);
			di.Text = text.replaceAll("\\{.*\\}", "").replaceAll("\\\\N",
					System.getProperty("line.separator"));
		}
		return di;
	}

	private float timeToFloat(String time) {
		String[] ss = time.split(":");
		float h = Float.parseFloat(ss[0]);
		float m = Float.parseFloat(ss[1]);
		float s = Float.parseFloat(ss[2]);
		return (h * 60 + m) * 60 + s;
	}

	public class Dialogue {
		public float Start;
		public String End;
		public String Text;

	}

	public void sortByStart(List<Dialogue> list) {
		Collections.sort(list, new SortByStart());
	}

	class SortByStart implements Comparator<Dialogue> {

		@Override
		public int compare(Dialogue o1, Dialogue o2) {
			// TODO Auto-generated method stub
			if (o1.Start < o2.Start)
				return -1;
			else
				return 1;
		}

	}


}
