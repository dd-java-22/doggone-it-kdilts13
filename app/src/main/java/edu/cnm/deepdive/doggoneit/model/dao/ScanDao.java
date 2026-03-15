package edu.cnm.deepdive.doggoneit.model.dao;

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
  Scan findById(long scanId);

  @Query("SELECT * FROM scan WHERE user_id = :userId ORDER BY timestamp DESC")
  List<Scan> findByUserId(long userId);

  @Query("SELECT * FROM scan WHERE user_id = :userId AND favorite = 1 ORDER BY timestamp DESC")
  List<Scan> findFavoritesByUserId(long userId);

  @Query("SELECT * FROM scan ORDER BY timestamp DESC")
  List<Scan> findAll();
}
