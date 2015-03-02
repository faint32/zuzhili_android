package com.zuzhili.model.multipart;

import android.os.Parcel;
import android.os.Parcelable;

import com.zuzhili.bussiness.utility.TextUtil;

/**
 * Created by addison on 2/25/14.
 */
public class FileUpload implements Parcelable{
    String filename;
    String filepath;
    String newfilename;
    String desc;
    String fileidentity;
    String type;//0 item,1 add
    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getNewfilename() {
        return newfilename;
    }
    public void setNewfilename(String newfilename) {
        this.newfilename = TextUtil.processFileName(newfilename);
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getFileidentity() {
        return fileidentity;
    }
    public void setFileidentity(String fileidentity) {
        this.fileidentity = fileidentity;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(filename);
        dest.writeString(filepath);
        dest.writeString(newfilename);
        dest.writeString(desc);
        dest.writeString(fileidentity);
        dest.writeString(type);
        dest.writeString(path);
    }

    public static final Parcelable.Creator<FileUpload> CREATOR = new Parcelable.Creator<FileUpload>() {
        @Override
        public FileUpload createFromParcel(Parcel arg0) {
            FileUpload item = new FileUpload();

            item.filename = arg0.readString();
            item.filepath = arg0.readString();
            item.newfilename = arg0.readString();
            item.desc = arg0.readString();
            item.fileidentity = arg0.readString();
            item.type = arg0.readString();
            item.type = arg0.readString();
            return item;
        };
        @Override
        public FileUpload[] newArray(int size) {
            return new FileUpload[size];
        };
    };

}
