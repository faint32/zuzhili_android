package com.zuzhili.model.multipart;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Title: MusicLocal.java
 * @Package com.zuzhili.mediaselect.domain
 * @Description: 本地音乐实体类
 * @author gengxin
 * @date 2013-4-23 下午13:57:25
 */
public class MusicLocal implements Parcelable{

	private String name;
	private double size;
	private Bitmap cover;
	private String path;
	private String pinyin;
	private long id;
	private long album_id;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	public Bitmap getCover() {
		return cover;
	}
	public void setCover(Bitmap cover) {
		this.cover = cover;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getAlbum_id() {
		return album_id;
	}
	public void setAlbum_id(long album_id) {
		this.album_id = album_id;
	}
	@Override
	public String toString() {
		return "MusicLocal [name=" + name + ", size=" + size + ", cover=" + cover
				+ ", path=" + path + ", pinyin=" + pinyin + "]";
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(path);
		dest.writeDouble(size);
		dest.writeParcelable(cover, flags);
		dest.writeString(pinyin);
		dest.writeLong(id);
		dest.writeLong(album_id);
	}
	
	public static final Creator<MusicLocal> CREATOR = new Creator<MusicLocal>() {
		@Override
		public MusicLocal createFromParcel(Parcel arg0) {
			MusicLocal item = new MusicLocal();
			item.name = arg0.readString();
			item.path = arg0.readString();
			item.size = arg0.readDouble();
			item.cover = arg0.readParcelable(null);
			item.pinyin = arg0.readString();
			item.id = arg0.readLong();
			item.album_id = arg0.readLong();
			return item;
		};
		@Override
		public MusicLocal[] newArray(int size) {
			return new MusicLocal[size];
		};
	};
	
}
