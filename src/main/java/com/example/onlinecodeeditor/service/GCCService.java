package com.example.onlinecodeeditor.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface GCCService {

     File compile(MultipartFile file) throws IOException;

     void delete(File file) throws IOException;

}
