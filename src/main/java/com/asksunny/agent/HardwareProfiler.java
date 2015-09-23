package com.asksunny.agent;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;


public class HardwareProfiler {

	private final Sigar sigar;
    private final int cpuCount = 1;   
    private ProcCpu prevPc = null;
    private double load = 0.0;
    
	
	public HardwareProfiler() {
		this.sigar = new Sigar();		
	}
	
	
	protected void gatherSystemInfo() throws Exception
	{
		
		System.out.println(sigar.getCpuPerc().getSys());		
		System.out.println(sigar.getCpuPerc().getUser());
		System.out.println(sigar.getCpuPerc().getIdle());
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		HardwareProfiler profiler = new HardwareProfiler();
		profiler.gatherSystemInfo();
		
		
		
		
		
		
	}
	
	

}
