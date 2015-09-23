package com.asksunny.helper.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;



public class LazyFileFragmentInputStream extends InputStream {

	InputStream in;
	long start;
	long length;
	long readed = 0;
	File file = null;
	AtomicBoolean closed   = new AtomicBoolean(false);
	AtomicBoolean opened   = new AtomicBoolean(false);
	
	public LazyFileFragmentInputStream(File file, long start, long length)
			throws IOException {
		super();
		this.start = start;
		this.length = length;
		this.file = file;
	}
	
	protected void open() throws IOException
	{
		if(opened.compareAndSet(false, true)){			
			in = new FileInputStream(file);
			if(start>0) in.skip(start);
		}
	}
	
	
	@Override
	public int available() throws IOException {
		open();
		return in.available();
	}





	@Override
	public long skip(long n) throws IOException {		
		open();
		return in.skip(n);
	}





	@Override
	public synchronized void mark(int readlimit) {			
		throw new UnsupportedOperationException("mark is not supported");
	}





	@Override
	public synchronized void reset() throws IOException {	
		open();
		in.reset();
	}





	@Override
	public boolean markSupported() {		
		return false;
	}





	@Override
	public void close() throws IOException {	
		open();
		if(closed.compareAndSet(false, true)){
			in.close();
		}
	}

	@Override
	public int read() throws IOException {
		open();
		if(length>0 && readed>=length) {			
			this.close();
			return -1;
		}
		int ret = this.in.read();
		readed++;
		return ret;
	}

}
