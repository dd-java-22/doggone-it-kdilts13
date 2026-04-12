package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "breed_info",
    indices = {
        @Index(value = "dog_api_breed_id", unique = true)
    }
)
public class BreedInfo {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "breed_info_id")
  private long id;

  @ColumnInfo(name = "dog_api_breed_id")
  private long dogApiBreedId;

  @ColumnInfo(collate = ColumnInfo.NOCASE)
  private String name;

  @ColumnInfo(name = "weight_metric")
  private String weightMetric;

  @ColumnInfo(name = "weight_imperial")
  private String weightImperial;

  @ColumnInfo(name = "height_metric")
  private String heightMetric;

  @ColumnInfo(name = "height_imperial")
  private String heightImperial;

  @ColumnInfo(name = "bred_for")
  private String bredFor;

  @ColumnInfo(name = "breed_group")
  private String breedGroup;

  @ColumnInfo(name = "life_span")
  private String lifeSpan;

  private String temperament;

  private String origin;

  @ColumnInfo(name = "reference_image_id")
  private String referenceImageId;

  @ColumnInfo(name = "image_id")
  private String imageId;

  @ColumnInfo(name = "image_width")
  private Integer imageWidth;

  @ColumnInfo(name = "image_height")
  private Integer imageHeight;

  @ColumnInfo(name = "image_url")
  private String imageUrl;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getDogApiBreedId() {
    return dogApiBreedId;
  }

  public void setDogApiBreedId(long dogApiBreedId) {
    this.dogApiBreedId = dogApiBreedId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getWeightMetric() {
    return weightMetric;
  }

  public void setWeightMetric(String weightMetric) {
    this.weightMetric = weightMetric;
  }

  public String getWeightImperial() {
    return weightImperial;
  }

  public void setWeightImperial(String weightImperial) {
    this.weightImperial = weightImperial;
  }

  public String getHeightMetric() {
    return heightMetric;
  }

  public void setHeightMetric(String heightMetric) {
    this.heightMetric = heightMetric;
  }

  public String getHeightImperial() {
    return heightImperial;
  }

  public void setHeightImperial(String heightImperial) {
    this.heightImperial = heightImperial;
  }

  public String getBredFor() {
    return bredFor;
  }

  public void setBredFor(String bredFor) {
    this.bredFor = bredFor;
  }

  public String getBreedGroup() {
    return breedGroup;
  }

  public void setBreedGroup(String breedGroup) {
    this.breedGroup = breedGroup;
  }

  public String getLifeSpan() {
    return lifeSpan;
  }

  public void setLifeSpan(String lifeSpan) {
    this.lifeSpan = lifeSpan;
  }

  public String getTemperament() {
    return temperament;
  }

  public void setTemperament(String temperament) {
    this.temperament = temperament;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getReferenceImageId() {
    return referenceImageId;
  }

  public void setReferenceImageId(String referenceImageId) {
    this.referenceImageId = referenceImageId;
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }

  public Integer getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(Integer imageWidth) {
    this.imageWidth = imageWidth;
  }

  public Integer getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(Integer imageHeight) {
    this.imageHeight = imageHeight;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
