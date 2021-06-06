package listener;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import globalVal.GlobalVal;
import s3Stream.S3Stream;

public class Listener implements FileAlterationListener{
	
	private final static S3Stream stream = new S3Stream();
	
	private final static GlobalVal val = new GlobalVal();
	
	@Override
	public void onStart(FileAlterationObserver observer) {}
	
	@Override
	// 监听本地文件夹的创建
	public void onDirectoryCreate(File directory) {
		String fileName = directory.toString().replaceAll("\\\\", "/");
		System.out.println("Local: create Dir " + fileName.substring(val.getPath().length() + 1));
		stream.dirCreate(fileName.substring(val.getPath().length() + 1) + "/");
	}
	
	@Override
	public void onDirectoryChange(File directory) {}
	
	@Override
	// 监听本地文件夹的删除
	public void onDirectoryDelete(File directory) {
		String fileName = directory.toString().replaceAll("\\\\", "/");
		System.out.println("Local: delete Dir " + fileName.substring(val.getPath().length() + 1));
		stream.dirDelete(fileName.substring(val.getPath().length() + 1) + "/");
	}
	
	@Override
	// 监听本地文件的添加
	public void onFileCreate(File file) {
		String fileName = file.toString().replaceAll("\\\\", "/");
		System.out.println("Local: create File " + fileName.substring(val.getPath().length() + 1));
		stream.fileUpload(fileName.substring(val.getPath().length() + 1), file);
		
	}
	
	@Override
	// 监听本地文件的修改
	public void onFileChange(File file) {
		String fileName = file.toString().replaceAll("\\\\", "/");
		System.out.println("Local: change File " + fileName.substring(val.getPath().length() + 1));
		stream.fileUpload(fileName.substring(val.getPath().length() + 1), file);
	}
	
	@Override
	// 监听本地文件的删除
	public void onFileDelete(File file) {
		String fileName = file.toString().replaceAll("\\\\", "/");
		System.out.println("Local: delete File " + fileName.substring(val.getPath().length() + 1));
		stream.fileDelete(fileName.substring(val.getPath().length() + 1));
	}
	
	@Override
	public void onStop(FileAlterationObserver observer) {}
}
