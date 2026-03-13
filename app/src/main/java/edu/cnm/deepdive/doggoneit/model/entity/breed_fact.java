package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class breed_fact {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  private long id;

  // TODO: 3/13/2026 unique key
  private Long dogFactsApiId;

  private String name;

  private double weightSi;

  private double heightSi;

  private String bredFor;

  private String breedGroup;

  private String lifeSpan;

  private String temperament;

  private String origin;

  private String referenceImageId;

  private String imageId;

  private int imageWidth;

  private int imageHeight;

  private String imageUrl;

}
