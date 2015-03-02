package com.zuzhili.bussiness.socket;

/**
 * 定义抽象接口
 * @author harry
 */
public abstract interface SocketEvent{
	
  public abstract void onConnect(boolean paramBoolean);

  public abstract void onClose(boolean paramBoolean);

  public abstract void onCommand();

  public abstract void onRawData();
}