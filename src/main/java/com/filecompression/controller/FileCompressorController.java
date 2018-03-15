package com.filecompression.controller;

import com.filecompression.service.GZipCompressionService;
import com.filecompression.service.HuffmanService;
import com.filecompression.service.LZWCompressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileCompressorController {

    @Autowired
    private HuffmanService huffmanService;

    @Autowired
    private GZipCompressionService gZipCompressionService;

    @Autowired
    private LZWCompressionService lzwCompressionService;

    @PostMapping(value = "/upload")
    public String handleFileUpload(@RequestParam("uploadFile") MultipartFile file,
                                   @RequestParam("path") String path,
                                   @RequestParam("algorithm") String algorithm,
                                   @RequestParam("type") String type,
                                   RedirectAttributes redirectAttributes) {
        try {
            if(type.equals("C")) {
                if (algorithm.equals("Huffman")) {
                    huffmanService.doEncode(file, path);
                    redirectAttributes.addFlashAttribute("result","File Compressed Successfully");
                }else if (algorithm.equals("GZIP")) {
                    gZipCompressionService.doEncode(file, path);
                    redirectAttributes.addFlashAttribute("result","File Compressed Successfully");
                }else if (algorithm.equals("LZW")) {
                    lzwCompressionService.doEncode(file, path);
                    redirectAttributes.addFlashAttribute("result","File Compressed Successfully");
                }
            }else{
                if (algorithm.equals("Huffman")) {
                    huffmanService.doDecode(file, path);
                    redirectAttributes.addFlashAttribute("result","File Decompressed Successfully");
                }else if (algorithm.equals("GZIP")) {
                    gZipCompressionService.doDecode(file, path);
                    redirectAttributes.addFlashAttribute("result","File Decompressed Successfully");
                }else if (algorithm.equals("LZW")) {
                    lzwCompressionService.doDecode(file, path);
                    redirectAttributes.addFlashAttribute("result","File Decompressed Successfully");
                }
            }
        }catch (Exception e){
          e.printStackTrace();
          redirectAttributes.addFlashAttribute("result","Some error occured");
        }
        return "redirect:/home";
    }

}
