package com.asksunny.tool;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import com.asksunny.cli.utils.CLIOptionAnnotationBasedBinder;
import com.asksunny.cli.utils.annotation.CLIOptionBinding;

public class Base64Tool {

	@CLIOptionBinding(shortOption = 'd', longOption = "decode", hasValue = false, description = "Encoding/decoding flag")
	boolean decode = false;

	@CLIOptionBinding(shortOption = 'h', longOption = "help", hasValue = false, description = "print this menu")
	boolean help = false;

	@CLIOptionBinding(shortOption = 'i', longOption = "input", hasValue = true, description = "Path to input file that to be encoded or decoded")
	String pathToInput = null;

	@CLIOptionBinding(shortOption = 'o', longOption = "output", hasValue = true, description = "Path to output file")
	String pathToOutput = null;

	public static final int BUFFER_SIZE = 1024 * 1024;
	
	@CLIOptionBinding(shortOption = 'v', longOption = "version", hasValue = false, description = "print this menu")
	boolean version = false;
	
	public static final String VERSION = "Base64 encoder/decoder version 1.0 (MIT License) - Sunny Liu";
	
	

	protected void encode() throws IOException {

		try (FileInputStream fin = new FileInputStream(pathToInput);
				FileOutputStream fout = new FileOutputStream(this.pathToOutput);
				Base64OutputStream b64out = new Base64OutputStream(fout)) {

			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = fin.read(buf)) != -1) {
				b64out.write(buf, 0, len);
			}
			fout.flush();
		}
	}

	protected void decode() throws IOException {

		try (FileInputStream fin = new FileInputStream(pathToInput);
				Base64InputStream b64in = new Base64InputStream(fin);
				FileOutputStream fout = new FileOutputStream(this.pathToOutput)) {
			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = b64in.read(buf)) != -1) {
				fout.write(buf, 0, len);
			}
			fout.flush();
		}
	}

	public Base64Tool() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Base64Tool base64 = new Base64Tool();
		Options options = CLIOptionAnnotationBasedBinder.getOptions(base64);
		CLIOptionAnnotationBasedBinder.bindPosix(options, args, base64);

		if (base64.isHelp() || base64.isVersion() || base64.getPathToInput() == null
				|| base64.getPathToOutput() == null) {
			HelpFormatter hfmt = new HelpFormatter();
			hfmt.printHelp(base64.getClass().getName() + " <options>", options);
			if (base64.isHelp()) {
				System.exit(0);
			}else if( base64.isVersion()){
				System.out.println(VERSION);
				System.exit(0);
			} else {
				System.exit(1);
			}
		}

		if (base64.isDecode()) {
			base64.decode();
		} else {
			base64.encode();
		}

	}

	
	
	
	public boolean isVersion() {
		return version;
	}

	public void setVersion(boolean version) {
		this.version = version;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isDecode() {
		return decode;
	}

	public void setDecode(boolean decode) {
		this.decode = decode;
	}

	public String getPathToInput() {
		return pathToInput;
	}

	public void setPathToInput(String pathToInput) {
		this.pathToInput = pathToInput;
	}

	public String getPathToOutput() {
		return pathToOutput;
	}

	public void setPathToOutput(String pathToOutput) {
		this.pathToOutput = pathToOutput;
	}

}
