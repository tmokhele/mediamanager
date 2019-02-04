package bttc.app.controller;

import bttc.app.model.FileUpload;
import bttc.app.service.MediaManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/cloud")
public class MediaController {

    @Autowired
    MediaManagerService mediaManagerService;


    @GetMapping
    public ResponseEntity<List<String>> get() {
       return ResponseEntity.status(HttpStatus.OK).body(mediaManagerService.getFiles());
    }


    @PostMapping
    public ResponseEntity upload(@RequestBody List<FileUpload> fileUploads) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(mediaManagerService.upLoadFiles(fileUploads));
    }


}