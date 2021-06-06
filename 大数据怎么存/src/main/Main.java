package main;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

import globalVal.GlobalVal;
import initalization.Initalization;
import listener.Listener;

public class Main {
	
	private final static Initalization init = new Initalization();
	
	private final static GlobalVal val = new GlobalVal();

	public static File[] getFileName(File file) {
		File[] fileList = file.listFiles();
		int k = fileList.length;
		for(int i = 0; i < k; i++) {
			if(fileList[i].isDirectory()) {
				File[] res = getFileName(fileList[i]);
				int num = fileList.length + res.length;
				File[] temp = new File[num];
				for(int j = 0; j < num; j++) {
					if(j < fileList.length)
						temp[j] = fileList[j];
					else
						temp[j] = res[j - fileList.length];
				}
				fileList = temp;
			}
		}
		return fileList;
	}
	
	public static void main(String[] args) {
		// ��ȡS3�ļ��б�
		List<S3ObjectSummary> s3os = val.getS3()
				.listObjects(val.getBucket()).getObjectSummaries();
		Collections.sort(s3os, new Comparator<S3ObjectSummary>() {
			@Override
			public int compare(S3ObjectSummary s3os1, S3ObjectSummary s3os2) {
				return s3os1.getKey().compareTo(s3os2.getKey());
			}
		});
		
		// ��ȡ�����ļ��б�
		final File file = new File(val.getPath());
		File[] fileList = getFileName(file);
		Arrays.sort(fileList, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return f1.toString()
						.compareTo(f2.toString());
			}
		});
		
		// ��S3�����ر�������ͬ���ļ�		
		init.Download(s3os, fileList);
		
		// �ӱ����ϴ�S3����ͬ���ļ�
		init.Upload(fileList, s3os);

		// ��ʼ��������
		long interval = TimeUnit.SECONDS.toMillis(1);
		FileAlterationObserver observer = new FileAlterationObserver(val.getPath());
		observer.addListener(new Listener());
		FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
		try {
			monitor.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Initialized Successfully");
	}
}
