package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import java.util.List;

@Dao
public interface ScanDao {

  @Insert
  long insert(Scan scan);

  @Insert
  List<Long> insert(Scan... scans);

  @Update
  int update(Scan... scans);

  @Delete
  int delete(Scan... scans);

  @Query("SELECT * FROM scan WHERE scan_id = :scanId")
  LiveData<Scan> findById(long scanId);

  @Query("SELECT * FROM scan WHERE user_profile_id = :userProfileId ORDER BY timestamp DESC")
  LiveData<List<Scan>> findByUserProfileId(long userProfileId);

  @Query("SELECT * FROM scan WHERE user_profile_id = :userProfileId AND favorite = 1 ORDER BY timestamp DESC")
  LiveData<List<Scan>> findFavoritesByUserProfileId(long userProfileId);

  @Query("SELECT * FROM scan ORDER BY timestamp DESC")
  LiveData<List<Scan>> findAll();
}
