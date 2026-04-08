package edu.cnm.deepdive.doggoneit.viewmodel;

import androidx.annotation.NonNull;
import java.time.Instant;

public class ScanGalleryItem {

  private long scanId;
  @NonNull
  private String imagePath = "";
  private Instant timestamp;
  @NonNull
  private String topBreedLabel = "";

  public long getScanId() {
    return scanId;
  }

  public void setScanId(long scanId) {
    this.scanId = scanId;
  }

  @NonNull
  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(@NonNull String imagePath) {
    this.imagePath = imagePath;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  @NonNull
  public String getTopBreedLabel() {
    return topBreedLabel;
  }

  public void setTopBreedLabel(@NonNull String topBreedLabel) {
    this.topBreedLabel = topBreedLabel;
  }
}
