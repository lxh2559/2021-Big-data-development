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
	// ���������ļ��еĴ���
	public void onDirectoryCreate(File directory) {
		String fileName = directory.toString().replaceAll("\\\\", "/");
		System.out.println("Local: create Dir " + fileName.substring(val.getPath().length() + 1));
		stream.dirCreate(fileName.substring(val.getPath().length() + 1) + "/");
	}
	
	@Override
	public void onDirectoryChange(File directory) {}
	
	@Override
	// ���������ļ��е�ɾ��
	public void onDirectoryDelete(File directory) {
		String fileName = directory.toString().replaceAll("\\\\", "/");
		System.out.println("Local: delete Dir " + fileName.substring(val.getPath().length() + 1));
		stream.dirDelete(fileName.substring(val.getPath().length() + 1) + "/");
	}
	
	@Override
	// ���������ļ������
	public void onFileCreate(File file) {
		String fileName = file.toString().replaceAll("\\\\", "/");
		System.out.println("Local: create File " + fileName.substring(val.getPath().length() + 1));
		stream.fileUpload(fileName.substring(val.getPath().length() + 1), file);
		
	}
	
	@Override
	// ���������ļ����޸�
	public void onFileChange(File file) {
		String fileName = file.toString().replaceAll("\\\\", "/");
		System.out.println("Local: change File " + fileName.substring(val.getPath().length() + 1));
		stream.fileUpload(fileName.substring(val.getPath().length() + 1), file);
	}
	
	@Override
	// ���������ļ���ɾ��
	public void onFileDelete(File file) {
		String fileName = file.toString().replaceAll("\\\\", "/");
		System.out.println("Local: delete File " + fileName.substring(val.getPath().length() + 1));
		stream.fileDelete(fileName.substring(val.getPath().length() + 1));
	}
	
	@Override
	public void onStop(FileAlterationObserver observer) {}
}
