package com.asksunny.io;

import java.io.IOException;
import java.io.Reader;

public class LookaheadReader extends Reader {

	private Reader reader;
	private char[] lookaheadBuffer;
	private boolean eof = false;
	private int readPos = 0;

	public LookaheadReader(int lhCnt, Reader reader) throws IOException {
		this.reader = reader;
		lookaheadBuffer = new char[lhCnt];
		fillBuffer();
	}

	protected void fillBuffer() throws IOException {
		int len = lookaheadBuffer.length;
		int pos = 0;
		int rlen = 0;
		while ((rlen = reader.read(lookaheadBuffer, pos, len)) > 0) {
			pos = pos + rlen;
			len = len - rlen;
		}
		for (; pos < lookaheadBuffer.length; pos++) {
			lookaheadBuffer[pos] = (char) -1;
		}
		if (rlen == -1) {
			this.eof = true;
		}
	}

	@Override
	public void close() throws IOException {
		if (reader != null) {
			reader.close();
		}
	}

	public int peek(int idx) {
		if (idx >= lookaheadBuffer.length) {
			throw new IllegalArgumentException(String.format("Invalid peekable index %d max peek buffer size is %d",
					idx, this.lookaheadBuffer.length));
		}
		return this.lookaheadBuffer[(readPos + idx) % this.lookaheadBuffer.length];
	}

	public boolean lookaheadMatch(char[] cmp, boolean ignoreCase) {
		if (cmp.length > this.lookaheadBuffer.length) {
			return false;
		}
		for (int i = 0; i < cmp.length; i++) {
			char c1 = cmp[i];
			char c2 = this.lookaheadBuffer[(readPos + i) % this.lookaheadBuffer.length];
			if (c2 == -1)
				return false;
			if (ignoreCase) {
				if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
					return false;
				}
			} else {
				if (c1 != c2) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int rlen = 0;
		int i = 0;
		for (; i < len; i++) {
			char c2 = this.lookaheadBuffer[(readPos + i) % this.lookaheadBuffer.length];
			if (c2 == -1) {
				this.eof = true;
				return rlen;
			} else {
				rlen++;
				cbuf[off++] = c2;
				if (rlen == this.lookaheadBuffer.length) {
					break;
				}
			}
		}

		if (rlen < len && !eof) {
			int nrlen = reader.read(cbuf, off, len - rlen);
			if (nrlen < len - rlen) {
				this.eof = true;
			}
			fillBuffer();
			readPos = 0;
			if (nrlen == -1) {
				return rlen;
			} else {
				return rlen + nrlen;
			}
		} else if (rlen == this.lookaheadBuffer.length) {
			fillBuffer();
			readPos = 0;
			return rlen;
		} else {
			for (i = 0; i < rlen; i++) {
				int c = -1;
				if (!this.eof) {
					c = reader.read();
					this.eof = c == -1;
				} else {
					c = -1;
				}
				this.lookaheadBuffer[(readPos++) % this.lookaheadBuffer.length] = (char) c;
			}
			readPos = readPos % this.lookaheadBuffer.length;
			return rlen;
		}

	}

}
