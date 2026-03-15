package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.doggoneit.model.entity.User;
import java.util.List;

@Dao
public interface UserDao {

  @Insert
  long insert(User user);

  @Insert
  List<Long> insert(User... users);

  @Update
  int update(User... users);

  @Delete
  int delete(User... users);

  @Query("SELECT * FROM user WHERE user_id = :userId")
  User findById(long userId);

  @Query("SELECT * FROM user WHERE email = :email")
  User findByEmail(String email);

  @Query("SELECT * FROM user ORDER BY name")
  List<User> findAll();
}
