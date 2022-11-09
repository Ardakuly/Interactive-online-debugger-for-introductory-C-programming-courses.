package com.example.onlinecodeeditor.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GCCServiceImpl implements GCCService{

    private final Path root = Paths.get("/Users/ardakuly/Desktop/projects/projectsCV/OnlineCodeEditor/src/main/resources/compiled");

    @Override
    public File compile(MultipartFile file) throws IOException {

            Path location = this.root.resolve(file.getOriginalFilename());

            String compiled = location.toString().substring(0, location.toString().length() - 1) + "exe";

            Files.copy(file.getInputStream(), location);

            Runtime rt = Runtime.getRuntime();

            rt.exec("gcc " + location.toString() + " -o "
                    + compiled);


            File result = new File(compiled);

            return result;

        }

    @Override
    public void delete(File file) throws IOException {

        Files.delete(root.resolve(file.getName()));

    }

}
