package bttc.app.controller;

import bttc.app.model.FileUpload;
import bttc.app.service.MediaManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/cloud")
public class MediaController {

    @Autowired
    MediaManagerService mediaManagerService;


    @GetMapping("/{documentType}")
    public ResponseEntity<Map<String, String>> get(@PathVariable String documentType) {
       return ResponseEntity.status(HttpStatus.OK).body(mediaManagerService.getFiles(documentType));
    }

    @PostMapping("/remove")
    public ResponseEntity deleteFile(@RequestBody String media){

        return ResponseEntity.status(HttpStatus.OK).body(mediaManagerService.deleteFile(media));
    }

    @PostMapping
    public ResponseEntity upload(@RequestBody List<FileUpload> fileUploads) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(mediaManagerService.upLoadFiles(fileUploads));
    }


}
