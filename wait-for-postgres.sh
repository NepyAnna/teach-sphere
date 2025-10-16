#!/bin/bash
# --- PostgreSQL ---
PGHOST="${POSTGRES_HOST}"

POSTGRES_USER="${POSTGRES_USER}"
PGPASSWORD="${POSTGRES_PASSWORD}"
POSTGRES_DB="${POSTGRES_DB}"
POSTGRES_PORT="${POSTGRES_PORT}"

echo "Waiting for PostgreSQL to be available at $PGHOST:$POSTGRES_PORT/$POSTGRES_DB for user $POSTGRES_USER..."

i=0
until pg_isready -h "$PGHOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
  i=$((i+1))
  if [ $i -ge 30 ]; then
    echo "PostgreSQL is still unreachable after 30 attempts. Exiting."
    exit 1
  fi
  echo "PostgreSQL not available yet. Retrying in 2 seconds... (attempt $i)"
  sleep 2
done

echo "PostgreSQL is available. Starting the app."
exec "$@"