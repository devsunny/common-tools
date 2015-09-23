package com.asksunny.helper.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileSpliter {

	public static InputStream[] splitInputStream(String path, char delimiter,
			int maxBlocks, long minBlockSize) throws Exception {
		return splitInputStream(new File(path), delimiter, maxBlocks, minBlockSize);
	}

	public static InputStream[] splitInputStream(File file, char delimiter,
			int maxBlocks, long minBlockSize) throws Exception {
		FileBlock[] blocks = split(file, delimiter, maxBlocks, minBlockSize);
		InputStream[] ins = new InputStream[blocks.length];
		for (int i = 0; i < ins.length; i++) {
			ins[i] = new FileBlockInputStream(blocks[i]);
		}
		return ins;
	}

	public static InputStream[] splitInputStream(String path, int maxBlocks,
			long minBlockSize) throws Exception {
		return splitInputStream(path, '\n', maxBlocks, minBlockSize);
	}

	public static FileBlock[] split(String path, int maxBlocks,
			long minBlockSize) throws Exception {
		return split(path, '\n', maxBlocks, minBlockSize);
	}

	public static FileBlock[] split(String path, char delimiter, int maxBlocks,
			long minBlockSize) throws IOException {
		return split(new File(path), delimiter, maxBlocks, minBlockSize);
	}

	public static FileBlock[] split(File file, char delimiter, int maxBlocks,
			long minBlockSize) throws IOException {
		FileBlock[] ret = null;
		if (!file.exists()) {
			throw new FileNotFoundException(String.format(
					"File [%s] not exists.", file.toString()));
		}
		long totalLength = file.length();
		int numBlocks = maxBlocks;
		long nb = totalLength / minBlockSize
				+ ((totalLength % minBlockSize > 0) ? 1 : 0);
		if (nb < maxBlocks) {
			numBlocks = (int) nb;
		}
		if (numBlocks == 0) {
			FileBlock fb = new FileBlock(file, 0, 0);
			return new FileBlock[] { fb };
		}

		long blockSize = totalLength / numBlocks;
		FileInputStream fin = null;
		ArrayList<FileBlock> blocks = new ArrayList<FileBlock>();
		try {
			fin = new FileInputStream(file);
			int c = -1;
			long sl = blockSize;
			long start = 0;
			long readBytes = 0;
			do {
				if (sl > 0) {
					fin.skip(sl);
					sl = 0;
				}
				c = fin.read();
				readBytes++;
				if (c == '\n') {
					long len = blockSize + readBytes;
					FileBlock fb = new FileBlock(file, start, len);
					blocks.add(fb);
					start = start + len;
					if (start + blockSize > totalLength) {
						fb = new FileBlock(file, start, totalLength - start);
						blocks.add(fb);
						c = -1;
						break;
					}
					sl = blockSize;
					readBytes = 0;
				} else if (c != -1) {

				}
			} while (c != -1);

		} finally {
			if (fin != null)
				fin.close();
		}
		ret = new FileBlock[blocks.size()];
		ret = blocks.toArray(ret);
		return ret;
	}

	public FileSpliter() {

	}

}
