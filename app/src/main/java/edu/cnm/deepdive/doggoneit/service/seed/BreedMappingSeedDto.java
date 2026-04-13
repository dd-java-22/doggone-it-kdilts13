package edu.cnm.deepdive.doggoneit.service.seed;

/**
 * DTO for one JSON seed row mapping a model label to a Dog API breed id.
 */
public class BreedMappingSeedDto {

  private String modelLabel;
  private int dogApiBreedId;

  /**
   * @return Model label from seed data.
   */
  public String getModelLabel() {
    return modelLabel;
  }

  /**
   * @return Dog API breed id from seed data.
   */
  public int getDogApiBreedId() {
    return dogApiBreedId;
  }
}
