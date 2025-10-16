#!/bin/sh

# Accept DATABASE_URL from env (as in Render)
DATABASE_URL="${DATABASE_URL}"

# Parse connection details from DATABASE_URL (bash only, not POSIX sh)
proto="$(echo $DATABASE_URL | sed -n 's,^\(.*\)://.*,\1,p')"
user="$(echo $DATABASE_URL | sed -n 's,.*//\([^:]*\):.*@.*,\1,p')"
pass="$(echo $DATABASE_URL | sed -n 's,.*//[^:]*:\([^@]*\)@.*,\1,p')"
host="$(echo $DATABASE_URL | sed -n 's,.*@\(.*\)/.*,\1,p')"
db="$(echo $DATABASE_URL | sed -n 's,.*/\([^?]*\).*,\1,p')"

# Set default port
port=5432

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