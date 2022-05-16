package com.technologies.sunset.filedescriptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class FileDescriptorsController {

    @Autowired
    private GenerateFileDescriptors generateFileDescriptors;

    @PostMapping
    public String testFileDescriptors(@RequestParam(name = "openFiles") int openFiles,
                                      @RequestParam(name = "threadCount") int threadCount,
                                      @RequestParam(name = "holdInSeconds", defaultValue = "10") int holdInSeconds) throws IOException, InterruptedException {
        return generateFileDescriptors.thread(openFiles, threadCount, holdInSeconds);
    }
    @GetMapping
    public String test(){
        return "test";
    }
}
