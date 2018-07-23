package codeu.model.store;

import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.DateTimeZone;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.servlet.http.Part;

public class StorageHelper {

  private static StorageHelper instance;
  private static GcsService gcsService;
  private final static String bucket = "binary-search-three.appspot.com";

  public static StorageHelper getInstance() {
    if (instance == null) {
      instance = new StorageHelper(GcsServiceFactory.createGcsService());
    }
    return instance;
  }

  public static StorageHelper getTestInstance(GcsService gcsService) {
    return new StorageHelper(gcsService);
  }

  private StorageHelper(GcsService gcsService) {
    this.gcsService = gcsService;
  }

  public String uploadImage(Part imagePart) throws IOException {
    // Create unique filename
    DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS-");
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    String dtString = dt.toString(dtf);
    final String fileName = "image" + dtString + imagePart.getSubmittedFileName();
    final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

    // Transform from Part to a byte array
    InputStream inputStream = imagePart.getInputStream();
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    while (inputStream.available() > 0) {
      byteStream.write(inputStream.read());
    }
    byte[] imageBytes = byteStream.toByteArray();

    // Upload image to Google Storage
    gcsService.createOrReplace(
        new GcsFilename(bucket, fileName),
        new GcsFileOptions.Builder().mimeType("image/" + extension).build(),
        ByteBuffer.wrap(imageBytes));

    // Create a fixed dedicated URL that points to the GCS hosted file
    ServingUrlOptions options = ServingUrlOptions.Builder
        .withGoogleStorageFileName("/gs/" + bucket + fileName)
        .imageSize(150)
        .crop(true)
        .secureUrl(true);

    return ImagesServiceFactory.getImagesService().getServingUrl(options);
  }


}
