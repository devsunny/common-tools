package com.asksunny.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.asksunny.cli.utils.CLIOptionAnnotationBasedBinder;
import com.asksunny.cli.utils.annotation.CLIOptionBinding;
import com.asksunny.helper.io.StreamCopier;

public class RemoteDataStreamer {

	@CLIOptionBinding(shortOption = 'H', longOption = "host", hasValue = true, description = "Binding Address or remote receiver hostname, this option is required for sender.")	
	String host;
	
	@CLIOptionBinding(shortOption = 'p', longOption = "port", hasValue = true, description = "TCP Port used to streaming data")	
	int port;
	
	@CLIOptionBinding(shortOption = 'f', longOption = "file", hasValue = true, description = "Source or destination file path, if file does not exists, application will act as receiver.")		
	String filePath;
	
	@CLIOptionBinding(shortOption = 'h', longOption = "help", hasValue = false, description = "print this menu")
	boolean help = false;
	
	@CLIOptionBinding(shortOption = 'v', longOption = "version", hasValue = false, description = "print this menu")
	boolean version = false;
	
	
	@CLIOptionBinding(shortOption = 'z', longOption = "compress", hasValue = false, description = "Compress Data or not")
	boolean compress = false;
	
	
	public static final String VERSION = "Remote Data Streamer version 1.0 (MIT License) - Sunny Liu";
	
	
	public RemoteDataStreamer()
	{	
		
		
	}

	
	public void send() throws Exception
	{
		try(
			Socket client = SocketFactory.getDefault().createSocket(getHost(), getPort());		
			OutputStream out = client.getOutputStream();			
			FileInputStream fin = new FileInputStream(getFilePath()))
			{			
				StreamCopier.copy(fin, out);			
			}		
	}
	
	public void sendCompress() throws Exception
	{
		try(
			Socket client = SocketFactory.getDefault().createSocket(getHost(), getPort());		
			OutputStream out = client.getOutputStream();
			GZIPOutputStream gout = new GZIPOutputStream(out);
			FileInputStream fin = new FileInputStream(getFilePath()))
			{			
				StreamCopier.copy(fin, gout);			
			}		
	}
	
	
	public void receive() throws Exception
	{		
			
		try(
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(getPort());	
		Socket client = server.accept();		
		InputStream in = client.getInputStream();		
		FileOutputStream fout = new FileOutputStream(getFilePath()))
		{			
			StreamCopier.copy(in, fout);			
		}		
		
	}
	
	public void receiveCompress() throws Exception
	{		
			
		try(
		ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(getPort());	
		Socket client = server.accept();		
		InputStream in = client.getInputStream();
		GZIPInputStream gin = new GZIPInputStream(in);
		FileOutputStream fout = new FileOutputStream(getFilePath()))
		{			
			StreamCopier.copy(gin, fout);			
		}		
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{		
		RemoteDataStreamer streamer = new RemoteDataStreamer();
		Options options = CLIOptionAnnotationBasedBinder.getOptions(streamer);
		CLIOptionAnnotationBasedBinder.bindPosix(options, args, streamer);
		
		if (streamer.isHelp() || streamer.isVersion() || streamer.getFilePath() == null	) {
			HelpFormatter hfmt = new HelpFormatter();			
			if (streamer.isHelp()) {				
				hfmt.printHelp(streamer.getClass().getName() + " <options>", options);
				System.exit(0);
			}else if( streamer.isVersion()){
				System.out.println(VERSION);
				System.exit(0);
			} else {				
				hfmt.printHelp(streamer.getClass().getName() + " <options>", options);
				System.exit(1);
			}
		}
		File f = new File(streamer.getFilePath());
		boolean sender = f.exists() && f.isFile();		
		if( sender && streamer.getHost()==null){			
			System.err.println("Host option is required for sender");			
			HelpFormatter hfmt = new HelpFormatter();
			hfmt.printHelp(streamer.getClass().getName() + " <options>", options);
			System.exit(1);			
		}
		
		if(sender && streamer.isCompress()){
			streamer.sendCompress();
		}else if(sender && !streamer.isCompress()){
			streamer.send();
		}else if(!sender && streamer.isCompress()){
			streamer.receiveCompress();
		}else{
			streamer.receive();
		}
		
		System.out.printf("File %s transfer complete.", streamer.getFilePath());
		
	}



	



	public boolean isCompress() {
		return compress;
	}


	public void setCompress(boolean compress) {
		this.compress = compress;
	}


	public boolean isHelp() {
		return help;
	}







	public void setHelp(boolean help) {
		this.help = help;
	}







	public boolean isVersion() {
		return version;
	}







	public void setVersion(boolean version) {
		this.version = version;
	}







	public String getHost() {
		return host;
	}







	public void setHost(String host) {
		this.host = host;
	}







	public int getPort() {
		return port;
	}







	public void setPort(int port) {
		this.port = port;
	}







	public String getFilePath() {
		return filePath;
	}







	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
	
	

}
