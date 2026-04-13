package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity representing a user profile in the Doggone It application.
 * Renamed from "User" to "UserProfile" to avoid conflicts with the reserved SQL keyword "user".
 * The table name is "user_profile" for clarity and to prevent potential SQL issues.
 */
@Entity(
    tableName = "user_profile",
    indices = {
        @Index(value = "email", unique = true)
    }
)
public class UserProfile {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "user_profile_id")
  private long id;

  @ColumnInfo(collate = ColumnInfo.NOCASE)
  @NonNull
  private String name = "";

  @ColumnInfo(collate = ColumnInfo.NOCASE)
  @NonNull
  private String email = "";

  /**
   * @return Local database identifier.
   */
  public long getId() {
    return id;
  }

  /**
   * @param id Local database identifier.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * @return Display name for the signed-in user.
   */
  @NonNull
  public String getName() {
    return name;
  }

  /**
   * @param name Display name for the signed-in user.
   */
  public void setName(@NonNull String name) {
    this.name = name;
  }

  /**
   * @return Unique user email address.
   */
  @NonNull
  public String getEmail() {
    return email;
  }

  /**
   * @param email Unique user email address.
   */
  public void setEmail(@NonNull String email) {
    this.email = email;
  }
}
