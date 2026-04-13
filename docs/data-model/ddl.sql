-- Generated 2026-04-13 15:31:25-0600 for database version 5

CREATE TABLE IF NOT EXISTS `user_profile`
(
    `user_profile_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name`            TEXT                              NOT NULL COLLATE NOCASE,
    `email`           TEXT                              NOT NULL COLLATE NOCASE
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_user_profile_email` ON `user_profile` (`email`);

CREATE TABLE IF NOT EXISTS `scan`
(
    `scan_id`                   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `user_profile_id`           INTEGER                           NOT NULL,
    `image_path`                TEXT                              NOT NULL,
    `timestamp`                 INTEGER                           NOT NULL,
    `note`                      TEXT,
    `favorite`                  INTEGER                           NOT NULL,
    `selected_breed_label`      TEXT,
    `selected_breed_confidence` REAL,
    FOREIGN KEY (`user_profile_id`) REFERENCES `user_profile` (`user_profile_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS `index_scan_user_profile_id` ON `scan` (`user_profile_id`);

CREATE INDEX IF NOT EXISTS `index_scan_timestamp` ON `scan` (`timestamp`);

CREATE INDEX IF NOT EXISTS `index_scan_favorite` ON `scan` (`favorite`);

CREATE TABLE IF NOT EXISTS `breed_prediction`
(
    `breed_prediction_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `scan_id`             INTEGER                           NOT NULL,
    `breed_info_id`       INTEGER,
    `name`                TEXT                              NOT NULL COLLATE NOCASE,
    `probability`         REAL                              NOT NULL,
    `rank`                INTEGER                           NOT NULL,
    FOREIGN KEY (`scan_id`) REFERENCES `scan` (`scan_id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (`breed_info_id`) REFERENCES `breed_info` (`breed_info_id`) ON UPDATE NO ACTION ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS `index_breed_prediction_scan_id` ON `breed_prediction` (`scan_id`);

CREATE INDEX IF NOT EXISTS `index_breed_prediction_breed_info_id` ON `breed_prediction` (`breed_info_id`);

CREATE INDEX IF NOT EXISTS `index_breed_prediction_scan_id_rank` ON `breed_prediction` (`scan_id`, `rank`);

CREATE TABLE IF NOT EXISTS `breed_info`
(
    `breed_info_id`      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `dog_api_breed_id`   INTEGER                           NOT NULL,
    `name`               TEXT COLLATE NOCASE,
    `weight_metric`      TEXT,
    `weight_imperial`    TEXT,
    `height_metric`      TEXT,
    `height_imperial`    TEXT,
    `bred_for`           TEXT,
    `breed_group`        TEXT,
    `life_span`          TEXT,
    `temperament`        TEXT,
    `origin`             TEXT,
    `reference_image_id` TEXT,
    `image_id`           TEXT,
    `image_width`        INTEGER,
    `image_height`       INTEGER,
    `image_url`          TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_breed_info_dog_api_breed_id` ON `breed_info` (`dog_api_breed_id`);

CREATE TABLE IF NOT EXISTS `breed_mapping`
(
    `model_label`      TEXT    NOT NULL COLLATE NOCASE,
    `dog_api_breed_id` INTEGER NOT NULL,
    PRIMARY KEY (`model_label`)
);