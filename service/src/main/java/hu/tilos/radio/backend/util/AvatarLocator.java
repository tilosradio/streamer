package hu.tilos.radio.backend.util;

import hu.tilos.radio.backend.Configuration;
import hu.tilos.radio.backend.author.AuthorBasic;

import javax.inject.Inject;
import java.io.File;

public class AvatarLocator {

    @Inject
    @Configuration(name = "upload.dir")
    private String uploadDir;

    public void locateAvatar(AuthorBasic author) {
        if (new File(uploadDir + "/avatar", author.getId() + ".jpg").exists()) {
            author.setAvatar("https://tilos.hu/upload/avatar/" + author.getId() + ".jpg");
        } else if (new File(uploadDir + "/avatar", author.getAlias() + ".jpg").exists()) {
            author.setAvatar("https://tilos.hu/upload/avatar/" + author.getAlias() + ".jpg");
        } else {
            author.setAvatar("https://tilos.hu/upload/avatar/noimage.jpg");
        }
    }


}
