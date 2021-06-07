package initalization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import globalVal.GlobalVal;
import s3Stream.S3Stream;

public class Initalization {
	
	private final static S3Stream stream = new S3Stream();
	
	private final static GlobalVal val = new GlobalVal();

	public static void Download(List<S3ObjectSummary> s3os, File[] fileList) {
		for(int i = 0, k = 0; i < s3os.size(); i++) {
			String fileName = s3os.get(i).getKey();
			while(k < fileList.length && fileName.compareTo(fileList[k].toString().replaceAll("\\\\", "/")
					.substring(val.getPath().length() + 1)) > 0) {
				k++;
			}
			// ͬ��S3�ļ���
			if(fileName.charAt(fileName.length() - 1) == '/') {
				File file = new File(val.getPath() + "/" + fileName);
				if(!file.exists()) {
					file.mkdir();
					System.out.println("Local: " + fileName + " has created");
				}
				else
					System.out.println("Local: " + fileName + " has existed");
				continue;
			}
			// �Ƚ�ͬ���ļ���ETagֵ��LastModifiedֵ
			if(k < fileList.length && fileName.equals(fileList[k].toString().replaceAll("\\\\", "/")
					.substring(val.getPath().length() + 1))) {
				if(stream.Compare(fileList[k], s3os.get(i).getETag()) 
						|| s3os.get(i).getLastModified().getTime() < fileList[k].lastModified()) {
					System.out.println("Local: " + fileName + " has existed");
					k++;
					continue;
				}
				k++;
			}
			GetObjectRequest objectRequest = new GetObjectRequest(val.getBucket(), fileName);
			ObjectMetadata objm = val.getS3().getObjectMetadata(val.getBucket(), fileName);
			long contentLength = objm.getContentLength(), partSize = val.getPartSize();
			FileOutputStream fos = null;
			try {
				// ֱ������С��20MB���ļ�
				if(contentLength < partSize) {
					fos = new FileOutputStream(new File(val.getPath() + "/" + fileName));
					stream.fileDownload(fos ,objectRequest);
					System.out.println("Local: " + fileName + " has downloaded");
					if(fos != null) try {fos.close();} catch (IOException e) {e.printStackTrace();}
				}
				// ��Ƭ���ش���20MB���ļ�
				else {
					boolean hasAbort = false;
					long filePosition = 0;
					int j = 1;
					// �����жϺ󣬼��������ļ����ݲ�׷�ӵ�.part�ļ�
					if(k < fileList.length && fileList[k].toString().replaceAll("\\\\", "/")
						.substring(val.getPath().length() + 1).equals(fileName + ".part")) {
						hasAbort = true;
						filePosition = fileList[k].length() + 1;
						j += filePosition / partSize;
					}
					// �����ص��ļ����ݴ洢��.part�ļ�
					File partFile = new File(val.getPath() + "/" + fileName + ".part");
					fos = new FileOutputStream(partFile, hasAbort);
					for(; filePosition < contentLength; j++) {
						partSize = Math.min(partSize, contentLength - filePosition);
						objectRequest.setRange(filePosition, filePosition + partSize);
						System.out.println("Local: " + fileName + " part " + j + " downloading...");
						stream.fileDownload(fos, objectRequest);
						filePosition += partSize + 1;
						System.out.println("Local: " + fileName + " part " + j + " has downloaded");
					} 
					fos.close(); 
					File temp = new File(val.getPath() + "/" + fileName);
					if(temp.exists()) 
						temp.delete();
					partFile.renameTo(temp);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	public static void Upload(File[] fileList, List<S3ObjectSummary> s3os) {
		for(int i = 0, k = 0; i < fileList.length; i++) {
			String fileName = fileList[i].toString().replaceAll("\\\\", "/").substring(val.getPath().length() + 1);
			while(k < s3os.size() && fileName.compareTo(s3os.get(k).getKey()) > 0) {
				k++;
			}
			if(fileList[i].isFile()) {
				// �Ƚ�ͬ���ļ���ETagֵ��LastModifiedֵ
				if(k < s3os.size() && fileName.equals(s3os.get(k).getKey())) {
					if(stream.Compare(fileList[i], s3os.get(k).getETag()) 
							|| fileList[i].lastModified() < s3os.get(k).getLastModified().getTime()) {
						System.out.println("S3: " + fileName + " has existed");
						k++;
						continue;
					}
					k++;
				}
				// ���˶ϵ�����������.part��.upload�ļ��������ϴ��ļ�ģ��
				if((fileName.length() > 7 && fileName.substring(fileName.length() - 7).equals(".upload")) 
						|| (fileName.length() > 5 && fileName.substring(fileName.length() - 5).equals(".part")))
					continue;
				stream.fileUpload(fileName, fileList[i]);
			}
			// ͬ�������ļ���
			if(fileList[i].isDirectory()) {
				if(k < s3os.size() && s3os.get(k).getKey().equals(fileName + '/')) {
					System.out.println("S3: " + fileName + " has existed");
					k++;
					continue;
				}
				stream.dirCreate(fileName + "/");
			}
		}		
	}
	
}
