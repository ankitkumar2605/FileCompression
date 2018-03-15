package com.filecompression.service;

import com.filecompression.Exceptions.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class GZipCompressionService {


    public void doEncode(MultipartFile file, String path) {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String outFileName = path + "/" + filename +  ".gz";

        try {

            System.out.println("Creating the GZIP output stream.");
            GZIPOutputStream out = null;
                out = new GZIPOutputStream(new FileOutputStream(outFileName));

            System.out.println("Opening the input file.");
            System.out.println("Transfering bytes from input file to GZIP Format.");
            byte[] bytes = file.getBytes();
            out.write(bytes);

            System.out.println("Completing the GZIP file");
            out.finish();
            out.close();


        } catch (Exception e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    public void doDecode(MultipartFile file,String path){

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        filename = filename.substring(0,filename.length()-3);
        String outFileName = path + "/" + filename;

        try {

            GZIPInputStream gis = new GZIPInputStream(file.getInputStream());
            FileOutputStream fos = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
