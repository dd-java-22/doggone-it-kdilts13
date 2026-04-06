package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.util.List;

@Dao
public interface UserProfileDao {

  @Insert
  long insert(UserProfile userProfile);

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insertOrIgnore(UserProfile userProfile);

  @Insert
  List<Long> insert(UserProfile... userProfiles);

  @Update
  int update(UserProfile... userProfiles);

  @Delete
  int delete(UserProfile... userProfiles);

  @Query("SELECT * FROM user_profile WHERE user_profile_id = :userProfileId")
  LiveData<UserProfile> findById(long userProfileId);

  @Query("SELECT * FROM user_profile WHERE email = :email")
  LiveData<UserProfile> findByEmail(String email);

  @Query("SELECT * FROM user_profile WHERE email = :email")
  UserProfile findByEmailSync(String email);

  @Query("SELECT * FROM user_profile ORDER BY name")
  LiveData<List<UserProfile>> findAll();
}
