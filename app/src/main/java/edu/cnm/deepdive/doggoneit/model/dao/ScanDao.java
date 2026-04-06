package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import java.util.List;

@Dao
public interface ScanDao {

  @Insert
  long insert(Scan scan);

  @Insert
  List<Long> insert(Scan... scans);

  @Insert
  List<Long> insert(BreedPrediction... breedPredictions);

  @Update
  int update(Scan... scans);

  @Delete
  int delete(Scan... scans);

  @Query("SELECT * FROM scan WHERE scan_id = :scanId")
  LiveData<Scan> findById(long scanId);

  @Transaction
  @Query("SELECT * FROM scan WHERE scan_id = :scanId")
  LiveData<ScanWithPredictions> findWithPredictionsById(long scanId);

  @Query("SELECT * FROM scan WHERE user_profile_id = :userProfileId ORDER BY timestamp DESC")
  LiveData<List<Scan>> findByUserProfileId(long userProfileId);

  @Query("SELECT * FROM scan WHERE user_profile_id = :userProfileId AND favorite = 1 ORDER BY timestamp DESC")
  LiveData<List<Scan>> findFavoritesByUserProfileId(long userProfileId);

  @Query("SELECT * FROM scan ORDER BY timestamp DESC")
  LiveData<List<Scan>> findAll();

  @Transaction
  default long insertWithPredictions(Scan scan, List<BreedPrediction> predictions) {
    long scanId = insert(scan);
    if (predictions != null && !predictions.isEmpty()) {
      BreedPrediction[] predictionArray = predictions.toArray(new BreedPrediction[0]);
      for (BreedPrediction prediction : predictionArray) {
        prediction.setScanId(scanId);
      }
      insert(predictionArray);
    }
    return scanId;
  }
}
