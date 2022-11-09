package com.example.onlinecodeeditor.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class GCCServiceImplTest {

    private GCCServiceImpl gccServiceImpl;

    private final Path root = Paths.get("/Users/ardakuly/Desktop/projects/projectsCV/OnlineCodeEditor/src/main/resources/compiled");
    private File file;

    @Mock private Runtime rt;

    public GCCServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        this.gccServiceImpl = new GCCServiceImpl();
    }

    @BeforeEach
    void setUp() {

        File file = new File("/Users/ardakuly/Desktop/eclipse-workspace/NumberCheker/src/NumberCheker.c");
        this.file = file;
        System.out.println(file.exists());


    }


    @Test
    void compile() throws IOException {

        //given



        String contentType = "text/plain";
        byte[] content = null;

        try {

            content = Files.readAllBytes(Paths.get(file.getPath()));

        } catch (final IOException e) {
            System.out.println("Not translated");
        }

        MultipartFile multipartFile = new MockMultipartFile(file.getName().substring(0, file.getName().length()-1) + "exe",
                file.getName(), contentType, content);


        Path location = root.resolve(multipartFile.getOriginalFilename());
        String compiled = location.toString().substring(0, location.toString().length() - 1);


        //when

        File result = gccServiceImpl.compile(multipartFile);

         
    }
}