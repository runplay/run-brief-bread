package run.brief.util;

import java.io.File;

public class FileItem extends File {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean isDir;
	public String file;
	public int icon;
	//public long size;
	//public Date created;
	//public String absolutePath;
	//public boolean isChecked;
/*
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
*/
	public FileItem(String filepath) {
		super(filepath);
		this.file = this.getName();
		//this.icon = Files.getFileRIcon(this.file);
		//this.absolutePath=absolutePath;
	}
	public FileItem(String filepath, Integer icon) {
		super(filepath);
		this.file = this.getName();
		this.icon = icon;
		//this.absolutePath=absolutePath;
	}

	public FileItem(String filename, Integer icon, String path) {
		super(path+ File.separator+filename);
		this.file = filename;
		this.icon = icon;
		//this.absolutePath=absolutePath;
	}

	@Override
	public String toString() {
		return this.getAbsolutePath();
	}
}