package edu.cnm.deepdive.doggoneit.service.dogapi.dto;

import com.google.gson.annotations.SerializedName;

public class BreedFactDto {

  @SerializedName("fact")
  private String fact;

  public String getFact() {
    return fact;
  }

  public void setFact(String fact) {
    this.fact = fact;
  }

}
