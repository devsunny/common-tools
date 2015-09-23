package com.asksunny.helper.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileBlockInputStream extends FileInputStream {

	private FileBlock fileBlock = null;
	private long readBytes = 0;
	private long maxLength = 0;
	public FileBlockInputStream(FileBlock fb) throws FileNotFoundException, IOException {
		super(fb.getOrignalFile());
		this.fileBlock = fb;
		super.skip(fb.getStart());
		this.maxLength =  fileBlock.getLength();		
	}

	

	@Override
	public int read(byte[] b, int off, int len) throws IOException {		
		if(readBytes >= this.maxLength) return -1;
		long left = this.maxLength - readBytes;
		int ret = 0;
		if(left>=len){
			ret = super.read(b, off, len);
			readBytes += len;
		}else{
			ret = super.read(b, off, (int)left);
			readBytes += left;
		}
		return ret;
	}

	
	
	
	
}
