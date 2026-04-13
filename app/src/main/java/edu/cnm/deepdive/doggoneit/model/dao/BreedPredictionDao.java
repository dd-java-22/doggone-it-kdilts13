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
/**
 * DAO for prediction rows generated from image scans.
 */
public interface BreedPredictionDao {

  /**
   * Inserts one prediction.
   *
   * @param breedPrediction Prediction entity.
   * @return Generated row id.
   */
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

  /**
   * Observes predictions for a scan in display order.
   *
   * @param scanId Parent scan id.
   * @return Live prediction list.
   */
  @Query("SELECT * FROM breed_prediction WHERE scan_id = :scanId ORDER BY rank ASC, probability DESC")
  LiveData<List<BreedPrediction>> findByScanId(long scanId);

  @Query("SELECT * FROM breed_prediction WHERE breed_info_id = :breedInfoId ORDER BY probability DESC")
  LiveData<List<BreedPrediction>> findByBreedInfoId(long breedInfoId);

  @Query("SELECT * FROM breed_prediction ORDER BY probability DESC")
  LiveData<List<BreedPrediction>> findAll();
}
