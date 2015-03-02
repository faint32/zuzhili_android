package com.zuzhili.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.zuzhili.R;

/**
 * 
 * @Title: FaceSelector.java
 * @Package: com.zuzhili.ui.views
 * @Description: 表情选择
 * @author: gengxin
 * @date: 2014-1-20
 */
public class FaceSelector {

	static public OnFaceSelectListener lis;

	public static void showDialog(final Context ctx) {
		GridView gridView = createGridView(ctx);
		final Dialog dialog = new AlertDialog.Builder(ctx).setTitle("选择表情").setView(gridView)
				.setPositiveButton("", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}).create();

		dialog.show();

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				dialog.dismiss();
				Map.Entry entry = (Map.Entry) FaceSelector.getfacesList(ctx).get(position).entrySet().iterator().next();
				int fid = (Integer) entry.getValue();
				String facename = FaceSelector.findFaceNameByFaceId(ctx, fid);
				if (lis != null) {
					lis.onSelected(facename);
				}
			}
		});
	}

	private static GridView createGridView(Context ctx) {

		final GridView view = new GridView(ctx);
		List<Map<String, Integer>> listItems = FaceSelector.getfacesList(ctx);
		SimpleAdapter simpleAdapter = new SimpleAdapter(ctx, listItems,R.layout.view_face_cell,new String[] { "image" }, new int[] { R.id.image });
		view.setAdapter(simpleAdapter);
		view.setNumColumns(6);
		view.setBackgroundColor(Color.rgb(214, 211, 214));
		view.setHorizontalSpacing(1);
		view.setVerticalSpacing(1);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		view.setGravity(Gravity.CENTER);
		return view;
	}

	public static String[] faceNames = new String[] { "嘻嘻", "哈哈", "喜欢", "晕",
            "流泪", "馋嘴", "抓狂", "哼", "可爱", "怒", "汗", "呵呵", "睡觉", "钱", "偷笑", "酷",
            "衰", "吃惊", "怒骂", "鄙视", "挖鼻孔", "色", "鼓掌", "悲伤", "思考", "生病", "亲亲",
            "抱抱", "懒得理你", "左哼哼", "右哼哼", "嘘", "委屈", "", "敲打", "疑问", "挤眼", "害羞",
            "可怜", "拜拜", "黑线", "强", "弱", "给力", "浮云", "围观", "威武", "", "汽车", "",
            "", "奥特曼", "", "", "不要", "ok", "赞", "勾引", "耶", "困", "拳头", "差劲",
            "握手", "玫瑰", "心", "伤心", "猪头", "咖啡", "麦克风", "月亮", "太阳", "", "萌",
            "礼物", "", "钟", "自行车", "蛋糕", "围脖", "手套", "雪花", "雪人", "帽子", "树叶",
            "足球", "吐", "闭嘴" };

	private static HashMap<String, Integer> faces;

	public static HashMap<String, Integer> getfaces(Context context) {
		if (faces != null) {
			return faces;
		}
		faces = new HashMap<String, Integer>();
		String faceName = "";
		for (int i = 1; i < 88; i++) {
			if (faceNames[i - 1].equals("")) {
				continue;
			}
			faceName = "face" + i;
			try {
				int id = R.drawable.class.getDeclaredField(faceName).getInt(
						context);
				faces.put(faceNames[i - 1], id);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return faces;
	}

	public static String findFaceNameByFaceId(Context context, int id) {
		String ret = "";
		getfaces(context);
		Iterator it = faces.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			int value = (Integer) entry.getValue();
			if (value == id) {
				ret = key;
				break;
			}
		}

		return "[" + ret + "]";
	}

	static List<Map<String, Integer>> listItems = new ArrayList<Map<String, Integer>>();

	public static List<Map<String, Integer>> getfacesList(Context context) {
		if (listItems.size() > 0) {
			return listItems;
		}
		String faceName = "";
		for (int i = 1; i < 88; i++) {
			if (faceNames[i - 1].equals("")) {
				continue;
			}
			faceName = "face" + i;
			try {
				int id = R.drawable.class.getDeclaredField(faceName).getInt(
						context);
				Map<String, Integer> listItem = new HashMap<String, Integer>();
				listItem.put("image", id);
				listItems.add(listItem);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return listItems;
	}

}
