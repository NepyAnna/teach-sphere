#!/bin/bash

DATABASE_URL="${DATABASE_URL}"

# Прибираємо протокол, ділимо на user:pass@host:port/db
proto_removed="${DATABASE_URL#*://}"

user="${proto_removed%%:*}"
rest="${proto_removed#*:}"
pass="${rest%%@*}"
rest="${rest#*@}"
host_port="${rest%%/*}"
db="${rest#*/}"

# host та port
host="${host_port%%:*}"
port="${host_port##*:}"

if [ "$host" = "$port" ]; then
  port="5432"
fi

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