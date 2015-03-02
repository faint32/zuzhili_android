package com.zuzhili.ui.activity.multiselect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.String2Alpha;
import com.zuzhili.controller.MusicListAdapter;
import com.zuzhili.model.multipart.MusicLocal;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.QuerySideBar;


/**
 * @Title: MusicListActivity.java
 * @Package com.zuzhili.mediaselect
 * @Description: 音乐列表
 * @author gengxin
 * @date 2013-4-16 上午10：24：20
 */
public class MusicListActivity extends BaseActivity implements QuerySideBar.OnTouchingLetterChangedListener, BaseActivity.TimeToShowActionBarCallback {

    @ViewInject(R.id.query_bar)
    private QuerySideBar queryBar;

    @ViewInject(R.id.music_list)
    private ListView musicListLV;

    private List<MusicLocal> musicLocalList;
    private List<MusicLocal> musicLocalChosed = new ArrayList<MusicLocal>();
    private Handler handler;
    private String[] media_info = new String[] { MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE };
    private int selectedCount;
    private Cursor cursor;
    private int added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music_list);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        Intent intent = getIntent();
        added = intent.getIntExtra(Constants.MUSIC_CHOOSED_COUNT, 0);
        initView();
        new AynsLoadMusic().start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                musicListLV.setAdapter(new MusicListAdapter(MusicListActivity.this, musicLocalList, cursor));
            }
        };
        initListener();
    }

    private void initView() {
        musicListLV.setTextFilterEnabled(true);
    }

    private void initListener() {
        queryBar.setOnTouchingLetterChangedListener(this);
    }

    @Override
    public boolean performClickOnRight() {
        setReult();
        return super.performClickOnRight();
    }

    /**
     * 返回结果
     */
    private void setReult(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.MUSIC_CHOOSED_LIST,  (ArrayList<? extends Parcelable>) musicLocalChosed);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean performClickOnLeft() {
        finish();;
        return super.performClickOnLeft();
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_done_top, getString(R.string.title_select_music), false);
        return false;
    }

    /**
     * 初始化音乐列表
     */
    class AynsLoadMusic extends Thread {
        @Override
        public void run() {
            musicLocalList = getMusicLocalList();
//			Collections.sort(musicLocalList, new PinyinComparator());
            Message message = handler.obtainMessage(0, musicLocalList);
            handler.sendMessage(message);
        }
    }

    /**
     * 显示音乐列表
     */
    private List<MusicLocal> getMusicLocalList() {
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, media_info, null, null, null);
        List<MusicLocal> musicLocals = new ArrayList<MusicLocal>();
        if (null != cursor && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String2Alpha cvstr=new String2Alpha();
            musicLocalList = new ArrayList<MusicLocal>();
            for (int i = 0; i < cursor.getCount(); i++) {
                MusicLocal musicLocal = new MusicLocal();
                musicLocal.setName(cursor.getString(0));
                musicLocal.setId(cursor.getInt(3));
                musicLocal.setPinyin(cvstr.chinese2PinYin(cursor.getString(0)));
                musicLocal.setPath(cursor.getString(5));
                musicLocal.setSize(cursor.getLong(7)/(1024*1024));
                musicLocal.setAlbum_id(cursor.getInt(cursor.getColumnIndex(AudioColumns.ALBUM_ID)));
                musicLocals.add(musicLocal);
                cursor.moveToNext();
            }
        }
        Collections.sort(musicLocals, new Sort_Members());
        return musicLocals;
    }

    /**
     * 根据音频首写字母排序
     */
    class Sort_Members implements Comparator<MusicLocal> {

        @Override
        public int compare(MusicLocal item1, MusicLocal item2) {
            String object1 = item1.getPinyin().toLowerCase();
            String object2 = item2.getPinyin().toLowerCase();
            int ret = object1.compareTo(object2);
            return ret;
        }

    }

    /**
     * 音频时间转换
     * @param time
     * @return
     */
    public String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


    @Override
    public void onTouchingLetterChanged(String s) {
        if (alphaIndexer(s) > 0) {
            int position = alphaIndexer(s);
            musicListLV.setSelection(position);

        }
    }

    /**
     * 返回首字母对应的索引
     * @param s
     * @return
     */
    public int alphaIndexer(String s) {
        int position = 0;
        for (int i = 0; i < musicLocalList.size(); i++) {

            if (musicLocalList.get(i).getPinyin().startsWith(s)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @OnItemClick(R.id.music_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicLocal musicLocal = (MusicLocal) parent.getItemAtPosition(position);
//		musicLocal.setCover(getArtwork(this, musicLocal.getId(),musicLocal.getAlbum_id(), true));
        MusicListAdapter.ViewHolder holder = (MusicListAdapter.ViewHolder) view.getTag();
        if(musicLocal.getSize() > 10.0){
            Toast.makeText(getApplicationContext(), getString(R.string.music_size_hint), 0).show();
            return;
        }
        if(holder.selectCBX.isChecked() == false) {
            if(selectedCount >= 5 - added) {
                Toast.makeText(getApplicationContext(), getString(R.string.music_upload_count_hint), 0).show();
                return;
            }
        }
        holder.selectCBX.toggle();
        MusicListAdapter.getIsSelected().put(position, holder.selectCBX.isChecked());
        if(holder.selectCBX.isChecked() == true) {
            selectedCount ++;
            musicLocalChosed.add(musicLocalChosed.size(), musicLocal);
        } else {
            selectedCount --;
            musicLocalChosed.remove(musicLocalChosed.size() - 1);
        }
    }

}
