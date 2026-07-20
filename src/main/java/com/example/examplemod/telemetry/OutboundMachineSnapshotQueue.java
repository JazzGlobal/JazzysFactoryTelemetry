package com.example.examplemod.telemetry;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.example.examplemod.models.MachineSnapshot;

public class OutboundMachineSnapshotQueue implements IOutboundMachineSnapshotQueue {
    private final Queue<RetryableOutboundItem<MachineSnapshot>> queue = new LinkedList<>();
    private final int maxSize;
    private final int dequeueSize;
    
    public OutboundMachineSnapshotQueue(int maxSize, int dequeueSize) {
        this.maxSize = maxSize;
        this.dequeueSize = dequeueSize;
    }

    public void enqueueSnapshot(RetryableOutboundItem<MachineSnapshot> snapshot) {
        if (queue.size() >= maxSize) {
            throw new IllegalStateException("Queue is full");
        }
        queue.add(snapshot);
    }

    public RetryableOutboundItem<MachineSnapshot> dequeueSnapshot() {
        return queue.poll();
    }

    public void enqueueSnapshots(RetryableOutboundItem<MachineSnapshot>[] snapshots) {
        int availableSpace = maxSize - queue.size();

        // Enqueue what we can
        queue.addAll(Arrays.asList(Arrays.copyOfRange(snapshots, 0, availableSpace)));

        // If there are more snapshots than available space, throw an exception
        if (snapshots.length > availableSpace) {
            throw new IllegalStateException("Queue is full, cannot enqueue all snapshots.");
        }
    }

    public RetryableOutboundItem<MachineSnapshot>[] dequeueSnapshots() {
        int actualDequeueSize = Math.min(dequeueSize, queue.size());
        RetryableOutboundItem<MachineSnapshot>[] snapshots = new RetryableOutboundItem[actualDequeueSize];
        for (int i = 0; i < actualDequeueSize; i++) {
            snapshots[i] = dequeueSnapshot();
        }
        return snapshots;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
