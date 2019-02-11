package bttc.app.service;

import bttc.app.model.FileUpload;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MediaManagerService {
    List<String> upLoadFiles(List<FileUpload> fileUploads) throws IOException;
    Map<String, String> getFiles(String mediaType);
     boolean deleteFile(String url);
}
