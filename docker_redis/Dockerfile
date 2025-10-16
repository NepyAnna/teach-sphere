# Базовий образ Redis
FROM redis:7

# Встановлюємо curl (для healthcheck) і netcat для псевдо-вебсерверу
RUN apt-get update && apt-get install -y netcat curl && rm -rf /var/lib/apt/lists/*

# Експортуємо порти
EXPOSE 6379 8080

# Healthcheck — для Redis
HEALTHCHECK --interval=5s --timeout=3s --retries=5 \
  CMD redis-cli ping || exit 1

# Запускаємо Redis і псевдо-HTTP-сервер паралельно
CMD redis-server & while true; do { echo -e "HTTP/1.1 200 OK\r\n\r\nOK"; } | nc -l -p 8080 -q 1; done