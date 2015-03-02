package com.zuzhili.bussiness.utility;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import com.lidroid.xutils.util.LogUtils;

public class TextUtil {
	public static String getUniqueFileName(String name) {
		int i = name.lastIndexOf(".");
		String lastfix = null;
		if (i == -1) {
			lastfix = ".dat";
		} else {
			lastfix = name.substring(i);

		}
//		String ret = System.currentTimeMillis() + "" + lastfisx;
		String ret  = UUID.randomUUID().toString() + lastfix;
		return ret;
	}

	public static String processFileName(String name) {
		int i = name.lastIndexOf(".");
		String lastfix = "";
		if (i == -1) {
			lastfix = ".dat";
		}
		name = name + lastfix;
		return name;
	}

	/**
	 * 加亮 @****��@****: 或@****[空格] <br>
	 * Html.fromHtml(String) *
	 * 
	 * @param
	 * @return
	 */
	public static String composeCommentPersonStr(String name, String ids) {
		String comment = "回复  @" + name + "(" + ids + ") " + ": ";

		return comment;
	}

	public static String composeReforwdReforwdStr(String name, String ids,
			String content) {
		String ret = "//@" + name + "(" + ids + ") :" + content;

		return ret;
	}
	
	public static String processNullString(String s) {
		if(s == null || s.equals("null")) {
			return "";
		}else {
			return s;
		}
	}

	public static SpannableStringBuilder contentFilter(String input, Context ctx) {
		// 可变字符串，缓存
		SpannableStringBuilder spannableString = new SpannableStringBuilder(
				input);
		replaceUserString(spannableString, null);
		replaceUserIDString(spannableString);
		replacePhotoString(spannableString, null);
		replaceCommonFolderString(spannableString, null);
		replaceMediaFolderString(spannableString, null);
		replaceVedioFolderString(spannableString, null);
		HtmlSpan2Text(spannableString);
		formatImage(spannableString, ctx);
		return spannableString;
	}
	public static SpannableStringBuilder contentFilter2(String input, Context ctx) {
		// 可变字符串，缓存
		SpannableStringBuilder spannableString = new SpannableStringBuilder(
				input);
		replaceUserString(spannableString, null);
		replaceUserIDString(spannableString);
		replacePhotoString(spannableString, null);
		replaceCommonFolderString(spannableString, null);
		replaceMediaFolderString(spannableString, null);
		replaceVedioFolderString(spannableString, null);
		HtmlSpan2Text(spannableString);
		formatImage(spannableString, ctx);
        replaceInfo(spannableString);
		return spannableString;
	}

	public static SpannableStringBuilder contentFilterSpan(String input,
			Context ctx, UserClickableSpan userSpan, PhotoClickableSpan photoSpan, FolderClickableSpan folderSpan) {
		SpannableStringBuilder spannableString = new SpannableStringBuilder(
				input);
		replaceUserString(spannableString, userSpan);
		replaceUserIDString(spannableString);
		replacePhotoString(spannableString, photoSpan);
		replaceCommonFolderString(spannableString, folderSpan);
		replaceMediaFolderString(spannableString, folderSpan);
		replaceVedioFolderString(spannableString, folderSpan);
		HtmlSpan2Text(spannableString);
		formatImage(spannableString, ctx);

		return spannableString;
	}

	public static Spanned highLight(String str) {
		Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String m = matcher.group();
			str = str.replace(m, "<font color=Navy>" + m + "</font>");
		}
		return Html.fromHtml(str);
	}

	/**
	 * 加亮 @****��@****: 或@****[空格] <br>
	 * SpannableString.setSpan(Object what, int start, int end, int flags)
	 * 
	 * @param text
	 * @return
	 */
	// 这个和hightLight(String str) 的效率对�
	public static SpannableString light(CharSequence text) {
		SpannableString spannableString = new SpannableString(text);
		Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]");
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			spannableString.setSpan(new ForegroundColorSpan(Color.CYAN),
					matcher.start(), matcher.end(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannableString;
	}

	public static SpannableString talkfilelight(CharSequence text) {
		SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new ForegroundColorSpan(Color.CYAN), 0,
				text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}

	/**
	 * 格式化名�<br>
	 * 用于保存微博图像，截取url的最后一段做为图像文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String formatName(String url) {
		if (url == null || "".equals(url)) {
			return url;
		}
		int start = url.lastIndexOf("/");
		int end = url.lastIndexOf(".");
		if (start == -1 || end == -1) {
			return url;
		}

		String url0 = url.substring(start + 1, end);

		if (url.contains("thumbnail")) {
			url0 = url0 + "thumbnail";
			return url0;
		}
		if (url.contains("bmiddle")) {
			url0 = url0 + "bmiddle";
			return url0;
		}
		if (url.contains("large")) {
			url0 = url0 + "large";
			return url0;
		}
		return url;
	}

	public static String getUrlParam(String url, String paramname) {
		String ret = "";
		ret=md5s(url);
//		String[] temp = url.split("\\?");
//		if (temp != null) {
//			if (temp[1] != null) {
//				String[] temp2 = temp[1].split("&");
//				if (temp2 != null) {
//					for (String param : temp2) {
//						String[] ptemp = param.split("=");
//						if (ptemp != null) {
//							if (ptemp[0].equals(paramname)) {
//								ret = ptemp[1];
//								break;
//							}
//						}
//					}
//				}
//			}
//		}

		return ret;
	}
	public static String getLastPicFix(String plainText) {
		int i = plainText.lastIndexOf(".");
		String lastfix = null;
		if (i == -1) {
			lastfix = ".jpg";
		} else {
			lastfix = plainText.substring(i);

		}
		
		return lastfix;
	}
	public static String md5s(String plainText) {
		String ret=null;
		  try {
		   MessageDigest md = MessageDigest.getInstance("MD5");
		   md.update(plainText.getBytes());
		   byte b[] = md.digest();

		   int i;

		   StringBuffer buf = new StringBuffer("");
		   for (int offset = 0; offset < b.length; offset++) {
		    i = b[offset];
		    if (i < 0)
		     i += 256;
		    if (i < 16)
		     buf.append("0");
		    buf.append(Integer.toHexString(i));
		   }
		   ret = buf.toString();
//		   System.out.println("result: " + buf.toString());// 32位的加密
//		   System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
		  } catch (NoSuchAlgorithmException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();

		  }
		  return ret;
	}
	/**
	 * @param name
	 * @return
	 */
	public static String formatSource(String name) {
		if (name == null || "".equals(name)) {
			return name;
		}
		int start = name.indexOf(">");
		int end = name.lastIndexOf("<");
		if (start == -1 || end == -1) {
			return name;
		}
		return name.substring(start + 1, end);
	}

	/**
	 * 匹配表情
	 * @param
	 * @param context
	 * @return
	 */
	public static void formatImage(SpannableStringBuilder spannableString,
			Context context) {

		Pattern pattern = Pattern.compile("\\[[\u4E00-\u9FFF|\\w]{1,4}\\]"); // 正则式出错？两个连续的单字表情都无法正确替换
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			String faceName = matcher.group();
			String key = faceName.substring(1, faceName.length() - 1);
			if (Face.getfaces(context).containsKey(key)) {
				spannableString.setSpan(
						new ImageSpan(context, Face.getfaces(context).get(key),
								DynamicDrawableSpan.ALIGN_BOTTOM), matcher.start(),
						matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	public static SpannableStringBuilder formatImage(String content,
			Context context) {
		SpannableStringBuilder spannableString = new SpannableStringBuilder();
		spannableString.append(content);
		Pattern pattern = Pattern.compile("\\[[\u4E00-\u9FFF|\\w]{1,4}\\]"); // 正则式出错？两个连续的单字表情都无法正确替换
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			String faceName = matcher.group();
			String key = faceName.substring(1, faceName.length() - 1);
			if (Face.getfaces(context).containsKey(key)) {
				spannableString.setSpan(
						new ImageSpan(context, Face.getfaces(context).get(key),
								DynamicDrawableSpan.ALIGN_BASELINE), matcher.start(),
						matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}

	/**
	 * 将text中@某人的字体加亮，匹配的表情文字以表情显示
	 * 
	 * @param text
	 * @param context
	 * @return
	 */
	public static SpannableString formatContent(CharSequence text,
			Context context) {
		SpannableString spannableString = new SpannableString(text);
		/*
		 * @[^\\s:：]+[:：\\s] 匹配@某人 \\[[^0-9]{1,4}\\] 匹配表情
		 */
		Pattern pattern = Pattern
				.compile("@[^\\s:：]+[:：\\s]|\\[[^0-9]{1,4}\\]");
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			String match = matcher.group();
			if (match.startsWith("@")) { // @某人，加亮字�				
				spannableString.setSpan(new ForegroundColorSpan(0xff0077ff),
				matcher.start(), matcher.end(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			} else if (match.startsWith("[")) { // 表情
				String key = match.substring(1, match.length() - 1);
				if (Face.getfaces(context).containsKey(key)) {
					spannableString.setSpan(new ImageSpan(context, Face
							.getfaces(context).get(key)), matcher.start(),
							matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return spannableString;
	}


	public static SpannableStringBuilder replaceName(String string) {
        if(!TextUtils.isEmpty(string)) {
            SpannableStringBuilder spannableString = new SpannableStringBuilder(
                    string);
            spannableString.setSpan(
                    new ForegroundColorSpan(Color.argb(255, 63, 104, 135)),
                    0, string.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
        return  null;
	}

	public static void replaceInfo(SpannableStringBuilder spannableString) {
            spannableString.setSpan(
                    new ForegroundColorSpan(Color.argb(255, 63, 104, 135)),
                    0, spannableString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	/**
	 * 超链接处�	 *
	 * @param hrefString
	 * @return
	 */
	public static void replaceUserString(SpannableStringBuilder hrefString,
			UserClickableSpan span) {
		// String
		// hrefString="<a href=\"aa.jsp?id=1\">mmm</a>sfsf阿娇咖啡机啊<a href=\"aa.jsp?id=2\">eee</a><a href=\"aa.jsp?id=3\">ccc</a><a href=\"aa.jsp?id=1\">mmm</a>";
		String rule = "(@([^@]+)\\((\\d+)\\))";
		replaceString(hrefString, rule, span);
	}

	public static void replacePhotoString(SpannableStringBuilder hrefString,
			PhotoClickableSpan span) {
		// String
		// hrefString="<a href=\"aa.jsp?id=1\">mmm</a>sfsf阿娇咖啡机啊<a href=\"aa.jsp?id=2\">eee</a><a href=\"aa.jsp?id=3\">ccc</a><a href=\"aa.jsp?id=1\">mmm</a>";
		String rule = "<a .*href='.+/album/(\\d+)'>(.*?)</a>";
		Pattern pattern = Pattern.compile(rule);// 加入规则
		Matcher m = pattern.matcher(hrefString);
		boolean result = m.find();
		while (result) {
			if (span != null) {
				PhotoClickableSpan tempspan = span.getSelf();
				String id = m.group(1);
//				tempspan.setAlbumeID(id);
//				tempspan.setAlbumeName(m.group(2));
				hrefString.setSpan(tempspan, m.start(2), m.end(2),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				hrefString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 63, 104, 135)),
						m.start(), m.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			result = m.find();
		}
	}
	
	public static void replaceCommonFolderString(SpannableStringBuilder hrefString,
			FolderClickableSpan span) {
		// String
		// <a href='http://www.zuzhili.com/2050/8894/folders?folderid=78443'>O(∩_∩)O哈！</a>"
//		String rule = "<a .*href='.+foldertype=(\\d+).id=(\\d+).ids=(\\d+).+'>(.*?)</a>";
		String rule = "<a .*href='.+/folders(\\S)folderid=(\\d+)'>(.*?)</a>";
		Pattern pattern = Pattern.compile(rule);// 加入规则
		Matcher m = pattern.matcher(hrefString);
		boolean result = m.find();
		while (result) {
			if (span != null) {
				FolderClickableSpan tempspan = span.getSelf();
				Log.i("liutao", "folderType: " + m.group(1) + " folderId: " + m.group(2) + " folderName: " + m.group(3));
				tempspan.setFolderId(m.group(2));
				tempspan.setFolderName(m.group(3));
				hrefString.setSpan(tempspan, m.start(3), m.end(3),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				hrefString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 63, 104, 135)),
						m.start(), m.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			result = m.find();
		}
	}
	
	public static void replaceMediaFolderString(SpannableStringBuilder hrefString,
			FolderClickableSpan span) {
		// 上传1个视频至目录<a href='http://www.zuzhili.com/63/6137/vidio/vfolder/91'>共享</a>
		String rule = "<a .*href='.+/mfolder/(\\d+)'>(.*?)</a>";
		Pattern pattern = Pattern.compile(rule);// 加入规则
		Matcher m = pattern.matcher(hrefString);
		boolean result = m.find();
		while (result) {
			if (span != null) {
				FolderClickableSpan tempspan = span.getSelf();
				tempspan.setFolderId(m.group(1));
				tempspan.setFolderName(m.group(2));
				hrefString.setSpan(tempspan, m.start(2), m.end(2),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				hrefString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 63, 104, 135)),
						m.start(), m.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			result = m.find();
		}
	}
	
	public static void replaceVedioFolderString(SpannableStringBuilder hrefString,
			FolderClickableSpan span) {
		// 上传1个视频至目录<a href='http://www.zuzhili.com/63/6137/vidio/vfolder/91'>共享</a>
		String rule = "<a .*href='.+/vfolder/(\\d+)'>(.*?)</a>";
		Pattern pattern = Pattern.compile(rule);// 加入规则
		Matcher m = pattern.matcher(hrefString);
		boolean result = m.find();
		while (result) {
			if (span != null) {
				FolderClickableSpan tempspan = span.getSelf();
				tempspan.setFolderId(m.group(1));
				tempspan.setFolderName(m.group(2));
				hrefString.setSpan(tempspan, m.start(2), m.end(2),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				hrefString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 63, 104, 135)),
						m.start(), m.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			result = m.find();
		}
	}

	public static void replaceUserIDString(SpannableStringBuilder hrefString) {
		// String
		// hrefString="<a href=\"aa.jsp?id=1\">mmm</a>sfsf阿娇咖啡机啊<a href=\"aa.jsp?id=2\">eee</a><a href=\"aa.jsp?id=3\">ccc</a><a href=\"aa.jsp?id=1\">mmm</a>";
		String rule = "@[^@]+(\\(\\d+\\))";
		Pattern pattern = Pattern.compile(rule);// 加入规则
		Matcher m = pattern.matcher(hrefString);
		boolean result = m.find();
		int length = 0;
		while (result) {
			String s = m.group(1);
			hrefString.replace(m.start(1) - length, m.end(1) - length, "");
			length += m.group(1).length();
			result = m.find();
		}
	}

	public static String replaceAhrefBiaoqianString(String hrefString) {

		String regex = "<\\/?[^\\>]+>";
		hrefString = hrefString.replaceAll(regex, "");
		return hrefString;
		// String rule =
		// "(<a href=\"javascript:;\" onclick=\"toSearch((.*?))\">(.*?)</a>)";
		// return replaceATagString(hrefString, rule);
	}

	public static String HtmlSpan2Text(SpannableStringBuilder inputString) {
		// String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;

		Pattern p_html1;
		Matcher m_html1;

		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{�script[^>]*?>[//s//S]*?<///script>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{�style[^>]*?>[//s//S]*?<///style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(inputString);
			while (m_script.find()) {

				inputString.replace(m_script.start(), m_script.end(), "");
			}
			// htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(inputString);
			while (m_style.find()) {

				inputString.replace(m_style.start(), m_style.end(), "");
			}
			// htmlStr = m_style.replaceAll(""); // 过滤style标签
			//
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(inputString);
			int length = 0;
			while (m_html.find()) {

				inputString.replace(m_html.start() - length, m_html.end()
						- length, "");
				length += m_html.end() - m_html.start();
			}
			// htmlStr = m_html.replaceAll(""); // 过滤html标签
			//
			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(inputString);
			while (m_html1.find()) {

				inputString.replace(m_html1.start(), m_html1.end(), "");
			}

			// htmlStr = m_html1.replaceAll(""); // 过滤html标签

			// textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符�	
	}

	public static String Html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;

		Pattern p_html1;
		Matcher m_html1;

		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{�script[^>]*?>[//s//S]*?<///script>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{�style[^>]*?>[//s//S]*?<///style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符�	
	}

	public static void replaceString(SpannableStringBuilder hrefString,
			String rule, UserClickableSpan span) {
		// String
		// hrefString="<a href=\"aa.jsp?id=1\">mmm</a>sfsf阿娇咖啡机啊<a href=\"aa.jsp?id=2\">eee</a><a href=\"aa.jsp?id=3\">ccc</a><a href=\"aa.jsp?id=1\">mmm</a>";
		// String rule="(<a\\s*href=[^>]*id=(.*?)\">(.*?)</a>)";

		// Pattern pattern = Pattern
		// .compile("(<a\\s*href=[^>]*id=(.*?)\">(.*?)</a>)");// 加入规则
		Pattern pattern = Pattern.compile(rule);// 加入规则

		Matcher m = pattern.matcher(hrefString);
		boolean result = m.find();
		while (result) {
			if (span != null) {
				UserClickableSpan tempspan = span.getSelf();
				String name = m.group(2);
				String id = m.group(3);
				tempspan.setIds(id);
				tempspan.setUsername(name);
				hrefString.setSpan(tempspan, m.start(), m.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				hrefString.setSpan(
						new ForegroundColorSpan(Color.argb(255, 63, 104, 135)),
						m.start(), m.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			result = m.find();
		}
	}

	public static String replaceATagString(String hrefString, String rule) {
		// String
		// hrefString="<a href=\"aa.jsp?id=1\">mmm</a>sfsf阿娇咖啡机啊<a href=\"aa.jsp?id=2\">eee</a><a href=\"aa.jsp?id=3\">ccc</a><a href=\"aa.jsp?id=1\">mmm</a>";
		// String rule="(<a\\s*href=[^>]*id=(.*?)\">(.*?)</a>)";

		// Pattern pattern = Pattern
		// .compile("(<a\\s*href=[^>]*id=(.*?)\">(.*?)</a>)");// 加入规则
		Pattern pattern = Pattern.compile(rule);// 加入规则

		Matcher m = pattern.matcher(hrefString);
		boolean result = m.find();
		while (result) {
			String s = m.group(4);
			hrefString = hrefString.replace(m.group(), s);
			result = m.find();
		}
		return hrefString;
	}

	static void Log(String msg) {
		Log.i("weibo", "TextUtil--" + msg);
	}

    /**
     * 检测是否有emoji字符
     * @param source
     * @return 一旦含有就抛出
     */
    public static boolean containsEmoji(String source) {
        if (null==source || "".equals(source.trim())) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (!isNotEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    private static boolean isNotEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {

        if (!containsEmoji(source)) {
            return source;//如果不包含，直接返回
        }
        //到这里铁定包含
        StringBuilder buf = null;
        source = " "+source;
        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isNotEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            } else {
            }
        }

        if (buf == null) {
        } else {
            if (buf.length() == len) {//这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;

            } else {
                source =  buf.toString();
            }
        }
        return source.trim();
    }
}
