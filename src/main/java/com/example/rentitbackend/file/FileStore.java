//package com.example.rentitbackend.file;
//
//import com.example.rentitbackend.entity.ProductImage;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class FileStore {
//
//    @Value("${file.dir}")
//    private String fileDir;
//
//    public String getFullPath(String filename) {
//        return fileDir + filename;
//    }
//
//    public List<ProductImage> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
//        List<ProductImage> storeFileResult = new ArrayList<>();
//        for (MultipartFile multipartFile : multipartFiles) {
//            if (!multipartFiles.isEmpty()) {
//                storeFileResult.add(storeFile(multipartFile));
//            }
//        }
//        return storeFileResult;
//    }
//
//    public ProductImage storeFile(MultipartFile multipartFile) throws IOException {
//        if (multipartFile.isEmpty()) {
//            return null;
//        }
//        String uploadFilename = multipartFile.getOriginalFilename();
//        String storeFileName = createStoreFileName(uploadFilename);
//        multipartFile.transferTo(new File(getFullPath(storeFileName)));
//        return new ProductImage(uploadFilename, storeFileName);
//    }
//
//    private String createStoreFileName(String originalFilename) {
//        String ext = extractExt(originalFilename);
//        String uuid = UUID.randomUUID().toString();
//        return uuid + "." + ext;
//    }
//
//    private String extractExt(String originalFilename) {
//        int pos = originalFilename.lastIndexOf(".");
//        return originalFilename.substring(pos + 1);
//    }
//}
