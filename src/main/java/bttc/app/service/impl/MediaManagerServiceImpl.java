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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
        fileUploads.forEach(aFile -> {
            try {
                File f = Files.createTempFile("temp", aFile.getDocName()).toFile();
                FileOutputStream outputStream = new FileOutputStream(f);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MessageFormat.format("{0}", aFile.getDocName()));
                outputStream.write(aFile.getFile());
                outputStream.close();
                Map params2 = null;
                if (aFile.getDocName().equalsIgnoreCase("image")) {
                    params2 = ObjectUtils.asMap("use_filename", true, "unique_filename",
                            "public_id", MessageFormat.format("{0}/{1}", aFile.getDocName(), aFile.getFileName()));
                }
                Map response = aFile.getDocName().equalsIgnoreCase("image") ? c.uploader().upload(f, params2) : c.uploader().upload(f,
                        ObjectUtils.asMap("resource_type", aFile.getDocName().equalsIgnoreCase("video") ? aFile.getDocName() : "raw",
                                "use_filename", true, "unique_filename", false,
                                "public_id", MessageFormat.format("{0}/{1}", aFile.getDocName(), aFile.getFileName())));
                JSONObject json = new JSONObject(response);
                picURLS.add(json.getString("url"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return picURLS;
    }

    @Override
    public Map<String, String> getFiles(String mediaType) {
        Cloudinary c = getCloudinaryClient();
        String jsonNext = null;
        JSONObject json = null;
        boolean isMoreAvailable = true;
        Map<String, String> files = new HashMap<>();
        try {
            while (isMoreAvailable) {
                Map response = c.api().resources(ObjectUtils.asMap("max_results", 500, "next_cursor", jsonNext, "resource_type", !mediaType.equalsIgnoreCase("audio") ? mediaType : "raw"));
                json = new JSONObject(response);
                if (json.has("next_cursor")) {
                    jsonNext = json.get("next_cursor").toString();
                    isMoreAvailable = true;
                } else {
                    isMoreAvailable = false;
                }
                JSONArray ja = json.getJSONArray("resources");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject j = ja.getJSONObject(i);
                    String[] public_ids = j.getString("public_id").split("/");
                    files.put(public_ids[1], j.getString("url"));
                }
            }
        } catch (Exception e) {
        }
        return files;
    }

    public boolean deleteFile(String url) {
        Cloudinary c = getCloudinaryClient();
        try {
          c.uploader().destroy(url, ObjectUtils.asMap("invalidate", true));
          return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Cloudinary getCloudinaryClient() {

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", mCloudName,
                "api_key", mApiKey,
                "api_secret", mApiSecret,
                "secure", true));
    }
}
