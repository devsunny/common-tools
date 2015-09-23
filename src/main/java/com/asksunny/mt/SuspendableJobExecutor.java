package com.asksunny.mt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class SuspendableJobExecutor implements Runnable {

	private AtomicBoolean stop = new AtomicBoolean(false);
	private AtomicLong counter = new AtomicLong(0L);
	private AtomicBoolean suspended = new AtomicBoolean(false);
	
	private int id = 0;

	public SuspendableJobExecutor(int i) {
		this.id = i;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExecutorService exepool = Executors.newCachedThreadPool(new JobThreadFactory());
		System.out.println(Thread.MAX_PRIORITY);
		System.out.println(Thread.NORM_PRIORITY);
		System.out.println(Thread.MIN_PRIORITY);
		
		final SuspendableJobExecutor[] jes = new SuspendableJobExecutor[3];
		for (int i = 0; i < jes.length; i++) {
			jes[i] = new SuspendableJobExecutor(i + 1);
			exepool.execute(jes[i]);
			
		}
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		new Thread(new Runnable() {
			@Override
			public void run() {
				jes[1].suspend();
			}
		}).start();
		System.out.println("SUSPENDed");
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		jes[1].resume();
		System.out.println("RESUMED");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
		for (int i = 0; i < jes.length; i++) {
			System.out.println(jes[i].getId() + " CANCELED:" + jes[i].cancel());
		}

	}

	@Override
	public void run() {
		while (!stop.get()) {
			try {
				synchronized(this) {
	                while (suspended.get())
	                    wait();
	            }
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
			
			try {
				
				System.out.println("Hello " + id + " " + counter.addAndGet(1));
				Thread.sleep(3000);
			
			} catch (InterruptedException e) {
				;
			} finally {
				
			}
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean suspend() {
		boolean ret = suspended.compareAndSet(false, true);		
		return ret;
	}

	public synchronized  boolean resume() {
		boolean ret = suspended.compareAndSet(true, false);	
		notify();
		return ret;
	}

	public synchronized boolean cancel() {
		boolean ret = stop.compareAndSet(false, true);
		return ret;
	}

}
