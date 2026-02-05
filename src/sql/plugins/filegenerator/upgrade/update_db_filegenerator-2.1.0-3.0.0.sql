-- liquibase formatted sql
-- changeset filegenerator:update_db_filegenerator-2.1.0-3.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url='ti ti-file-export' WHERE id_right='VIEW_TEMP_FILES';