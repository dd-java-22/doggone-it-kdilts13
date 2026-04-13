/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.doggoneit.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Persists selected/captured images into app-private storage for saved scans.
 */
public final class ImageStorage {

  private static final String SCANS_DIR = "saved_scans";
  private static final String DEFAULT_EXTENSION = "jpg";
  private static final int BUFFER_SIZE = 8 * 1024;

  private ImageStorage() {
    // Utility class.
  }

  /**
   * Copies an image from content URI into app-private scan storage.
   *
   * @param context Context used to resolve content and files directories.
   * @param sourceUri Source image URI.
   * @return File URI of the saved app-private image.
   * @throws IOException If read/write operations fail.
   */
  public static Uri saveImage(Context context, Uri sourceUri) throws IOException {
    if (context == null) {
      throw new IllegalArgumentException("Context must not be null.");
    }
    if (sourceUri == null) {
      throw new IllegalArgumentException("Source Uri must not be null.");
    }

    Context appContext = context.getApplicationContext();
    File directory = new File(appContext.getFilesDir(), SCANS_DIR);
    if (!directory.exists() && !directory.mkdirs()) {
      throw new IOException("Unable to create app-private scan directory: " + directory);
    }

    String extension = resolveExtension(appContext.getContentResolver(), sourceUri);
    String fileName = "scan_" + UUID.randomUUID() + "." + extension;
    File destination = new File(directory, fileName);

    try (InputStream input = appContext.getContentResolver().openInputStream(sourceUri)) {
      if (input == null) {
        throw new IOException("Content resolver returned null stream for Uri: " + sourceUri);
      }
      try (OutputStream output = new FileOutputStream(destination)) {
        copy(input, output);
      }
    } catch (IOException e) {
      throw new IOException("Failed to save image from Uri: " + sourceUri, e);
    }

    return Uri.fromFile(destination);
  }

  private static String resolveExtension(ContentResolver resolver, Uri sourceUri) {
    String mimeType = resolver.getType(sourceUri);
    String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    return (extension == null || extension.isBlank()) ? DEFAULT_EXTENSION : extension;
  }

  private static void copy(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int read;
    while ((read = input.read(buffer)) != -1) {
      output.write(buffer, 0, read);
    }
  }

}
