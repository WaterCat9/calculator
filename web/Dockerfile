# ===== web service: static frontend + nginx reverse proxy =====
# nginx:alpine is a small official image that bundles the nginx web server.
# (It was verified to be pullable from the configured domestic mirror registry.)
FROM nginx:alpine

# COPY the static calculator page into nginx's default html directory.
COPY index.html /usr/share/nginx/html/

# COPY our server block over the default one. It serves index.html and
# proxies /api/* to the backend service by its compose service name.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# EXPOSE documents that the web server listens on port 80 inside the container.
EXPOSE 80
