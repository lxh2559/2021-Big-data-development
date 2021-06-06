package s3Stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import java.security.MessageDigest;
import org.apache.commons.codec.digest.DigestUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;

import globalVal.GlobalVal;

import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3Stream {
	
	private final static GlobalVal val = new GlobalVal();
	
	private final static InputStream im = new InputStream() {
		@Override
		public int read() throws IOException {
			return -1;
		}
	};
	private final static ObjectMetadata om = new ObjectMetadata();
	
	// 比较同名文件的ETag值
	public static boolean Compare(File file, String ETag) {
		String str = new String();
		try {
			long partSize = val.getPartSize();
			FileInputStream temp = new FileInputStream(file.getPath());
			if(file.length() > partSize * 4) {
				MessageDigest mD = MessageDigest.getInstance("MD5");
				int part = (int)((file.length() - 1) / partSize) + 1;
				byte[] res = new byte[part * 16];
				byte[] buffer = new byte[(int)partSize];
				int size = 0, k = 0;
				while((size = temp.read(buffer)) != -1) {
					mD.update(buffer, 0, size);
					byte[] b = mD.digest();
					System.arraycopy(b, 0, res, k * 16, 16);
					k++;
				}	
				str = DigestUtils.md5Hex(res) + "-" + part;
			} else
				str = DigestUtils.md5Hex(temp);
			temp.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(str.equals(ETag))
			return true;
		else 
			return false;
	}
	
	public static void fileUpload(String fileName, File file) {
		long contentLength = file.length(), partSize = val.getPartSize();
		try {
			// 直接上传小于20MB的文件
			if(contentLength < partSize * 4) {
				val.getS3().putObject(val.getBucket(), fileName, file);
				System.out.println("S3: " + fileName + " has uploaded");				
			}
			// 分片上传大于20MB的文件
			else {
				ArrayList<PartETag> partETags = new ArrayList<PartETag>();
				InitiateMultipartUploadRequest initRequest = 
						new InitiateMultipartUploadRequest(val.getBucket(), fileName);
				String uploadId = val.getS3().initiateMultipartUpload(initRequest).getUploadId();
				long filePosition = 0;
				int i = 1;
				boolean hasAbort = false;
				// 利用.upload文件存储分片信息
				File uploadFile = new File(val.getPath() + "/" + fileName + ".upload");
				// 上传中断后，读取.upload文件内容继续上传
				if(uploadFile.exists()) {
					Scanner scan = new Scanner(uploadFile);
					uploadId = scan.nextLine();
					while (scan.hasNextLine()) {
						partETags.add(new PartETag(Integer.valueOf(scan.nextLine()), scan.nextLine()));
						filePosition += partSize;
						i += 1;
					}
					hasAbort = true;
					scan.close();					
				}
				FileOutputStream fos = new FileOutputStream(uploadFile, hasAbort);
				if(!hasAbort)
					fos.write((uploadId + "\n").getBytes());
				for(; filePosition < contentLength; i++) {
					partSize = Math.min(partSize, contentLength - filePosition);
					UploadPartRequest uploadPartRequest = new UploadPartRequest()
							.withBucketName(val.getBucket())
							.withKey(fileName)
							.withUploadId(uploadId)
							.withPartNumber(i)
							.withFile(file)
							.withFileOffset(filePosition)
							.withPartSize(partSize);
					System.out.println("S3: " + fileName + " part " + i + " uploading...");
					partETags.add(val.getS3().uploadPart(uploadPartRequest).getPartETag());
					fos.write((String.valueOf(partETags.get(i - 1).getPartNumber()) + "\n").getBytes());
					fos.write((partETags.get(i - 1).getETag() + "\n").getBytes());
					filePosition += partSize;
					System.out.println("S3: " + fileName + " part " + i + " has uploaded");
				}
				fos.close();
				CompleteMultipartUploadRequest compRequest = 
						new CompleteMultipartUploadRequest(val.getBucket(), fileName, uploadId, partETags);
				val.getS3().completeMultipartUpload(compRequest);
				uploadFile.delete();
				val.getS3().listMultipartUploads(new ListMultipartUploadsRequest(val.getBucket())).getMultipartUploads()
				.forEach(item -> val.getS3().abortMultipartUpload(new AbortMultipartUploadRequest
								(val.getBucket(), item.getKey(), item.getUploadId())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	// 删除文件
	public static void fileDelete(String fileName) {
		try {
			val.getS3().deleteObject(val.getBucket(), fileName);
			System.out.println("S3: " + fileName + " has deleted");
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return;
	}
	
	// 下载文件
	public static void fileDownload(FileOutputStream fos ,GetObjectRequest objRequest) {
		S3ObjectInputStream s3is = null;
		S3Object obj = val.getS3().getObject(objRequest);
		try {
			s3is = obj.getObjectContent();
			byte[] read_buf = new byte[64 * 1024];
			int read_len = 0;
			while((read_len = s3is.read(read_buf)) > 0) {
				fos.write(read_buf, 0, read_len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(s3is != null) 
				try {
					s3is.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
		}
		return;
	}	
	
	// 创建文件夹
	public static void dirCreate(String DirName) {
		om.setContentLength(0L);
		try {
			val.getS3().putObject(new PutObjectRequest(val.getBucket(), DirName, im, om));
			System.out.println("S3: " + DirName + " has created");
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return;
	}
	
	// 删除文件夹
	public static void dirDelete(String DirName) {
		try {
			val.getS3().deleteObject(val.getBucket(), DirName);
			System.out.println("S3: " + DirName + " has deleted");
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return;
	}
}
