package bttc.app.service;

import bttc.app.model.FileUpload;

import java.io.IOException;
import java.util.List;

public interface MediaManagerService {
    List<String> upLoadFiles(List<FileUpload> fileUploads) throws IOException;
    List<String> getFiles();
}
