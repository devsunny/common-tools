package com.asksunny.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class FileServer {

	public FileServer() {

	}

	public static void main(String[] args) throws IOException {
		ServerSocket listener = new ServerSocket(9090);
		try {
			while (true) {				
				Socket socket = listener.accept();	
				DataInputStream din = new DataInputStream(socket.getInputStream());
				int namelen = din.readInt();
				byte[] nbuf = new byte[namelen];
				din.readFully(nbuf);
				FileOutputStream fout = new FileOutputStream(new File(new String(nbuf, Charset.defaultCharset())));
				byte[] fbuf = new byte[1024*1024];
				long stc = din.readLong();
				System.out.println("File length:" + stc);
				long tc = 0;
				int rlen = 0;
				while(tc<stc && rlen!=-1){
					rlen = din.read(fbuf);
					tc += rlen;
					fout.write(fbuf, 0, rlen);					
				}
				fout.flush();
				fout.close();
				System.out.println("Done");
				
			}
		} finally {
			listener.close();
		}
	}

}
