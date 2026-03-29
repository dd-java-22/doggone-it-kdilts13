package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import java.util.List;

@Dao
public interface BreedPredictionDao {

  @Insert
  long insert(BreedPrediction breedPrediction);

  @Insert
  List<Long> insert(BreedPrediction... breedPredictions);

  @Update
  int update(BreedPrediction... breedPredictions);

  @Delete
  int delete(BreedPrediction... breedPredictions);

  @Query("SELECT * FROM breed_prediction WHERE breed_prediction_id = :breedPredictionId")
  LiveData<BreedPrediction> findById(long breedPredictionId);

  @Query("SELECT * FROM breed_prediction WHERE scan_id = :scanId ORDER BY probability DESC")
  LiveData<List<BreedPrediction>> findByScanId(long scanId);

  @Query("SELECT * FROM breed_prediction WHERE breed_fact_id = :breedFactId ORDER BY probability DESC")
  LiveData<List<BreedPrediction>> findByBreedFactId(long breedFactId);

  @Query("SELECT * FROM breed_prediction ORDER BY probability DESC")
  LiveData<List<BreedPrediction>> findAll();
}
