#!/bin/sh

# --- PostgreSQL ---
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

echo "PostgreSQL is available."

# --- Redis ---
REDIS_URL="${REDIS_URL:-redis://default:YXZSLIFDMrhVxppnSQqtq5TR4kUOfnoe@redis-13510.c55.eu-central-1-1.ec2.redns.redis-cloud.com:13510}"

echo "Waiting for Redis to be available at $REDIS_URL..."

i=0
until redis-cli -u "$REDIS_URL" PING | grep -q PONG; do
  i=$((i+1))
  if [ $i -ge 30 ]; then
    echo "Redis is still unreachable after 30 attempts. Exiting."
    exit 1
  fi
  echo "Redis not available yet. Retrying in 2 seconds... (attempt $i)"
  sleep 2
done

echo "Redis is available."

echo "Starting the app."
exec "$@"