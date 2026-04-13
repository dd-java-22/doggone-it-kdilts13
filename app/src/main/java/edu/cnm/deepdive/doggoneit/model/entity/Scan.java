package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.time.Instant;

@Entity(
    tableName = "scan",
    foreignKeys = {
        @ForeignKey(
            entity = UserProfile.class,
            parentColumns = "user_profile_id",
            childColumns = "user_profile_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = "user_profile_id"),
        @Index(value = "timestamp"),
        @Index(value = "favorite")
    }
)
/**
 * Represents one saved image scan and user-selected metadata for that scan.
 */
public class Scan {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "scan_id")
  private long id;

  @ColumnInfo(name = "user_profile_id")
  private long userProfileId;

  @ColumnInfo(name = "image_path")
  @NonNull
  private String imagePath = "";

  @NonNull
  private Instant timestamp = Instant.now();

  private String note;

  private boolean favorite;

  @ColumnInfo(name = "selected_breed_label")
  private String selectedBreedLabel;

  @ColumnInfo(name = "selected_breed_confidence")
  private Double selectedBreedConfidence;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserProfileId() {
    return userProfileId;
  }

  public void setUserProfileId(long userProfileId) {
    this.userProfileId = userProfileId;
  }

  @NonNull
  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(@NonNull String imagePath) {
    this.imagePath = imagePath;
  }

  @NonNull
  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(@NonNull Instant timestamp) {
    this.timestamp = timestamp;
  }

  @NonNull
  public String getNote() {
    return note;
  }

  public void setNote(@NonNull String note) {
    this.note = note;
  }

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  public String getSelectedBreedLabel() {
    return selectedBreedLabel;
  }

  public void setSelectedBreedLabel(String selectedBreedLabel) {
    this.selectedBreedLabel = selectedBreedLabel;
  }

  public Double getSelectedBreedConfidence() {
    return selectedBreedConfidence;
  }

  public void setSelectedBreedConfidence(Double selectedBreedConfidence) {
    this.selectedBreedConfidence = selectedBreedConfidence;
  }
}
