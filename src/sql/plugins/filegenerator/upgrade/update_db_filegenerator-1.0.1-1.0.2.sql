-- liquibase formatted sql
-- changeset filegenerator:update_db_filegenerator-1.0.1-1.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET level_right=2 WHERE id_right='VIEW_TEMP_FILES';
