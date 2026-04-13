package edu.cnm.deepdive.doggoneit.viewmodel;

import androidx.annotation.NonNull;
import java.time.Instant;

/**
 * Projection model for one tile in the saved-scans gallery.
 */
public class ScanGalleryItem {

  private long scanId;
  @NonNull
  private String imagePath = "";
  private Instant timestamp;
  @NonNull
  private String topBreedLabel = "";
  private Double topBreedConfidence;
  private boolean favorite;

  /**
   * @return Scan id backing this gallery tile.
   */
  public long getScanId() {
    return scanId;
  }

  /**
   * @param scanId Scan id backing this gallery tile.
   */
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

  public Double getTopBreedConfidence() {
    return topBreedConfidence;
  }

  public void setTopBreedConfidence(Double topBreedConfidence) {
    this.topBreedConfidence = topBreedConfidence;
  }

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }
}
