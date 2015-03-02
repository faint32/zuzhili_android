package com.zuzhili.ui.activity.multiselect;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.controller.FileListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by addison on 2/25/14.
 */
public class FileListActivity extends ListActivity {

    @ViewInject(R.id.txt_path)
    private TextView pathTxt;

    private List<String> items = null;
    private List<String> paths = null;
    private String rootPath = "/";
    private String curPath = "/";
    int selecttype;//0,folder,1,file
    List<Integer> mposStack=new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_fileselect);
        ViewUtils.inject(this);
        Intent it=getIntent();
        selecttype=it.getIntExtra("selecttype", 1);
        rootPath= Environment.getExternalStorageDirectory().getAbsolutePath();
        getFileDir(rootPath,1);
    }


    private void getFileDir(String filePath,int type/*0 back one,1 go,2 back home*/) {
        pathTxt.setText(filePath);
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        File f = new File(filePath);
        File[] files = f.listFiles();

        if (!filePath.equals(rootPath)) {
            items.add("b1");
            paths.add(rootPath);
            items.add("b2");
            paths.add(f.getParent());
        }
        List<File> filelists=convert2List(files);
        Collections.sort(filelists, new NameSort());
        for (File file:filelists) {
            if(selecttype==0){
                if(file.isDirectory()){

                    items.add(file.getName());
                    paths.add(file.getPath());
                }
            }else if(selecttype==1){
                items.add(file.getName());
                paths.add(file.getPath());
            }else{

            }

        }
        FileListAdapter adapter=new FileListAdapter(this, items, paths);
        setListAdapter(adapter);
        if(type==0){

            int count=mposStack.size();
            this.setSelection(mposStack.get(count-1));
            mposStack.remove(count-1);
        }else if(type==2){

            this.setSelection(mposStack.get(0));
            mposStack.clear();
        }
    }
    List<File> convert2List(File[] files){
        List<File> filelists=new ArrayList<File>();
        if(files!=null){

            for(File f:files){
                filelists.add(f);
            }
        }

        return filelists;
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(paths.get(position));
        String type=items.get(position);
        int action=0;
        if(type.equals("b1")){
            action=2;
        }else if(type.equals("b2")){
            action=0;
        }else{
            action=1;
            mposStack.add(getListView().getFirstVisiblePosition());
        }
        if (file.isDirectory()) {
            curPath = paths.get(position);
            getFileDir(paths.get(position),action);
        } else {
//			openFile(file);
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("file", file.getAbsolutePath());
            bundle.putString("name", file.getName());
            data.putExtras(bundle);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();

        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else {
            type = "*";
        }
        type += "/*";
        return type;
    }
    class NameSort implements Comparator {
        public  int compare(Object  obj1, Object obj2 ){
            String u1 = ((File)obj1).getName();
            String u2 =((File)obj2).getName();
            int ret=u1.compareTo(u2);
            return ret;
        }
    }

}
