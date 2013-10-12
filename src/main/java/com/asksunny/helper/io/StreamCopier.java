package com.asksunny.helper.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StreamCopier {

	
	public final static long MEM_SIZE_1GB = 1024 * 1024 * 1024;
	public final static long MEM_SIZE_2GB = MEM_SIZE_1GB * 2;
	public final static long MEM_SIZE_3GB = MEM_SIZE_1GB * 3;
	public final static long MEM_SIZE_4GB = MEM_SIZE_1GB * 4;
	public final static long MEM_SIZE_5GB = MEM_SIZE_1GB * 5;
	public final static long MEM_SIZE_10GB = MEM_SIZE_1GB * 10;
	public final static long MEM_SIZE_1MB = 1024 * 1024;
	public final static long MEM_SIZE_10MB = MEM_SIZE_1MB *10;
	public final static long MEM_SIZE_30MB = MEM_SIZE_1MB *30;	
	public final static long MEM_SIZE_1K = 1024;
	
	
	
	public static void copy(InputStream in, OutputStream out) throws IOException
	{
		copy( in, 0, 0, out);
	}
	
	public static void copy(InputStream in, long offset, long len, OutputStream out) throws IOException
	{
		
		if(offset>0){
			in.skip(offset);
		}
		if(len<=0) len = Long.MAX_VALUE;
		byte[] buf = new byte[determineBufferSize()];
		
		long sent = 0;
		int rlen = 0;
		while((rlen=in.read(buf))!=-1 && sent<len){
			long left = len - sent;
			if(rlen>left){
				out.write(buf, 0, (int)left);
				sent += left;
			}else{
				out.write(buf, 0, rlen);
				sent += rlen;
			}			
		}
		out.flush();		
	}
	
	
	private static int determineBufferSize()
	{
		long freeMem = Runtime.getRuntime().freeMemory();
		int bufferSize = (int)MEM_SIZE_1K;
		if(freeMem>MEM_SIZE_10GB){
			bufferSize = (int)(MEM_SIZE_1MB * 500);
		}else if(freeMem>MEM_SIZE_5GB){
			bufferSize = (int)(MEM_SIZE_1MB * 200);
		}else if(freeMem>MEM_SIZE_1GB){
			bufferSize = (int)(MEM_SIZE_1MB * 100);
		}else if(freeMem>MEM_SIZE_30MB){
			bufferSize = (int)(MEM_SIZE_1MB *10);
		}else if(freeMem>MEM_SIZE_10MB){
			bufferSize = (int)(MEM_SIZE_1MB *3);
		}else{
			bufferSize = (int)(MEM_SIZE_1MB);
		}
		return bufferSize;
	}
	
	
	private StreamCopier() {
		
	}

	

}
