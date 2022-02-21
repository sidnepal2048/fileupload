package fileupload.fileupload.model;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class FileInfo {
    private String id;
    private String name;
    private String url;
    public FileInfo(String id, String name, String url) {
        this.id=id;
        this.name = name;
        this.url = url;
    }
}