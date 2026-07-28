package com.example.examplemod.telemetry;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.http.TelemetryApiClient;
import com.example.examplemod.models.MachineSnapshot;

public class MachineSnapshotSender implements IMachineSnapshotSender {

    // Send snapshot should hit the API endpoint for a single snapshot
    // Send snapshots should hit the API endpoint for multiple snapshots
    // On fail, append meta data to the payload and retry sending.

    private IOutboundMachineSnapshotQueue outboundQueue;

    // TODO: Should we create a new instance for each call to "sendSnapshots"? Or should this be injected? Hmmmm .. Injecting makes this easier to test!
    private TelemetryApiClient telemetryClient;
    
    public MachineSnapshotSender(IOutboundMachineSnapshotQueue outboundQueue, TelemetryApiClient telemetryClient) {
        this.outboundQueue = outboundQueue;
        this.telemetryClient = telemetryClient;
    }

    @Override
    public void sendSnapshot() {
        RetryableOutboundItem<MachineSnapshot> snapshot = outboundQueue.dequeueSnapshot();
        if (snapshot == null) {
            return;
        }
        telemetryClient
                .sendSnapshot(new ArrayList<MachineSnapshot>(List.of(snapshot.getPayload())))
                .whenComplete((response, error) -> {
                    if (error != null || response.statusCode() != 200) {
                        handleFailedSnapshot(new RetryableOutboundItem[]{snapshot});
                        return;
                    }
                    ExampleMod.LOGGER.info("Snapshot sent successfully. Status: {}", response.statusCode());
                });
    }

    @Override
    public void sendSnapshots() {
        RetryableOutboundItem<MachineSnapshot>[] snapshots = outboundQueue.dequeueSnapshots();
        if (snapshots == null || snapshots.length == 0) {
            return;
        }
        List<MachineSnapshot> snapshotPayloads = List.of(snapshots).stream().map(item -> item.getPayload()).toList();
        telemetryClient
            .sendSnapshot(snapshotPayloads)
            .whenComplete((response, error) -> {
                if (error != null || response.statusCode() != 200) {
                    // TODO: The API currently doesn't report which snapshots failed and which succeeded in the batch
                    // Batch retry therefore won't be implemented yet. 
                    return;
                }
                ExampleMod.LOGGER.info("Snapshots sent successfully. Status: {}", response.statusCode());
            });
    }

    @Override
    public void handleFailedSnapshot(RetryableOutboundItem<MachineSnapshot>[] snapshots) {
        List<RetryableOutboundItem<MachineSnapshot>> thrownAway = new ArrayList<>();
        for (RetryableOutboundItem<MachineSnapshot> snapshot : snapshots) {
            // Requeue the snapshot if it has not reached the maximum retry count.
            if (snapshot.getRetryCount() < snapshot.getMaxRetries()) {
                snapshot.incrementRetryCount();
                outboundQueue.enqueueSnapshot(snapshot);
            } else {
                thrownAway.add(snapshot);
            }
        }

        if (thrownAway.size() > 0) {
            // Handle thrown away snapshots, e.g., log them or send to a dead-letter queue.
            // TODO: Should we log these? Do we even need to throw here? I imagine it'd be nice for callers to be able to handle these butttttttttttt ... will we actually have other clients?
            throw new RuntimeException("Failed to process snapshots: " + thrownAway.size() + " snapshots were thrown away.");
        }
    }
}
