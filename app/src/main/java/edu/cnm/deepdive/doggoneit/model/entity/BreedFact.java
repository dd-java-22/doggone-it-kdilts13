package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "breed_fact",
    indices = {
        @Index(value = "dog_facts_api_id", unique = true)
    }
)
public class BreedFact {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "breed_fact_id")
  private long id;

  @ColumnInfo(name = "dog_facts_api_id")
  private long dogFactsApiId;

  @ColumnInfo(name = "name")
  private String name;

  @ColumnInfo(name = "weight_si")
  private Double weightSi;

  @ColumnInfo(name = "height_si")
  private Double heightSi;

  @ColumnInfo(name = "bred_for")
  private String bredFor;

  @ColumnInfo(name = "breed_group")
  private String breedGroup;

  @ColumnInfo(name = "life_span")
  private String lifeSpan;

  @ColumnInfo(name = "temperament")
  private String temperament;

  @ColumnInfo(name = "origin")
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

  public long getDogFactsApiId() {
    return dogFactsApiId;
  }

  public void setDogFactsApiId(long dogFactsApiId) {
    this.dogFactsApiId = dogFactsApiId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getWeightSi() {
    return weightSi;
  }

  public void setWeightSi(Double weightSi) {
    this.weightSi = weightSi;
  }

  public Double getHeightSi() {
    return heightSi;
  }

  public void setHeightSi(Double heightSi) {
    this.heightSi = heightSi;
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
