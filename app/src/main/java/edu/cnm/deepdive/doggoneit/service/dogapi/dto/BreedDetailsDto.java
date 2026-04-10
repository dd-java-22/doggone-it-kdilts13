package edu.cnm.deepdive.doggoneit.service.dogapi.dto;

import com.google.gson.annotations.SerializedName;

public class BreedDetailsDto {

  private long id;
  private String name;
  @SerializedName("bred_for")
  private String bredFor;
  @SerializedName("breed_group")
  private String breedGroup;
  @SerializedName("life_span")
  private String lifeSpan;
  private String temperament;
  private String origin;
  @SerializedName("reference_image_id")
  private String referenceImageId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

}
