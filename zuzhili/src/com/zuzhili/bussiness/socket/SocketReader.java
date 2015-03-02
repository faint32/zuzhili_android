package com.zuzhili.bussiness.socket;

/**
 * 主线程处理InputStream返回的数据
 * @author harry
 *
 */
public class SocketReader extends Thread {
	Socket _Socket = null;
	String _Address = null;
	int    _port    = 0 ;
	boolean _Exit = false;
	int _Size = 0;

	public SocketReader(Socket sock, String addr ,int port) {
		this._Socket = sock;
		this._Address = addr;
		this._port    = port;
	}


	public void run() {

		byte[] buf = new byte[Socket.BUF_SIZE];	// 读缓冲区
		int	ptr = 0;			// 读缓冲区指针
		int	total = 0;			// 读缓冲区的数据长度
		while ( ! _Exit ) {
			int c = 0;		// 当前读到的数据
			try {
				if ( _Socket._RawMode ) {	// 原始数据模式
					synchronized ( _Socket._Buffer ) {
						if ( _Socket._RawDataPtr == 0 && _Socket._RawFreePtr >= _Socket._Buffer.length - 1 )
							c ++;
						else if ( _Socket._RawFreePtr == _Socket._RawDataPtr - 1 )
							c ++;
					}
					if ( c != 0 ) {	// 缓冲区中没有空闲空间了
						if ( _Socket._Handler != null && ! _Exit )
							_Socket._Handler.onRawData();	// 通知取数据
						continue;
					}
				}
				
				// 从连接中读取数据（此处不能使用read(buf)，因为E会等待buf.length个数据到达）
				if ( ptr >= total ) {
					total = _Socket._InStream.available();
					if ( total > 0 ) {
						total = _Socket._InStream.read(buf, 0, total > buf.length ? buf.length : total);
						if ( total == -1 )
							throw new Exception("被对方关闭!");
						ptr = 0;
						c = buf[ptr++];
					} else {
						c = _Socket._InStream.read();
						if ( c == -1 )
							throw new Exception("被对方关闭!");
					}
					// 更新时间戳（在Timeout时间内，必须收到新数据）
					_Socket._Timestamp = (int)(System.currentTimeMillis() / 1000);
				} else
					c = buf[ptr++];
				
				if ( _Socket._RawMode ) {	// 原始数据模式
					synchronized ( _Socket._Buffer ) {
						_Socket._Buffer[_Socket._RawFreePtr++] = (byte)c;
						if ( _Socket._RawFreePtr >= _Socket._Buffer.length )
							_Socket._RawFreePtr = 0;
					}
					if ( _Socket._InStream.available() == 0 )	// 没有后续数据了
						if ( _Socket._Handler != null && ! _Exit )
							_Socket._Handler.onRawData();		// 通知取数据
					continue;
				}
				
				// 非原始数据模式
				if ( c != 0 ) {
					if ( _Size >= _Socket._Buffer.length )
						throw new Exception("接收数据超出缓冲区!");		// 命令尺寸超出缓冲区
					_Socket._Buffer[_Size++] = (byte)c;
					continue;
				}
				
				// 收到结束字符(0)，开始解析命令
				int 	start = 0;					// 缓冲区内有效字符的起始位置
				byte	b;
				for ( ; start < _Size; start ++ ) {
					b = _Socket._Buffer[start];
					//if ( b != 0x0d && b != 0x0a && b != ' ' )	// 过滤掉开始的0x0d、0x0a和' '
					if ( b != ' ' )	// 过滤掉开始的' '
						break;
				}
				for ( ; _Size > start + 1; _Size -- ) {
					b = _Socket._Buffer[_Size-1];
					//if ( b != 0x0d && b != 0x0a && b != ' ' )	// 过滤掉结束的0x0d、0x0a和' '
					if ( b != ' ' )	// 过滤掉结束的' '
						break;
				}
				if ( start >= _Size )
					continue;				// 没有有效命令
				
				// 计算字段的数量
				boolean field = false;				// 当前是否在字段内
				boolean mark = false;				// 字段是否有双引号
				int	n = 0;					// 字段数量
				for ( c = start; c < _Size; c ++ ) {
					b = _Socket._Buffer[c];
					if ( b == ' ' ) {
						if ( ! field )
							continue;				// 字段间的分隔
						if ( mark )
							continue;				// 在字段内
						n ++;
						field = false;				// 开始下一个字段
						continue;
					}
					if ( ! field ) {
						field = true;				// 字段开始
						if ( b == '"' )
							mark = true;			// 双引号开始
						continue;
					}
					if ( b == '"' ) {
						if ( ! mark )
							continue;				// 字段中的双引号，不处理
						if ( _Socket._Buffer[c-1] == '\\' )
							continue;				// 被转义的双引号
						mark = false;
						if ( c == _Size - 1 )
							break;					// 整个命令结尾的双引号
						if ( _Socket._Buffer[c+1] != ' ' )
							throw new Exception("接收命令时分隔符错误!");	// 下个字符不是' '，格式非法
						n ++;
						field = false;				// 开始下一个字段
					}
				}
				if ( mark )
					throw new Exception("接收命令时双引号错误!");			// 双引号未结束，格式非法
				if ( field )
					n ++;							// 最后一个字段
				if ( n == 0 )
					continue;						// 无有效命令
				
				// 命令解码
				String[] cmd = new String[n];
				n = 0;
				int point = start;					// 当前字段的起始位置
				for ( c = start; c < _Size; c ++ ) {
					b = _Socket._Buffer[c];
					if ( b == ' ' ) {
						if ( ! field )
							continue;				// 字段间的分隔
						if ( mark )
							continue;				// 在字段内
						cmd[n++] = new String(_Socket._Buffer, point, c - point, "UTF-8");
						field = false;				// 开始下一个字段
						continue;
					}
					if ( ! field ) {
						field = true;				// 字段开始
						point = c;
						if ( b == '"' ) {
							mark = true;			// 双引号开始
							point ++;
						}
						continue;
					}
					if ( b == '"' ) {
						if ( ! mark )
							continue;				// 字段中的双引号，不处理
						if ( _Socket._Buffer[c-1] == '\\' ) {
							for ( int i = c - 1; i < _Size - 1; i ++ )
								_Socket._Buffer[i] = _Socket._Buffer[i+1];
							c --;
							_Size --;
							continue;				// 被转义的双引号
						}
						mark = false;
						if ( c == point )
							cmd[n++] = "";
						else
							cmd[n++] = new String(_Socket._Buffer, point, c - point, "UTF-8");
						field = false;				// 开始下一个字段
					}
				}
				if ( field )
					cmd[n++] = new String(_Socket._Buffer, point, c - point, "UTF-8");		// 最后一个字段

				// 成功接收命令，保存命令并复位缓冲区
				_Size = 0;
				synchronized ( _Socket._Command ) {
					_Socket._Command.addElement(cmd);
				}
				// 命令到达的事件通知
				if ( _Socket._Handler != null && ! _Exit )
					try { _Socket._Handler.onCommand(); } catch(Exception ee) {}
			} catch(Exception e) {
				_Socket.ErrMessage = "read data：" + e.toString();
				if ( ! _Exit ) {	// 判断是否是主动关闭
					_Socket.close();
					if ( _Socket._Handler != null )
						try { _Socket._Handler.onClose(c == -1); } catch(Exception ee) {}
				}
				break;				// 线程退出
			}
		}
			
			if( this._Socket.ErrMessage != null )
				System.out.println("this._Socket.ErrMessage="+ this._Socket.ErrMessage );
	}
}