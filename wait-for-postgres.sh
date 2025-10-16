#!/bin/bash

POSTGRES_USER="${POSTGRES_USER}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD}"
POSTGRES_DB="${POSTGRES_DB}"
POSTGRES_HOST="${POSTGRES_HOST}"
POSTGRES_PORT="${POSTGRES_PORT}"

pass=POSTGRES_PASSWORD
db=POSTGRES_DB
host=POSTGRES_HOST
port=POSTGRES_PORT

export PGPASSWORD="$pass"

echo "Waiting for PostgreSQL to be available at $host:$port/$db for user $user..."

i=0
until pg_isready -h "$host" -p "$port" -U "$user" -d "$db"; do
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