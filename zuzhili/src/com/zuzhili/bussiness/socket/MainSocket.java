package com.zuzhili.bussiness.socket;

/*
 * MainSocket.java， 
 * 作者：		 张卫广
 * 创建日期：	2013/10/15
 * 当前版本：	1.0
 * 修改记录：
 */
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.utility.Constants;

import java.util.*;

/**
 * 主服务器Socket通讯实现类
 * @author harry
 */
public class MainSocket extends Thread implements SocketEvent {

    private static OnReceiveDataListener onReceiveDataListener;

	static final Boolean TRUE  = new Boolean(true);
	static final Boolean FALSE = new Boolean(false);
	static final Exception EXCEPTION = new Exception();
    static Socket  Connection = null;	// 主服务器的连接
	boolean Working    = false;	// Socket正在处理过程中
	boolean Connected  = false;	// 是否连接上了Server
	boolean isLogin    = false; // 是否登录成功！
	@SuppressWarnings("unchecked")
	Vector CmdList = new Vector();	// 命令列表

	static final String IP   ="218.244.148.21";
	static final int    Port = 6666;

    private static String mIds;
    private static MainSocket instance;

    public void setOnReceiveDataListener(OnReceiveDataListener onReceiveDataListener) {
        this.onReceiveDataListener = onReceiveDataListener;
    }

    public static MainSocket getInstance(String ids) {
        mIds = ids;
        if (instance  == null) {
            instance = new MainSocket();
        }
        if (!Connection.isConnected()) {
            Connection.connect(IP, Port);
        }
        return instance;
    }

    public static MainSocket getInstance(String ids, OnReceiveDataListener listener) {
        mIds = ids;
        onReceiveDataListener = listener;
        if (instance  == null) {
            instance = new MainSocket();
        }
        if (!Connection.isConnected()) {
            Connection.connect(IP, Port);
        }
        return instance;
    }

    public void ensureSocketConnected() {
        if (!Connection.isConnected()) {
            Connection.connect(IP, Port);
        }
    }

    public boolean isSocketConnected() {
        return Connection.isConnected();
    }

	/**
	 * 构造函数
	 */
	private MainSocket() {
		Connection = new Socket(this, 20 * 1024);
		Connection.setTimeout( 1000 ); // 60秒需要有系统交互，否则系统将自动关闭通讯
		
		if ( !Connected ) {     // 连接服务器
			 Connection.connect( IP , Port); 
		}
		this.start();
	}

	/** Socket连接建立后，由Socket调用
	 * @param flag
	 */
	public void onConnect(boolean flag) {
		synchronized (CmdList) {
			Connected  = flag;
			if ( !flag ) {
				doClose();
			} else{
				loginCommand();
			}
		}
	}

	/**
	 * Socket连接关闭后调用，flag定义如下：
	 *		true：	被连接的对方主动关闭
	 *		false：	读取数据或命令失败导致关闭
	 * 注：
	 *		主动关闭Socket不会发出onClose事件
	 * @param flag
	 */
	public void onClose(boolean flag) {
		synchronized ( CmdList) {
			doClose();
		}
	}

	/** 当Socket从对方接收到一个完整命令后调用 */
	public void onCommand() {
		String[] cmd = Connection.getCommand();
		SocketCommand sc = null;
		Object flag = null;
		synchronized (CmdList) { 
			try {
				//  服务器发送的命令为空
				if ( cmd == null ) {
					throw EXCEPTION;
				}  
				 
				if (cmd[0].equals("欢迎使用组织力Im服务软件") || cmd[0].equals("login")) { // 系统自动消息，和登录消息
					if( cmd[0].equals("login")  ){
						if( cmd[1].equals("false") ){   // 登录失败
							System.out.println("-----------登录失败");
						}else{     // 登录成功
							isLogin = true;
							System.out.println("-----------登录成功");
							doCommand();  // 处理下一个命令
						}
					}
					return;
				}
                else if( cmd[0].equals("sysmessage") || cmd[0].equals("warn")  ){// 系统消息 ，需要通知系统线程处理,比如，好友上线，下线等
					for( int i=0;  i< cmd.length; i++){
						System.out.println("系统自动消息：--------> "+cmd[i]);
					}
					return;
				}else if( cmd[0].equals("error") ){    // 接口错误
					for( int i=0;  i< cmd.length; i++){
						System.out.println("接口调用错误：--------> "+cmd[i]);
					}
				    return;
				}else if( cmd[0].equals("talk") ){     // 好友消息
					if( cmd[0].equals("0") )  // 这个地方0，是自己发送消息后的回调，供发送方判断消息是否发送成功
						return;
					for( int i=0;  i< cmd.length; i++){
						System.out.println("好友对您说：--------> "+cmd[i]);
					}
				} else if (cmd[0].equals("setuserinfo")) {
                    for( int i=0;  i< cmd.length; i++){
                        LogUtils.e("-----------------用户上下线: " + cmd[i] + "-----------------");
                    }
                }
                if (onReceiveDataListener != null) {
                    onReceiveDataListener.onReceiveData(cmd);
                }

			} catch (Exception e) {
				doClose();
				return;
			}
		}

		// 处理下一个命令
		synchronized (CmdList) {   // 移除掉的也是当前的命令，不影响下一个命令的执行
			for (int i = 4; i > 0; i--) {
//                if (!cmd[0].equals("欢迎使用组织力Im服务软件")) {
                    CmdList.removeElementAt(i - 1);
//                }
			}	// 删除当前命令
			Working = false; 
			doCommand();
		}
	}


	/***
	 * 主循环，用于判断连接是否没有异常，如通讯异常或背关闭
	 */
	public void run(){
		while( true ){
			if ( this.Connection.isConnected() )
				if ( this.Connection.isTimeout() ){
					this.onClose(false);
				}
			try {
				Thread.sleep( 1000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}



	/** 当Socket接收到一批原始数据后调用 */
	public void onRawData() {

	}


	/**
	 * 发送一个命令
	 * @param cmd		命令，null表示登录
	 * @param login		是否需要login
	 * @param wait		等待时MessageBox的内容，null表示不等待
	 * @param trace		如果等待，是否在MessageBox中显示通讯过程
	 *
	 * 注：每次调用时参数cmd最好能使用新建的String[]实例，不要重用以前的对象，以免造成命令覆盖！
	 */
	@SuppressWarnings("unchecked")
	public void sendCommand(String[] cmd, boolean login, String wait, boolean trace) {
		synchronized (CmdList) {
			CmdList.addElement(cmd);
			CmdList.addElement(login ? TRUE : FALSE);
			CmdList.addElement(wait);
			CmdList.addElement(trace ? TRUE : FALSE);

			if ( Working ) { // 如果上一个命令没有处理完毕，则不接受新的命令调用  ,但是命令已经放到了队列中
				 return;
			}
			doCommand();
		}
	}

	// 处理当前命令
	void doCommand() {
		if ( CmdList.size() <= 0 ) { // 消息队列为空
			 return;
		}
		
		if( !isLogin ) {  // 用户未登录，或登录失败 ，不接受命令的发送
			 return;
		}

		if (!Working) {   // 当前命令未处理完毕，不接受新的命令
			Working = true;
		}

		boolean login = ((Boolean) CmdList.elementAt(1)).booleanValue();
		if (!login) {	// 当前未登录，且该命令需要登录后执行
			loginCommand();
			return;
		}

		// 往队列中直接压送消息发送命令
		String[] cmd = (String[]) CmdList.elementAt(0);
		System.out.println( "---------------->cmd[0]="+cmd[0] );
		if (!Connection.putCommand(cmd)) {
			doClose();
		}
	}

	// 服务器关闭，主动调用
	void doClose() { 
		Connection.close();
        Connection._Command.clear();
		CmdList.removeAllElements();
		Connected = false;
		Working = false;
	}


	// 执行登录命令
	void loginCommand() {
		if (!Connection.putCommand(buildLoginCommand())) {
			doClose();
		}	 
	}

    String buildLoginCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.IM_CMD_LOGIN)
                .append(Constants.BLANK)
                .append(mIds)
                .append(Constants.BLANK)
                .append("1")
                .append(Constants.BLANK)
                .append("1")
                .append(Constants.BLANK)
                .append("1");
        return builder.toString();
    }

}
