package com.example.SoftbinatorProject.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class AmazonService {

    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;
    @Value("${amazonProperties.region}")
    private String region;

    @PostConstruct
    private void initializeAmazon() {
        BasicAWSCredentials creds = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).build();
    }

    public String uploadFile(String folderName, String fileName, File file) {
        String fileUrl = endpointUrl + "/"  + bucketName + "/" + folderName + "/" + fileName;
        uploadFileTos3bucket(folderName, fileName, file);
        file.delete();
        return fileUrl;
    }

    public String upload(String folderName, String fileName, MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + folderName + "/" + fileName;
            uploadFileTos3bucket(folderName, fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileUrl;
    }

    public void deleteFileFroms3bucket(String folderName, String fileName) {
        s3client.deleteObject(bucketName, folderName + "/" + fileName);
    }

    public String renameFileOns3bucket(String folderName, String fileName, String newFileName) {
        String fileUrl = endpointUrl + "/" + bucketName + "/" + folderName + "/" +newFileName;
        s3client.copyObject(bucketName, folderName + "/" + fileName, bucketName, folderName + "/" + newFileName);
        s3client.deleteObject(bucketName, folderName + "/" + fileName);
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private void uploadFileTos3bucket(String folderName, String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, folderName + "/" + fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
    }
}