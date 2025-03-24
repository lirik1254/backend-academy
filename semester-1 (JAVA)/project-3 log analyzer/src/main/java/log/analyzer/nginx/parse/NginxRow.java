package log.analyzer.nginx.parse;

import java.time.LocalDateTime;

public record NginxRow(String ip, LocalDateTime localDateTime, String requestedResource,
                       String responseCode, Integer bodyBytesSent,
                       String client) {}
