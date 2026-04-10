package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "breed_mapping")
public class BreedMapping {

  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "model_label", collate = ColumnInfo.NOCASE)
  private String modelLabel = "";

  @ColumnInfo(name = "dog_api_breed_id")
  private long dogApiBreedId;

  @NonNull
  public String getModelLabel() {
    return modelLabel;
  }

  public void setModelLabel(@NonNull String modelLabel) {
    this.modelLabel = modelLabel;
  }

  public long getDogApiBreedId() {
    return dogApiBreedId;
  }

  public void setDogApiBreedId(long dogApiBreedId) {
    this.dogApiBreedId = dogApiBreedId;
  }
}
