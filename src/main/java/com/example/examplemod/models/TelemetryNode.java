package com.example.examplemod.models;

import java.util.List;

public class TelemetryNode {
    public String TelemetryNodeId;
    public List<MinimizedBlockPos> LinkedMachines;

    public TelemetryNode(String telemetryNodeId, List<MinimizedBlockPos> linkedMachines) {
        this.TelemetryNodeId = telemetryNodeId;
        this.LinkedMachines = linkedMachines;
    }
}
