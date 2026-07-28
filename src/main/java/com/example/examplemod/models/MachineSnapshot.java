package com.example.examplemod.models;

import java.time.Instant;

public class MachineSnapshot {
    public TelemetryNode TelemetryNode;
    public String MachineId;
    public String MachineType;
    public boolean PoweredOn;
    public Instant ObservedAt;

    public MachineSnapshot(TelemetryNode telemetryNode, String machineId, String machineType, boolean poweredOn, Instant observedAt) {
        this.TelemetryNode = telemetryNode;
        this.MachineId = machineId;
        this.MachineType = machineType;
        this.PoweredOn = poweredOn;
        this.ObservedAt = observedAt;
    }

    @Override
    public String toString() {
        return "MachineSnapshot{" +
                "TelemetryNode=" + TelemetryNode +
                ", MachineId='" + MachineId + '\'' +
                ", MachineType='" + MachineType + '\'' +
                ", PoweredOn=" + PoweredOn +
                ", ObservedAt=" + ObservedAt +
                '}';
    }
}