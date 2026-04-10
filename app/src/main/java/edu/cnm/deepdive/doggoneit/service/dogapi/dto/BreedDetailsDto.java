package edu.cnm.deepdive.doggoneit.service.dogapi.dto;

import com.google.gson.annotations.SerializedName;

public class BreedDetailsDto {

  private long id;
  private String name;
  private MeasurementDto weight;
  private MeasurementDto height;
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
  private ImageDto image;

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

  public MeasurementDto getWeight() {
    return weight;
  }

  public void setWeight(MeasurementDto weight) {
    this.weight = weight;
  }

  public MeasurementDto getHeight() {
    return height;
  }

  public void setHeight(MeasurementDto height) {
    this.height = height;
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

  public ImageDto getImage() {
    return image;
  }

  public void setImage(ImageDto image) {
    this.image = image;
  }

  public static class MeasurementDto {

    private String imperial;
    private String metric;

    public String getImperial() {
      return imperial;
    }

    public void setImperial(String imperial) {
      this.imperial = imperial;
    }

    public String getMetric() {
      return metric;
    }

    public void setMetric(String metric) {
      this.metric = metric;
    }
  }

  public static class ImageDto {

    private String id;
    private Integer width;
    private Integer height;
    private String url;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Integer getWidth() {
      return width;
    }

    public void setWidth(Integer width) {
      this.width = width;
    }

    public Integer getHeight() {
      return height;
    }

    public void setHeight(Integer height) {
      this.height = height;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }

}
