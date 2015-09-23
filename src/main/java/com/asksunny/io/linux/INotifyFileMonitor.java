package com.asksunny.io.linux;

import java.util.concurrent.atomic.AtomicBoolean;

import com.asksunny.platform.linux.CLibrary;
import com.sun.jna.Memory;

public class INotifyFileMonitor implements Runnable {

	
	
	
	public final static int IN_ACCESS = 0x00000001; /* File was accessed */
	public final static int IN_MODIFY = 0x00000002; /* File was modified */
	public final static int IN_ATTRIB = 0x00000004; /* Metadata changed */
	public final static int IN_CLOSE_WRITE = 0x00000008; /*
														 * Writtable file was
														 * closed
														 */
	public final static int IN_CLOSE_NOWRITE = 0x00000010; /*
															 * Unwrittable file
															 * closed
															 */
	public final static int IN_OPEN = 0x00000020; /* File was opened */
	public final static int IN_MOVED_FROM = 0x00000040; /* File was moved from X */
	public final static int IN_MOVED_TO = 0x00000080; /* File was moved to Y */
	public final static int IN_CREATE = 0x00000100; /* Subfile was created */
	public final static int IN_DELETE = 0x00000200; /* Subfile was deleted */
	public final static int IN_DELETE_SELF = 0x00000400; /* Self was deleted */
	/* the following are legal events. they are sent as needed to any watch */
	public final static int IN_UNMOUNT = 0x00002000; /* Backing fs was unmounted */
	public final static int IN_Q_OVERFLOW = 0x00004000; /*
														 * Event queued
														 * overflowed
														 */
	public final static int IN_IGNORED = 0x00008000; /* File was ignored */
	/* helper events */
	public final static int IN_CLOSE = (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE);/* close */
	public final static int IN_MOVE = (IN_MOVED_FROM | IN_MOVED_TO);/* moves */
	/* special flags */
	public final static int IN_ISDIR = 0x40000000; /*
													 * event occurred against
													 * dir
													 */
	public final static int IN_ONESHOT = 0x80000000; /* only send event once */
	public final static int IN_ALL_EVENTS = (IN_MODIFY | IN_CLOSE_WRITE
			| IN_CLOSE_NOWRITE | IN_MOVED_FROM | IN_CREATE | IN_DELETE_SELF);

	protected int inotifyHandle = -1;
	protected AtomicBoolean keepRunning = new AtomicBoolean(true);

	public INotifyFileMonitor() {
		inotifyHandle = CLibrary.INSTANCE.inotify_init();
	}

	public synchronized int register(String pathname, int mode) {
		int handle = CLibrary.INSTANCE.inotify_add_watch(inotifyHandle,
				pathname, mode);
		return handle;
	}

	public synchronized boolean unregister(int fmonHandle, int wd) {
		int ret = CLibrary.INSTANCE.inotify_rm_watch(fmonHandle, wd);
		if (ret < 0) {
			return false;
		} else {
			return true;
		}
	}

	public void run() {
		while (keepRunning.get()) {
			Memory buf = new Memory(64 * 1024);
			int cnt = CLibrary.INSTANCE.read(this.inotifyHandle, buf,
					(int) buf.size());
			if (cnt == -1) {
				keepRunning.set(false);
			} else {
				long start = 0;
				while (cnt > 0) {
					int wd2 = buf.getInt(start);
					int mode2 = buf.getInt(start + 4);
					int cookie2 = buf.getInt(start + 8);
					int len2 = buf.getInt(start + 12);
					byte[] names2 = buf.getByteArray(start + 16, len2);
					System.out.println(new String(names2));
					start += 16 + len2;
					cnt -= (16 + len2);
					switch (mode2) {
					case INotifyFileMonitor.IN_CLOSE_WRITE:
						System.out.println("Write Close2.");
						break;
					case IN_ACCESS:
						System.out.println("IN_ACCESS2");
						break;
					case IN_MODIFY:
						System.out.println("IN_MODIFY2");
						break;
					case IN_ATTRIB:
						System.out.println("IN_ATTRIB2");
						break;
					case IN_CLOSE_NOWRITE:
						System.out.println("IN_CLOSE_NOWRITE2");
						break;
					case IN_OPEN:
						System.out.println("IN_OPEN2");
						break;
					case IN_MOVED_FROM:
						System.out.println("IN_MOVED_FROM2:" + cookie2);
						break;
					case IN_MOVED_TO:
						System.out.println("IN_MOVED_TO2:" + cookie2);
						break;
					case IN_DELETE:
						System.out.println("IN_DELETE2");
						break;
					case IN_CREATE:
						System.out.println("IN_CREATE2");
						break;
					case IN_DELETE_SELF:
						System.out.println("IN_DELETE_SELF2");
						break;
					}
				}
				buf.clear();
			}

		}

		if (this.inotifyHandle != -1) {
			CLibrary.INSTANCE.close(this.inotifyHandle);
			this.inotifyHandle = -1;
		}

	}

	@Override
	protected void finalize() throws Throwable {
		if (this.inotifyHandle != -1)
			CLibrary.INSTANCE.close(this.inotifyHandle);
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out.println("Please specify directory");
			System.exit(1);
		}
		INotifyFileMonitor fmon = new INotifyFileMonitor();
		fmon.register(args[0], INotifyFileMonitor.IN_CLOSE_WRITE
				| INotifyFileMonitor.IN_MOVE);

		Thread t = new Thread(fmon);
		t.start();
		t.join();

	}
}
