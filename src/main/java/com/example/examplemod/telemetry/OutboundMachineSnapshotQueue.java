package com.example.examplemod.telemetry;

import java.util.LinkedList;
import java.util.List;
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

    public synchronized boolean enqueueSnapshot(RetryableOutboundItem<MachineSnapshot> snapshot) {
        if (queue.size() >= maxSize) {
           return false;
        }
        queue.add(snapshot);
        return true;
    }

    public synchronized RetryableOutboundItem<MachineSnapshot> dequeueSnapshot() {
        return queue.poll();
    }

    public synchronized boolean enqueueSnapshots(List<RetryableOutboundItem<MachineSnapshot>> snapshots) {
        int availableSpace = maxSize - queue.size();

        // Enqueue what we can
        queue.addAll(snapshots.subList(0, Math.min(snapshots.size(), availableSpace)));

        // If there are more snapshots than available space, return false
        if (snapshots.size() > availableSpace) {
            return false;
        }
        return true;
    }

    public synchronized RetryableOutboundItem<MachineSnapshot>[] dequeueSnapshots() {
        int actualDequeueSize = Math.min(dequeueSize, queue.size());
        RetryableOutboundItem<MachineSnapshot>[] snapshots = new RetryableOutboundItem[actualDequeueSize];
        for (int i = 0; i < actualDequeueSize; i++) {
            snapshots[i] = dequeueSnapshot();
        }
        return snapshots;
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}
