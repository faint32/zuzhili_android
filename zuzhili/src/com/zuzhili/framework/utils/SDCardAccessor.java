package com.zuzhili.framework.utils;

import java.io.File;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

interface SDCardStatusChangedCtrl {
	public void ChangedAvailable();

	public void ChangedUnAvailable();
}

public class SDCardAccessor extends BroadcastReceiver {

	private enum SDCardStatusChanged {
		SDCardStatusChanged_Available, SDCardStatusChanged_UnAvailable
	}

	private static boolean m_sdcardAvailable = false;
	private static boolean m_sdcardAvailabilityDetected = false;
	public static String m_sdcardDir = null;
	public static final String m_strTestJsonFilePath = "/testjson";
	public static final String m_strUserDataFilePath = "/userdata";
	public static final String m_strExceptionDataFilePath = "/exceptions";
	public static final String m_strAppFilePath = "/zhiliren";
	public static final String m_strimagecacheFilePath = "/imgscache";

	private static ArrayList<SDCardStatusChangedCtrl> m_ctrlList = null;

	public static boolean isSDCardAvailable() {
		if (!m_sdcardAvailabilityDetected) {
			m_sdcardAvailable = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
			m_sdcardAvailabilityDetected = true;
			if (m_sdcardAvailable) {
				if (m_sdcardDir == null) {
					m_sdcardDir = Environment.getExternalStorageDirectory()
							.toString();
				}
				notifyCtrl(SDCardStatusChanged.SDCardStatusChanged_Available);
			}
		}
		return m_sdcardAvailable;
	}

	public static String getTestJsonFullPath(String filename) {
		String retFullPath = "";
		if (isSDCardAvailable()) {
			retFullPath = m_sdcardDir + m_strAppFilePath
					+ m_strTestJsonFilePath;
			File fp = new File(retFullPath);
			if (!fp.exists()) {
				fp.mkdirs();
			}
			retFullPath += "/" + filename + ".txt";
		} else {

		}
		return retFullPath;
	}
	public static String getExecptionPath() {
		String retFullPath = "";
		if (isSDCardAvailable()) {
			retFullPath = m_sdcardDir + m_strAppFilePath
					+ m_strExceptionDataFilePath;
			File fp = new File(retFullPath);
			if (!fp.exists()) {
				fp.mkdirs();
			}
			retFullPath += "/exceptions.txt";
		} else {

		}
		return retFullPath;
	}

	public static String getUserDataFullPath(String username) {
		String retFullPath = "";
		if (isSDCardAvailable()) {
			retFullPath = m_sdcardDir + m_strAppFilePath
					+ m_strUserDataFilePath;
			File fp = new File(retFullPath);
			if (!fp.exists()) {
				fp.mkdirs();
			}
			retFullPath += "/" + username + ".db";
		} else {

		}
		return retFullPath;
	}

	public static String getImageCachePath() {
		String retFullPath = "";
		if (isSDCardAvailable()) {
			retFullPath = m_sdcardDir + m_strAppFilePath
					+ m_strimagecacheFilePath;
			File fp = new File(retFullPath);
			if (!fp.exists()) {
				fp.mkdirs();
			}
		} else {

		}
		return retFullPath;
	}

	public static void deleteFile(File fileTemp) {
		if (fileTemp == null) {
			return;
		}
		if (fileTemp.exists()) {
			if (fileTemp.isFile()) {
				fileTemp.delete();
			} else if (fileTemp.isDirectory()) {
				File files[] = fileTemp.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						deleteFile(files[i]);
					}
				}
			}
			fileTemp.delete();
		} else {
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		m_sdcardAvailabilityDetected = false;
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
			m_sdcardAvailable = true;
			if (m_sdcardDir == null) {
				m_sdcardDir = Environment.getExternalStorageDirectory()
						.toString();
			}
			notifyCtrl(SDCardStatusChanged.SDCardStatusChanged_Available);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			notifyCtrl(SDCardStatusChanged.SDCardStatusChanged_UnAvailable);
			m_sdcardAvailable = false;
		}
		m_sdcardAvailabilityDetected = true;
	}

	private static void notifyCtrl(SDCardStatusChanged status) {
		if (m_ctrlList != null) {
			int size = m_ctrlList.size();
			for (int i = 0; i < size; i++) {
				SDCardStatusChangedCtrl temp = m_ctrlList.get(i);
				if (temp != null) {
					if (status == SDCardStatusChanged.SDCardStatusChanged_Available) {
						temp.ChangedAvailable();
					} else {
						temp.ChangedUnAvailable();
					}
				}
			}
		}
	}
}
