package edu.cnm.deepdive.doggoneit.service.dogapi;

import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedDetailsDto;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedSearchResultDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DogApiService {

  @GET("v1/breeds/search")
  Call<List<BreedSearchResultDto>> searchBreeds(@Query("q") String query);

  @GET("v1/breeds/{breedId}")
  Call<BreedDetailsDto> getBreedDetails(@Path("breedId") long breedId);

}
