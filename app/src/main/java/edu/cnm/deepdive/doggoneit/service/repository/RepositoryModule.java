package edu.cnm.deepdive.doggoneit.service.repository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public interface RepositoryModule {

  @Binds
  @Singleton
  GoogleAuthRepository bindGoogleAuthRepository(GoogleAuthRepositoryImpl implementation);

  @Binds
  @Singleton
  UserProfileRepository bindUserProfileRepository(UserProfileRepositoryImpl implementation);

  @Binds
  @Singleton
  ScanRepository bindScanRepository(ScanRepositoryImpl implementation);

  @Binds
  @Singleton
  BreedFactRepository bindBreedFactRepository(BreedFactRepositoryImpl implementation);

}
