package edu.cnm.deepdive.doggoneit.service.dogapi;

import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedDetailsDto;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedDetailsDto.ImageDto;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedDetailsDto.MeasurementDto;

/**
 * Maps Dog API breed detail responses into local {@link BreedInfo} entities.
 */
public final class BreedDetailsMapper {

  private BreedDetailsMapper() {
  }

  /**
   * Converts a Dog API details DTO into a persisted breed-info entity.
   *
   * @param details Dog API details payload.
   * @return Populated {@link BreedInfo}, or {@code null} when input is null.
   */
  public static BreedInfo toBreedInfo(BreedDetailsDto details) {
    if (details == null) {
      return null;
    }
    BreedInfo info = new BreedInfo();
    info.setDogApiBreedId(details.getId());
    info.setName(details.getName());
    info.setBredFor(details.getBredFor());
    info.setBreedGroup(details.getBreedGroup());
    info.setLifeSpan(details.getLifeSpan());
    info.setTemperament(details.getTemperament());
    info.setOrigin(details.getOrigin());
    info.setReferenceImageId(details.getReferenceImageId());

    MeasurementDto weight = details.getWeight();
    if (weight != null) {
      info.setWeightMetric(weight.getMetric());
      info.setWeightImperial(weight.getImperial());
    }

    MeasurementDto height = details.getHeight();
    if (height != null) {
      info.setHeightMetric(height.getMetric());
      info.setHeightImperial(height.getImperial());
    }

    ImageDto image = details.getImage();
    if (image != null) {
      info.setImageId(image.getId());
      info.setImageWidth(image.getWidth());
      info.setImageHeight(image.getHeight());
      info.setImageUrl(image.getUrl());
    }
    return info;
  }

}
