package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.multiselect.VedioListActivity;

/**
 * @Title: PublishMainActivity.java
 * @Package: com.zuzhili.ui.activity.publish
 * @author: gengxin
 * @date: 2014-1-16
 */
public class PublishMainActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {
	
	@ViewInject(R.id.txt_publish_write)
	private TextView publishWriteTxt;			//publish write
	
	@ViewInject(R.id.txt_publish_pic)
	private TextView publishPixTxt;				//publish pictrue
	
	@ViewInject(R.id.txt_publish_music)
	private TextView publishMusicTxt;			//publish music
	
	@ViewInject(R.id.txt_publish_vedio)
	private TextView publishVedioTxt;			//publish vedio
	
	@ViewInject(R.id.txt_publish_file)
	private TextView publishFileTxt;			//publish file
	
	@ViewInject(R.id.txt_publish_approval)
	private TextView publishApprovalTxt;			//publish approval

	private String spaceid;			//spaceid
	
	private static final int FLAG_ACTIVITY_RESULT_WRITE = 0;		//request code for write
	private static final int FLAG_ACTIVITY_RESULT_FILE = 1;		//request code for file
	private static final int FLAG_ACTIVITY_RESULT_PIC = 2;		//request code for pic
	private static final int FLAG_ACTIVITY_RESULT_MUSIC = 3;		//request code for music
	private static final int FLAG_ACTIVITY_RESULT_VEDIO_SEND = 4;		//request code for vedio
	private static final int FLAG_ACTIVITY_RESULT_VEDIO_LIST = 5;		//request code for vediolist
	private static final int FLAG_ACTIVITY_RESULT_APPROVAL = 6;		//request code for approval
	
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.activity_publish_main);
		ViewUtils.inject(this);
		initData();
	}

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0,  getString(R.string.publish_main), false);
        return true;
    }

    /**
	 * init the data from source-activity
	 */
	private void initData() {
		spaceid = getIntent().getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);
        //the resource is from space and the layout of approval should be gone
        if(spaceid != null && !spaceid.trim().equals("")){
            publishApprovalTxt.setVisibility(View.GONE);
        }
        setCustomActionBarCallback(this);
	}
	
	/**
	 * publish write
	 * @param view
	 */
	@OnClick(R.id.txt_publish_write)
	public void publishWrite(View view){
		toPublish(PublishWriteActivity.class, FLAG_ACTIVITY_RESULT_WRITE);
	}
	
	/**
	 * publish pic
	 * @param view
	 */
	@OnClick(R.id.txt_publish_pic)
	public void publishPic(View view){
		toPublish(PublishImageActivity.class, FLAG_ACTIVITY_RESULT_PIC);
	}
	
	/**
	 * publish music
	 * @param view
	 */
	@OnClick(R.id.txt_publish_music)
	public void publishMusic(View view){
		toPublish(PublishMusicActivity.class, FLAG_ACTIVITY_RESULT_WRITE);
	}
	
	/**
	 * publish vedio
	 * @param view
	 */
	@OnClick(R.id.txt_publish_vedio)
	public void publishVedio(View view){
		toPublish(VedioListActivity.class, FLAG_ACTIVITY_RESULT_VEDIO_LIST);
	}
	
	/**
	 * publish file
	 * @param view
	 */
	@OnClick(R.id.txt_publish_file)
	public void publishFile(View view){
		toPublish(PublishFileActivity.class, FLAG_ACTIVITY_RESULT_FILE);
	}
	
	/**
	 * publish write
	 * @param view
	 */
	@OnClick(R.id.txt_publish_approval)
	public void publishApproval(View view){
		toPublish(PublishApprovalActivity.class, FLAG_ACTIVITY_RESULT_APPROVAL);
	}
	
	
	
	/**
	 * go to publish by specified class
	 * @param clazz		class of the specified activity
	 * @param flag		responseCode for result
	 */
	private void toPublish(Class clazz, int flag){
		Intent intent = new Intent();
		intent.setClass(this, clazz);
		if(spaceid != null && !spaceid.trim().equals("")){
			intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID, spaceid);
		}
        if(flag == FLAG_ACTIVITY_RESULT_VEDIO_SEND){
            intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE, Constants.VEDIO_LIST);
        }
		startActivityForResult(intent, flag);
        if(flag == FLAG_ACTIVITY_RESULT_VEDIO_SEND){
            finish();
        }
	}

    /**
     * 发布视频
     * @param clazz
     * @param flag
     */
    private void toPublishVedio(Class clazz, int flag, Intent intent){
        intent.setClass(this, clazz);
        if(ValidationUtils.validationString(spaceid))
            intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID, spaceid);
        if(flag == FLAG_ACTIVITY_RESULT_VEDIO_SEND){
            intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE, Constants.VEDIO_LIST);
        }
        startActivityForResult(intent, flag);
        if(flag == FLAG_ACTIVITY_RESULT_VEDIO_SEND){
            finish();
        }
    }
	
	/**
	 * finish this activity
	 * @param resultCode
	 */
	private void finishThisPage(int resultCode){
		if(resultCode == Activity.RESULT_OK){
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FLAG_ACTIVITY_RESULT_WRITE:
			finishThisPage(resultCode);
			break;
		case FLAG_ACTIVITY_RESULT_PIC:
			finishThisPage(resultCode);
			break;
		case FLAG_ACTIVITY_RESULT_FILE:
			finishThisPage(resultCode);
			break;
		case FLAG_ACTIVITY_RESULT_MUSIC:
			finishThisPage(resultCode);
			break;
		case FLAG_ACTIVITY_RESULT_VEDIO_LIST:
            if(resultCode == RESULT_OK){
                toPublishVedio(PublishVedioActivity.class, FLAG_ACTIVITY_RESULT_VEDIO_SEND, data);
            }
			break;
		case FLAG_ACTIVITY_RESULT_VEDIO_SEND:
			finishThisPage(resultCode);
			break;
		case FLAG_ACTIVITY_RESULT_APPROVAL:
			finishThisPage(resultCode);
			break;
		}
	}


    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean performClickOnRight() {
        return super.performClickOnRight();
    }
}
