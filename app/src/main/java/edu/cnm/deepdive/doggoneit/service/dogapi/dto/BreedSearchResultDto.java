package edu.cnm.deepdive.doggoneit.service.dogapi.dto;

/**
 * DTO representing one breed summary row from Dog API search responses.
 */
public class BreedSearchResultDto {

  private long id;
  private String name;

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

}
