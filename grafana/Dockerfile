FROM grafana/alloy:latest

COPY config.alloy /etc/alloy/config.alloy

# Alloy UI
EXPOSE 12345

# HTTP Protobuf port
EXPOSE 4318

CMD ["run", "/etc/alloy/config.alloy", "--server.http.listen-addr=0.0.0.0:12345", "--storage.path=/var/lib/alloy/data"]