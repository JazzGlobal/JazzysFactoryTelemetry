namespace JazzyFactoryTelemetryApi.Models;

public class MachineSnapshot
{
    public string TelemetryNodeId { get; set;}
    public string MachineId { get; set; }
    public string MachineType { get; set; }
    public bool PoweredOn { get; set; }
    public DateTime ObservedAt { get; set; }
}