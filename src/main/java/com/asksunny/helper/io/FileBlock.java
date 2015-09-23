package com.asksunny.helper.io;

import java.io.File;
import java.io.FileNotFoundException;

public class FileBlock {

	private long start;
	private long length;
	private File orignalFile;

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;

	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public File getOrignalFile() {
		return orignalFile;
	}

	public void setOrignalFile(File orignalFile) {
		this.orignalFile = orignalFile;
	}

	public FileBlock(String filePath, long start, long length)
			throws FileNotFoundException {
		this(new File(filePath), start, length);
	}

	public FileBlock(File file, long start, long length)
			throws FileNotFoundException {
		super();
		this.orignalFile = file;
		if (!this.orignalFile.exists()) {
			throw new FileNotFoundException(String.format(
					"File [%s] not exists.", file.toString()));
		}

		this.start = start;
		this.length = length;
	}

	public FileBlock() {

	}

	@Override
	public String toString() {
		return "FileBlock [filePath=" + this.orignalFile.toString() + ", start=" + start
				+ ", length=" + length + "]";
	}

}
