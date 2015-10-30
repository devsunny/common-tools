package com.asksunny.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileValidator {

	private File validateFileList = null;
	private File expandedFileDirectory = null;
	final Pattern FILEBEGIN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}");

	public FileValidator(String validateFileList, String expandedFileDirectory) {
		super();
		this.validateFileList = new File(validateFileList);
		if (!this.validateFileList.exists()) {
			throw new IllegalArgumentException(validateFileList + " does not exist");
		}
		this.expandedFileDirectory = new File(expandedFileDirectory);
		if (!this.expandedFileDirectory.exists()) {
			throw new IllegalArgumentException(expandedFileDirectory + " does not exist");
		}
	}

	public void doValidation() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(validateFileList));
		try {

			String line = null;
			while ((line = br.readLine()) != null) {				
				
				if (line.indexOf("<DIR>") != -1)
					continue;
				
				line = line.trim();
				if (!FILEBEGIN.matcher(line).find())
					continue;
				String[] entry = line.split("\\s+");
				if (entry.length < 2) {
					throw new IllegalArgumentException("Invalid entry:" + line);
				}
				int len = entry.length;
				
				long size = Long.valueOf(entry[len-2].replaceAll(",", "").trim());
				File f = new File(expandedFileDirectory, entry[len-1]);
				if (!f.exists()) {
					throw new IllegalArgumentException(String.format("Missing file:%s", entry[len-1]));
				}
				if (f.length() != size) {
					throw new IllegalArgumentException(
							String.format("File size not match:%s expect %d actual %d", entry[len-1], size, f.length()));
				}
				
				
			}

		} finally {
			if (br != null) {
				br.close();
			}
		}

	}

	public File getValidateFileList() {
		return validateFileList;
	}

	public void setValidateFileList(File validateFileList) {
		this.validateFileList = validateFileList;
	}

	public File getExpandedFileDirectory() {
		return expandedFileDirectory;
	}

	public void setExpandedFileDirectory(File expandedFileDirectory) {
		this.expandedFileDirectory = expandedFileDirectory;
	}

}
