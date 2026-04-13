package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Maps a TensorFlow model output label to a Dog API breed identifier.
 */
@Entity(tableName = "breed_mapping")
public class BreedMapping {

  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "model_label", collate = ColumnInfo.NOCASE)
  private String modelLabel = "";

  @ColumnInfo(name = "dog_api_breed_id")
  private long dogApiBreedId;

  /**
   * @return Raw model label used as mapping key.
   */
  @NonNull
  public String getModelLabel() {
    return modelLabel;
  }

  /**
   * @param modelLabel Raw model label key.
   */
  public void setModelLabel(@NonNull String modelLabel) {
    this.modelLabel = modelLabel;
  }

  /**
   * @return Dog API breed identifier mapped from the model label.
   */
  public long getDogApiBreedId() {
    return dogApiBreedId;
  }

  /**
   * @param dogApiBreedId Dog API breed identifier to store.
   */
  public void setDogApiBreedId(long dogApiBreedId) {
    this.dogApiBreedId = dogApiBreedId;
  }
}
