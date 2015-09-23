package com.asksunny.helper.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class LocalDividableFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public InputStream[] openStream(long maxLength, boolean lazyOpen) throws IOException
	{
		long length = this.length();
		long cnt = length / maxLength;
		long remain = length % maxLength;
		cnt = (remain>0)? cnt+1:cnt;		
		return openStream((int)cnt, lazyOpen);
	}
	
	
	public InputStream[] openStream(long maxLength) throws IOException
	{
		return openStream(maxLength, false);
	}
	
	
	
	public InputStream[] openStream(int number) throws IOException
	{
		return openStream(number, false);
	}
	
	
	public InputStream[] openStream(int number, boolean lazyOpen) throws IOException {
		if (number < 1)
			throw new IOException("number has to be greater than 0");
		if (number == 1) {
			return new InputStream[] { new FileInputStream(this) };
		} else {
			InputStream[] ins = new InputStream[number];
			long length = this.length();
			long remain = length % number;
			long aLen = length / number;
			long start = 0;
			for (int i = 0; i < number; i++) {
				if (i < number - 1) {
					System.out.println(String.format("start %d length %d", start, aLen));
					ins[i] = lazyOpen?new LazyFileFragmentInputStream(this, start, aLen):new FileFragmentInputStream(this, start, aLen);
					start += aLen;
				} else {
					System.out.println(String.format("start %d length %d", start, (aLen	+ remain)));
					ins[i] = lazyOpen?new LazyFileFragmentInputStream(this, start, aLen
							+ remain):new FileFragmentInputStream(this, start, aLen+ remain);
				}
			}
			return ins;
		}
	}

	public LocalDividableFile(String pathname) {
		super(pathname);

	}

	public LocalDividableFile(URI uri) {
		super(uri);
	}

	public LocalDividableFile(String parent, String child) {
		super(parent, child);
	}

	public LocalDividableFile(File parent, String child) {
		super(parent, child);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		LocalDividableFile file = new LocalDividableFile("test.out");
		InputStream[] ins = file.openStream(5, true);
		for(int j=0; j<27; j++){
			for(int i=0; i<5; i++){
				InputStream in = ins[i];			
				int x = in.read();
				System.out.println((char)x);
			}
		}	
		for(int i=0; i<5; i++){
			InputStream in = ins[i];			
			in.close();
		}

	}

}
