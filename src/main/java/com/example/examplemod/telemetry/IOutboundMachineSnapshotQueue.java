package com.example.examplemod.telemetry;

import java.util.List;

import com.example.examplemod.models.MachineSnapshot;

public interface IOutboundMachineSnapshotQueue {
    public boolean enqueueSnapshot(RetryableOutboundItem<MachineSnapshot> snapshot);
    public boolean enqueueSnapshots(List<RetryableOutboundItem<MachineSnapshot>> snapshots);
    public RetryableOutboundItem<MachineSnapshot> dequeueSnapshot();
    public RetryableOutboundItem<MachineSnapshot>[] dequeueSnapshots();
}
