package edu.cnm.deepdive.doggoneit.service.dogapi;

import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedDetailsDto;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedSearchResultDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit contract for The Dog API endpoints used by the app.
 */
public interface DogApiService {

  /**
   * Searches breeds by free-text query.
   *
   * @param query User-entered search text.
   * @return Retrofit call yielding matching breed summaries.
   */
  @GET("v1/breeds/search")
  Call<List<BreedSearchResultDto>> searchBreeds(@Query("q") String query);

  /**
   * Fetches detailed breed facts by Dog API breed id.
   *
   * @param breedId Dog API breed id.
   * @return Retrofit call yielding breed details.
   */
  @GET("v1/breeds/{breedId}")
  Call<BreedDetailsDto> getBreedDetails(@Path("breedId") long breedId);

}
