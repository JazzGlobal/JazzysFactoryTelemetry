namespace JazzyFactoryTelemetryApi.Models;
using System.Collections.Generic;
using JazzyFactoryTelemetryApi.Models;

public class TelemetryNode
{
    public string TelemetryNodeId { get; set; }
    public List<MinimizedBlockPos> LinkedMachines { get; set; }
}