package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "breed_prediction",
    foreignKeys = {
        @ForeignKey(
            entity = Scan.class,
            parentColumns = "scan_id",
            childColumns = "scan_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = BreedFact.class,
            parentColumns = "breed_fact_id",
            childColumns = "breed_fact_id",
            onDelete = ForeignKey.SET_NULL
        )
    },
    indices = {
        @Index(value = "scan_id"),
        @Index(value = "breed_fact_id")
    }
)
public class BreedPrediction {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "breed_prediction_id")
  private long id;

  private long scanId;

  private Long breedFactId;

  @NonNull
  private String name = "";

  private double probability;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getScanId() {
    return scanId;
  }

  public void setScanId(long scanId) {
    this.scanId = scanId;
  }

  public Long getBreedFactId() {
    return breedFactId;
  }

  public void setBreedFactId(Long breedFactId) {
    this.breedFactId = breedFactId;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  public double getProbability() {
    return probability;
  }

  public void setProbability(double probability) {
    this.probability = probability;
  }
}
