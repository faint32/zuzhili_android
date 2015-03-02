package com.zuzhili.bussiness.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/***
 * 重新封装Socket的通讯
 * @author harry
 */
public class Socket{
	SocketEvent _Handler    = null;
	java.net.Socket _Socket = null;
	InputStream _InStream   = null;
	OutputStream _OutStream = null;
	SocketReader _Thread    = null;

	long _Timeout   = Long.MAX_VALUE;
	int _Timestamp = 0;
	Vector<String[]> _Command = new Vector<String[]>();
	byte[] _Buffer = null;

	boolean _RawMode = false;
	int _RawDataPtr = 0;
	int _RawFreePtr = 0;

	public String ErrMessage = null;
	public static final int BUF_SIZE = 8192;
	byte[] _SendBuf = new byte[BUF_SIZE];
	int   _SendPtr  = 0;

	int _FlashTime = 0;
	int _FlashSend = 0;

	void sendData( byte c ) throws Exception {
		if (this._SendPtr >= this._SendBuf.length)
			sendData();
		this._SendBuf[this._SendPtr] = c;
		this._SendPtr += 1;
	}

	void sendData() throws Exception {
		if (this._SendPtr > 0) {
			this._OutStream.write(this._SendBuf, 0, this._SendPtr);
			this._OutStream.flush();
		}
		this._SendPtr = 0;
	}

	public Socket() {
		this._Buffer = new byte[8192];
	}


	public Socket(SocketEvent handler) {
		this._Handler = handler;
		this._Buffer = new byte[8192];
	}
	public Socket(int bufsize) {
		this._Buffer = new byte[bufsize];
	}
	public Socket(SocketEvent handler, int bufsize) {
		this._Handler = handler;
		this._Buffer = new byte[bufsize];
	}

	public void connect(String ip, int port) {
		close();

		try {
			_Socket = new java.net.Socket(ip, port);
			_InStream  = _Socket.getInputStream();
			_OutStream = _Socket.getOutputStream();
			_Timestamp = ((int)(System.currentTimeMillis() / 1000L));
			if (_Handler != null)
				_Handler.onConnect(true);
		} catch (Exception e) {
			ErrMessage = ("connect：" + e.toString());
			try {
				if( _Socket != null)
					_Socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if ( _Handler != null)
				_Handler.onConnect(false);
			return;
		}

		this._Thread = new SocketReader(this,  ip , port);
		this._Thread.start(); 
	}

	public void close()
	{
		if (this._Thread != null)
			this._Thread._Exit = true;

		if (this._InStream != null) {
			try {
				this._InStream.close(); 
			} catch (Exception e) {
			} 
		}

		if (this._OutStream != null) {

			try {
				this._OutStream.close(); 
			} catch (Exception e) { } 

			if (this._Socket != null){
				try { 
					this._Socket.close(); 
				} catch (Exception e) { } 

				synchronized (this._Command) {
					this._Command.removeAllElements();
				}

				this._Thread = null;
				this._InStream = null;
				this._OutStream = null;
				this._Socket = null;

				this._SendPtr = 0;
				this._RawDataPtr = 0;
				this._RawFreePtr = 0;
			}
		}
	}

	public boolean isConnected()
	{
		return this._Socket != null;
	}

	public void setTimeout(long timeout)
	{
		this._Timeout = timeout;
	}

	public void setFlashTime(int flash)
	{
		this._FlashTime = flash;
	}

	public boolean isTimeout() {
		if (this._FlashTime > 0) {
			int t = (int)(System.currentTimeMillis() / 1000L);
			if ((t - this._Timestamp > this._FlashTime) && (t - this._FlashSend > this._FlashTime)) {
				try {
					synchronized (this) {
						this._OutStream.write(0);
						this._OutStream.flush();
					}
				} catch (Exception e) {
					return true;
				}
				this._FlashSend = t;
			}
		}
		return System.currentTimeMillis() / 1000L - this._Timestamp > this._Timeout;
	}

	public boolean hasCommand()
	{
		return this._Command.size() > 0;
	}

	public String[] getCommand()
	{
		synchronized (this._Command) {
			if (this._Command.size() == 0)
				return null;
			String[] arr = (String[])this._Command.firstElement();
			this._Command.removeElementAt(0);
			return arr;
		}
	}

	public boolean putCommand(String[] cmd) {
		synchronized (this) {
			try {
				if (cmd != null)
					for (int i = 0; i < cmd.length; i++) {
						if (i > 0)
							sendData((byte)32);
						String s = cmd[i];
						if (s == null)
							s = "";
						if (s.length() == 0) {
							sendData((byte)34);
							sendData((byte)34);
						}
						else {
							byte[] arr = s.getBytes("UTF-8");
							int n = arr.length - 1;
							while ((n >= 0) &&  (arr[n] != 32)) {
								n--;
							}

							if (n >= 0)
								sendData((byte)34);
							for (int x = 0; x < arr.length; x++) {
								if ((arr[x] == 34) && (n >= 0))
									sendData((byte)92);
								sendData(arr[x]);
							}
							if (n >= 0)
								sendData((byte)34);
						}
					}
				sendData((byte)0);
				sendData();
				return true;
			} catch (Exception e) {
				this.ErrMessage = ("putCommand([])：" + e.toString()); 
				close();
				return false;
			}
		}
	}

	public boolean putCommand(String cmd) {
		synchronized (this) {
			try {
				if ((cmd != null) &&  (cmd.length() != 0))
					this._OutStream.write(cmd.getBytes("UTF-8"));
				this._OutStream.write(0);
				this._OutStream.flush();
				return true;
			} catch (Exception e) {
				this.ErrMessage = ("putCommand()：" + e.toString()); 
				close();
				return false;
			}
		}
	}

	public void setRawMode(boolean mode) {
		this._RawMode = mode;
		this._RawDataPtr = 0;
		this._RawFreePtr = 0;
		if (this._Thread != null)
			this._Thread._Size = 0;
	}

	public boolean isRawMode() {
		return this._RawMode;
	}

	public int hasRawData() {
		synchronized (this._Buffer) {
			if (this._RawDataPtr <= this._RawFreePtr) {
				return this._RawFreePtr - this._RawDataPtr;
			}
			return this._Buffer.length - this._RawDataPtr + this._RawFreePtr;
		}
	}

	public int getRawData(byte[] buf, int offset, int len) {
		int n = 0;
		synchronized (this._Buffer) {
			if (this._RawDataPtr <= this._RawFreePtr) {
				for (; this._RawDataPtr < this._RawFreePtr; len--) {
					if (len == 0)
						return n;
					buf[(offset++)] = this._Buffer[(this._RawDataPtr++)];
					n++;
				}
				return n;
			}

			for (; this._RawDataPtr < this._Buffer.length; len--) {
				if (len == 0)
					return n;
				buf[(offset++)] = this._Buffer[(this._RawDataPtr++)];
				n++;
			}

			for (this._RawDataPtr = 0; this._RawDataPtr < this._RawFreePtr; len--) {
				if (len == 0)
					return n;
				buf[(offset++)] = this._Buffer[(this._RawDataPtr++)];
				n++;
			}
		}
		return n;
	}

	public boolean putRawData(byte[] buf, int offset, int len) {
		synchronized (this) {
			try {
				this._OutStream.write(buf, offset, len);
				this._OutStream.flush();
				return true;
			} catch (Exception e) {
				this.ErrMessage = ("putRawData()：" + e.toString());
				close();
				return false;
			}
		}
	}
}