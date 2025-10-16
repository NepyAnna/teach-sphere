#!/bin/bash

DATABASE_URL="${DATABASE_URL}"

user="teach_sphere"
pass="jZpu4D1INu1n6hxLjgQcndehiMdx1AV5"
db="teach_sphere"

# host та port
host="dpg-d3o1tjre5dus73ac3iig-a.frankfurt-postgres.render.com"
port="5432"

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