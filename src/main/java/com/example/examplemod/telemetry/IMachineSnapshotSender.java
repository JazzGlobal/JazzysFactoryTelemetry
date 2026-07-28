package com.example.examplemod.telemetry;

import com.example.examplemod.models.MachineSnapshot;

public interface IMachineSnapshotSender {
    void sendSnapshot();
    void sendSnapshots();
    void handleFailedSnapshot(RetryableOutboundItem<MachineSnapshot>[] snapshots);
}
