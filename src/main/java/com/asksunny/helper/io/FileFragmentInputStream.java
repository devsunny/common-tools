package com.asksunny.helper.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;



public class FileFragmentInputStream extends InputStream {

	InputStream in;
	long start;
	long length;
	long readed = 0;
	AtomicBoolean closed   = new AtomicBoolean(false);
	
	
	public FileFragmentInputStream(File file, long start, long length) throws IOException
	{
		super();
		this.in = new FileInputStream(file);
		this.start = start;
		this.length = length;
		if(start>0) in.skip(this.start);
	}
	
	
	public FileFragmentInputStream(InputStream in, long start, long length) throws IOException
	{
		super();
		this.in = in;
		this.start = start;
		this.length = length;
		if(start>0) in.skip(this.start);
	}
	
	

	@Override
	public int available() throws IOException {
		
		return in.available();
	}





	@Override
	public long skip(long n) throws IOException {		
		return in.skip(n);
	}





	@Override
	public synchronized void mark(int readlimit) {	
		in.mark(readlimit);
	}





	@Override
	public synchronized void reset() throws IOException {	
		in.reset();
	}





	@Override
	public boolean markSupported() {		
		return in.markSupported();
	}





	@Override
	public void close() throws IOException {	
		if(closed.compareAndSet(false, true)){
			in.close();
		}
	}

	@Override
	public int read() throws IOException {
		if(length>0 && readed>=length) {			
			this.close();
			return -1;
		}
		int ret = this.in.read();
		readed++;
		return ret;
	}

}
