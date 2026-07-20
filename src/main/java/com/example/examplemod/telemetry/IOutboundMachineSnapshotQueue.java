package com.example.examplemod.telemetry;

import com.example.examplemod.models.MachineSnapshot;

public interface IOutboundMachineSnapshotQueue {
    public void enqueueSnapshot(RetryableOutboundItem<MachineSnapshot> snapshot);
    public void enqueueSnapshots(RetryableOutboundItem<MachineSnapshot>[] snapshots);
    public RetryableOutboundItem<MachineSnapshot> dequeueSnapshot();
    public RetryableOutboundItem<MachineSnapshot>[] dequeueSnapshots();
}
