package com.filecompression.service;

import com.filecompression.huffman.HuffmanInputStream;
import com.filecompression.huffman.HuffmanOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class HuffmanService {

    public void doEncode(MultipartFile file, String path) throws IOException {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        File outFile = new File(path + "/" + filename + ".zip");
        InputStream in = file.getInputStream();
        HuffmanOutputStream hout = new HuffmanOutputStream(
                new FileOutputStream(outFile));
        byte buf[] = new byte[4096];
        int len;

        while ((len = in.read(buf)) != -1)
            hout.write(buf, 0, len);

        in.close();
        hout.close();
    }

    public void doDecode(MultipartFile file,String path) throws IOException {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        filename = filename.substring(0,filename.length()-4);
        File outFile = new File(path + "/" + filename);
        HuffmanInputStream hin = new HuffmanInputStream(file.getInputStream());
        OutputStream out = new FileOutputStream(outFile);
        byte buf[] = new byte[4096];
        int len;
        while ((len = hin.read(buf)) != -1)
            out.write(buf, 0, len);
        hin.close();
        out.close();
        System.out.println("Decompression: done");
        System.out.println("Decompressed file size: " + outFile.length());
    }
}
