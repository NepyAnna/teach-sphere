#!/bin/sh

PGHOST="${POSTGRES_HOST}"
PGPORT="${POSTGRES_PORT}"
PGUSER="${POSTGRES_USER}"
PGPASSWORD="${POSTGRES_PASSWORD}"
PGDATABASE="${POSTGRES_DB}"

export PGPASSWORD

echo "Waiting for PostgreSQL to be available at $PGHOST:$PGPORT/$PGDATABASE..."

i=0
until pg_isready -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE"; do
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