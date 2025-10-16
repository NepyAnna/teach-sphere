# Use official Redis 7 image
FROM redis:7

# Set container name (Render ignores this, but we keep it for local clarity)
LABEL name="teach-sphere-redis"

# Expose default Redis port
EXPOSE 6379

# Optional: create data volume for persistence
VOLUME ["/data"]

# Add healthcheck (same as in your docker-compose)
HEALTHCHECK --interval=5s --timeout=3s --retries=5 \
  CMD redis-cli ping || exit 1

# Start Redis server
CMD ["redis-server"]