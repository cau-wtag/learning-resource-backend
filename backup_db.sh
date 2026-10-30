#!/bin/bash
set -e

# Load environment variables if .env exists in current or parent directory
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
elif [ -f ../.env ]; then
  export $(grep -v '^#' ../.env | xargs)
fi

BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/db_backup_$TIMESTAMP.sql"

mkdir -p "$BACKUP_DIR"

echo "==> Creating backup for container: learningres-db..."
docker exec -t learningres-db pg_dump -U "$DB_USER" "$DB_NAME" > "$BACKUP_FILE"

echo "==> Backup complete: $BACKUP_FILE"

# Retention policy: remove backup files older than 7 days
find "$BACKUP_DIR" -type f -name "db_backup_*.sql" -mtime +7 -exec rm {} +
echo "==> Retention cleanup finished (retaining last 7 days)."