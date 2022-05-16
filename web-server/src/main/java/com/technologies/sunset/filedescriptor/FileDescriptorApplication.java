package com.technologies.sunset.filedescriptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class FileDescriptorApplication {

	public static void main(String[] args) throws InterruptedException, IOException {
		SpringApplication.run(FileDescriptorApplication.class, args);
	}

}
