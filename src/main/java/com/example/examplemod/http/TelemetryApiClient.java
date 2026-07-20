package com.example.examplemod.http;

import com.example.examplemod.blocks.TelemetryNodeBlock;
import com.example.examplemod.models.MachineSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TelemetryApiClient {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static final URI SNAPSHOT_ENDPOINT =
            URI.create("http://localhost:5240/api/machine-snapshots/batch");

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    Instant.class,
                    (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                            new JsonPrimitive(src.toString())
            )
            .create();

        public CompletableFuture<HttpResponse<String>> sendSnapshot(List<MachineSnapshot> snapshots) {
        String json = gson.toJson(snapshots);

        HttpRequest request = HttpRequest.newBuilder(SNAPSHOT_ENDPOINT)
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        return HTTP_CLIENT.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                )
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        System.err.println(
                                "Failed to send telemetry snapshot: " +
                                        exception.getMessage()
                        );
                        return;
                    }

                    if (response.statusCode() < 200 ||
                            response.statusCode() >= 300) {
                        System.err.printf(
                                "Telemetry API returned %d: %s%n",
                                response.statusCode(),
                                response.body()
                        );
                    }
                });
    }
}