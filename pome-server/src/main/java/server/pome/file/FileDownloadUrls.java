package server.pome.file;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileDownloadUrls {

  public String profileImage() {
    return "/files/profile";
  }

  public String portfolioAttachment(Long attachmentId) {
    return "/files/portfolio/" + attachmentId;
  }
}
