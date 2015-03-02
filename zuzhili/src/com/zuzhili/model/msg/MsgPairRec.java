package com.zuzhili.model.msg;
import com.zuzhili.model.Member;

public class MsgPairRec {
	Member identity;//用户身份
	MsgDetailRec lastmsg;//最新一条
	public Member getIdentity() {
		return identity;
	}
	public void setIdentity(Member identity) {
		this.identity = identity;
	}
	public MsgDetailRec getLastmsg() {
		return lastmsg;
	}
	public void setLastmsg(MsgDetailRec lastmsg) {
		this.lastmsg = lastmsg;
	}
	
	
}
