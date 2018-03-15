package com.filecompression.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class LZWCompressionService {


    public HashMap<String, Integer> table = new HashMap<String, Integer>();
    private String[] Array_char;
    private int count;



    public void doEncode(MultipartFile file, String path) throws IOException {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String outFileName = path + "/" + filename +  ".zip";


        Array_char = new String[4096];
        for (int i = 0; i < 256; i++) {
            table.put(Character.toString((char) i), i);
            Array_char[i] = Character.toString((char) i);
        }
        count = 256;

        DataInputStream read = new DataInputStream(new BufferedInputStream(file.getInputStream()));

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(outFileName)));

        byte input_byte;
        String temp = "";
        byte[] buffer = new byte[3];
        boolean onleft = true;

        try {

            input_byte = read.readByte();
            int i = new Byte(input_byte).intValue();
            if (i < 0) {
                i += 256;
            }
            char c = (char) i;
            temp = "" + c;

            while (true) {
                input_byte = read.readByte();
                i = new Byte(input_byte).intValue();

                if (i < 0) {
                    i += 256;
                }
                c = (char) i;

                if (table.containsKey(temp + c)) {
                    temp = temp + c;
                } else {
                    String s12 = to12bit(table.get(temp));


                    if (onleft) {
                        buffer[0] = (byte) Integer.parseInt(
                                s12.substring(0, 8), 2);
                        buffer[1] = (byte) Integer.parseInt(
                                s12.substring(8, 12) + "0000", 2);
                    } else {
                        buffer[1] += (byte) Integer.parseInt(
                                s12.substring(0, 4), 2);
                        buffer[2] = (byte) Integer.parseInt(
                                s12.substring(4, 12), 2);
                        for (int b = 0; b < buffer.length; b++) {
                            out.writeByte(buffer[b]);
                            buffer[b] = 0;
                        }
                    }
                    onleft = !onleft;
                    if (count < 4096) {
                        table.put(temp + c, count++);
                    }
                    temp = "" + c;
                }
            }

        } catch (EOFException e) {
            String temp_12 = to12bit(table.get(temp));
            if (onleft) {
                buffer[0] = (byte) Integer.parseInt(temp_12.substring(0, 8), 2);
                buffer[1] = (byte) Integer.parseInt(temp_12.substring(8, 12)
                        + "0000", 2);
                out.writeByte(buffer[0]);
                out.writeByte(buffer[1]);
            } else {
                buffer[1] += (byte) Integer
                        .parseInt(temp_12.substring(0, 4), 2);
                buffer[2] = (byte) Integer
                        .parseInt(temp_12.substring(4, 12), 2);
                for (int b = 0; b < buffer.length; b++) {
                    out.writeByte(buffer[b]);
                    buffer[b] = 0;
                }
            }
            read.close();
            out.close();
        }

    }

    public String to12bit(int i) {
        String temp = Integer.toBinaryString(i);
        while (temp.length() < 12) {
            temp = "0" + temp;
        }
        return temp;
    }

    public int getvalue(byte b1, byte b2, boolean onleft) {
        String temp1 = Integer.toBinaryString(b1);
        String temp2 = Integer.toBinaryString(b2);
        while (temp1.length() < 8) {
            temp1 = "0" + temp1;
        }
        if (temp1.length() == 32) {
            temp1 = temp1.substring(24, 32);
        }
        while (temp2.length() < 8) {
            temp2 = "0" + temp2;
        }
        if (temp2.length() == 32) {
            temp2 = temp2.substring(24, 32);
        }

        if (onleft) {
            return Integer.parseInt(temp1 + temp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(temp1.substring(4, 8) + temp2, 2);
        }

    }

    public void doDecode(MultipartFile file, String path) throws IOException {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        filename = filename.substring(0,filename.length()-4);
        String outFileName = path + "/" + filename;

        Array_char = new String[4096];
        for (int i = 0; i < 256; i++) {
            table.put(Character.toString((char) i), i);
            Array_char[i] = Character.toString((char) i);
        }
        count = 256;

        DataInputStream in = new DataInputStream(new BufferedInputStream(file.getInputStream()));

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(outFileName)));

        int currword, priorword;
        byte[] buffer = new byte[3];
        boolean onleft = true;
        try {


            buffer[0] = in.readByte();
            buffer[1] = in.readByte();

            priorword = getvalue(buffer[0], buffer[1], onleft);
            onleft = !onleft;
            out.writeBytes(Array_char[priorword]);

            while (true) {

                if (onleft) {
                    buffer[0] = in.readByte();
                    buffer[1] = in.readByte();
                    currword = getvalue(buffer[0], buffer[1], onleft);
                } else {
                    buffer[2] = in.readByte();
                    currword = getvalue(buffer[1], buffer[2], onleft);
                }
                onleft = !onleft;
                if (currword >= count) {

                    if (count < 4096)
                        Array_char[count] = Array_char[priorword]
                                + Array_char[priorword].charAt(0);
                    count++;
                    out.writeBytes(Array_char[priorword]
                            + Array_char[priorword].charAt(0));
                } else {

                    if (count < 4096)
                        Array_char[count] = Array_char[priorword]
                                + Array_char[currword].charAt(0);
                    count++;
                    out.writeBytes(Array_char[currword]);
                }
                priorword = currword;
            }

        } catch (EOFException e) {
            in.close();
            out.close();
        }

    }
}