package com.example.onlinecodeeditor.controller;


import com.example.onlinecodeeditor.service.GCCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/gcc")
public class GCCController {

    public GCCService gccService;

    @Autowired
    public GCCController(GCCService gccService) {
        this.gccService = gccService;
    }

    @GetMapping("/compile")
    public ResponseEntity<File> compile(@RequestBody MultipartFile file) throws IOException {

        return new ResponseEntity<>(gccService.compile(file), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam File file) throws IOException {

        gccService.delete(file);

        return new ResponseEntity<>(HttpStatus.OK);
    }



}
