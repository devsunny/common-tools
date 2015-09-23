package com.asksunny.mt;

import java.util.concurrent.ThreadFactory;

public class JobThreadFactory implements ThreadFactory {

	public JobThreadFactory() {
		
	}

	@Override
	public Thread newThread(Runnable r) {
		if(r instanceof DistributedPriorityJob){
			DistributedPriorityJob job = (DistributedPriorityJob)r;
			Thread t =  new Thread(job, job.getName());
			int  p = (job.getPriority()>Thread.MAX_PRIORITY)?Thread.MAX_PRIORITY:job.getPriority();
			t.setPriority(p);
			return t;
		}else{
			Thread t =  new Thread(r);
			t.setPriority(Thread.MIN_PRIORITY);
			return t;
		}
		
	}
	
}
