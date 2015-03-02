package com.zuzhili.model;

import java.io.Serializable;
import java.util.List;

import com.zuzhili.bussiness.utility.TimeUtils;

import android.text.format.DateUtils;

public class MiniBlog implements Serializable {
	
	private static final long serialVersionUID = 7938121259826632162L;
	private Integer id;
    private String listid;
	private String sourceid;
    private Integer fowardnum;
    private Integer replynum;
    private Integer collectionnum;
	private String title;
    private String createtime;
    private String timeLong;
    private String updatetime;
    private String content;
    private String username;
    private String ids;
    private String userhead;
    private String userhead150;
    private String messagetype;//来自哪里
    private String apptype;// 3文章 6图片 9文件 16 音视频
    private String apptypeid;//类型id
    private long toptime;
    private List<Config> configlist;
    private MiniBlog childAbs;
    private String comefrom;//来自哪个activity
    private String fromspace;
    
    public String getComefrom() {
		return comefrom;
	}
    public String getReforwordID() {
    	String ret="";
    	if(sourceid==null || sourceid.length()==0){
    		ret=String.valueOf(id);
    	}else{
    		ret=sourceid;
    	}
		return ret;
	}
	public void setComefrom(String from) {
		String s = from;
		if("1".equals(s)){
			this.comefrom="来自Android客户端";
		}else if("0".equals(s)){
			this.comefrom="来自网页版";
		}else if("2".equals(s)){
			this.comefrom="来自iPhone客户端";
		}else{
			this.comefrom="来自网页版";
		}
	}

	public String getApptypeid() {
		return apptypeid;
	}

	public void setApptypeid(String apptypeid) {
		this.apptypeid = apptypeid;
	}

	public String getApptype() {
		return apptype;
	}

	public void setApptype(String appype) {
		this.apptype = appype;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserhead() {
		return userhead;
	}

	public void setUserhead(String userImage) {
		this.userhead = userImage;
	}

	public String getListid() {
		return listid;
	}

	public void setListid(String listid) {
		this.listid = listid;
	}
    
    public List<Config> getConfiglist() {
		return configlist;
	}

	public void setConfiglist(List<Config> configs) {
		this.configlist = configs;
	}

    public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	
	
	public String getMessagetype() {
		return messagetype;
	}

	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}

	public MiniBlog getChildAbs() {
		return childAbs;
	}

	public void setChildAbs(MiniBlog child) {
		this.childAbs = child;
	}

	public String getUserName() {
		return username;
	}

	public void setUsername(String author) {
		this.username = author;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String authorid) {
		this.ids = authorid;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public Integer getFowardnum() {
        return fowardnum;
    }

    public void setFowardnum(Integer repeatcount) {
        this.fowardnum = repeatcount;
    }

    public Integer getReplynum() {
        return replynum;
    }

    public void setReplynum(Integer commentcount) {
        this.replynum = commentcount;
    }

    public Integer getCollectionnum() {
		return collectionnum;
	}
	public void setCollectionnum(Integer collectionnum) {
		this.collectionnum = collectionnum;
	}
	public String getCreatetime() {
        return createtime;
    }
    public String getTimeLong() {
        return timeLong;
    }
    public void setCreatetime(Long time) {
        this.createtime = TimeUtils.getTimeMinute(time);
        this.timeLong = String.valueOf(time);
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
	public String getUserhead150() {
		return userhead150;
	}
	public void setUserhead150(String userImage150) {
		this.userhead150 = userImage150;
	}
	public String getFromspace() {
		return fromspace;
	}
	public void setFromspace(String fromspace) {
		this.fromspace = fromspace;
	}
    public boolean equals(MiniBlog miniBlog) {
        if (this.getId() == miniBlog.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public long getToptime() {
        return toptime;
    }

    public void setToptime(long toptime) {
        this.toptime = toptime;
    }

    /**
     * 动态是否置顶
     * @return
     */
    public boolean isUp() {
        return this.getToptime() > 0 ? true : false;
    }
}