/*
 * SocketCommand.java，通过Socket获取命令结果的接口
 *
 * 作者：		张卫广
 * 创建日期：	2013/10/16
 * 当前版本：	1.0
 * 修改记录：
 */

package com.zuzhili.bussiness.socket;

/**
 * 通过Socket获取命令结果的接口定义
 */
public interface SocketCommand {
	/** 
	 * 通过Socket获得Command返回结果的事件通知
	 * @param result	命令返回的结果，null表示Socket通讯失败
	 * @param flag	发送命令时指定的标志参数
	 *
	 * 注：抛出异常表示返回的命令格式错误，需要关闭Socket连接
	 */
	public void recvSocketCommand(String[] result, Object flag) throws Exception;
}
