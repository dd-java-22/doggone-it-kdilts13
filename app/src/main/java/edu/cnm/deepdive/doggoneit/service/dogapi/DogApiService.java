package edu.cnm.deepdive.doggoneit.service.dogapi;

import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedFactDto;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedSearchResultDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DogApiService {

  @GET("v1/breeds/search")
  Call<List<BreedSearchResultDto>> searchBreeds(@Query("q") String query);

  // TODO: 4/8/2026 switch to v1/breeds/{BREED_ID} 
  @GET("v1/facts/breed/{breedId}")
  Call<List<BreedFactDto>> getBreedFacts(@Path("breedId") long breedId, @Query("limit") int limit);

}