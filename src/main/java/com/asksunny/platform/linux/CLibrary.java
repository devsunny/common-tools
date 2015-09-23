package com.asksunny.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface CLibrary extends Library{

	CLibrary INSTANCE = (CLibrary) Native.loadLibrary("c", CLibrary.class);

	int inotify_init();

	int inotify_add_watch(int fd, String pathname, int mask);

	int inotify_rm_watch(int fd, int wd);

	int read(int fd, Pointer buf, int size);

	int close(int fd);

	void perror(String message);

}
