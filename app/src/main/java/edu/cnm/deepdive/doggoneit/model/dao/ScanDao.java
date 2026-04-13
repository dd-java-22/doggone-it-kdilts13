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
import edu.cnm.deepdive.doggoneit.viewmodel.ScanGalleryItem;
import java.util.List;

@Dao
/**
 * DAO for scan records and related projection queries used by gallery and detail screens.
 */
public interface ScanDao {

  /**
   * Inserts one scan.
   *
   * @param scan Scan to insert.
   * @return Generated scan id.
   */
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

  /**
   * Observes one scan and its predictions as a single transaction.
   *
   * @param scanId Scan id.
   * @return Live scan-with-predictions stream.
   */
  @Transaction
  @Query("SELECT * FROM scan WHERE scan_id = :scanId")
  LiveData<ScanWithPredictions> findWithPredictionsById(long scanId);

  @Query("SELECT * FROM scan WHERE user_profile_id = :userProfileId ORDER BY timestamp DESC")
  LiveData<List<Scan>> findByUserProfileId(long userProfileId);

  /**
   * Returns gallery projection rows for one user, ordered by most recent scan first.
   *
   * @param userProfileId User profile id.
   * @return Live gallery item list.
   */
  @Query(
      "SELECT "
          + "scan.scan_id AS scanId, "
          + "scan.image_path AS imagePath, "
          + "scan.timestamp AS timestamp, "
          + "scan.favorite AS favorite, "
          + "COALESCE(NULLIF(scan.selected_breed_label, ''), top_prediction.name, '') AS topBreedLabel, "
          + "COALESCE(scan.selected_breed_confidence, top_prediction.probability) AS topBreedConfidence "
          + "FROM scan "
          + "LEFT JOIN breed_prediction AS top_prediction "
          + "ON top_prediction.scan_id = scan.scan_id "
          + "AND top_prediction.rank = 0 "
          + "WHERE scan.user_profile_id = :userProfileId "
          + "ORDER BY scan.timestamp DESC"
  )
  LiveData<List<ScanGalleryItem>> findGalleryItemsByUserProfileId(long userProfileId);

  @Query("SELECT * FROM scan WHERE user_profile_id = :userProfileId AND favorite = 1 ORDER BY timestamp DESC")
  LiveData<List<Scan>> findFavoritesByUserProfileId(long userProfileId);

  @Query("SELECT * FROM scan ORDER BY timestamp DESC")
  LiveData<List<Scan>> findAll();

  /**
   * Inserts a scan and its associated predictions atomically.
   *
   * @param scan Parent scan to insert.
   * @param predictions Prediction list to associate.
   * @return Generated scan id.
   */
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
