package bttc.app.service.impl;

import bttc.app.model.FileUpload;
import bttc.app.service.MediaManagerService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MediaManagerServiceImpl implements MediaManagerService {

    @Value("${api.name}")
    public String mCloudName;

    @Value("${api.key}")
    String mApiKey;

    @Value("${api.secret}")
    String mApiSecret;

    @Override
    public List<String> upLoadFiles(List<FileUpload> fileUploads) {
        List<String> picURLS = new ArrayList<>();
        Cloudinary c = getCloudinaryClient();
        fileUploads.forEach(aFile ->{
            try {
                File f = Files.createTempFile("temp", aFile.getDocName()).toFile();
                FileOutputStream outputStream = new FileOutputStream(f);
                outputStream.write(aFile.getFile());
                outputStream.close();
                Map params2 = ObjectUtils.asMap("use_filename", true, "unique_filename", false, "folder", String.format("{0}/{1}",aFile.getDocName(),aFile.getFileName()),
                        "public_id", String.format("{0}/{1}",aFile.getDocName(),aFile.getFileName()));
                Map response =  aFile.getDocName().equalsIgnoreCase("image")? c.uploader().upload(f, params2): c.uploader().upload(f,
                        ObjectUtils.asMap("resource_type", aFile.getDocName(),
                                "use_filename", true, "unique_filename", false,"folder", String.format("{0}/{1}",aFile.getDocName(),aFile.getFileName()),
                                "public_id", String.format("{0}/{1}",aFile.getDocName(),aFile.getFileName())));
                JSONObject json = new JSONObject(response);
                picURLS.add(json.getString("url"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return picURLS;
    }

    @Override
    public List<String> getFiles() {
        Cloudinary c = getCloudinaryClient();
        List<String> retUrls = new ArrayList<>();
        try {
            Map response = c.api().resource("", ObjectUtils.asMap("type", "upload"));
            JSONObject json = new JSONObject(response);
            JSONArray ja = json.getJSONArray("resources");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject j = ja.getJSONObject(i);
                retUrls.add(j.getString("url"));
            }
        } catch (Exception e) {
        }
        return retUrls;
    }

    private Cloudinary getCloudinaryClient() {

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", mCloudName,
                "api_key", mApiKey,
                "api_secret", mApiSecret,
                "secure", true));
    }
}
