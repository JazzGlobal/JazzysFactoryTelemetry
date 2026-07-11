using JazzyFactoryTelemetryApi.Models;

namespace JazzyFactoryTelemetryApi.Services;

public class InMemoryMachineSnapshotService : IMachineSnapshotService
{
    private readonly List<MachineSnapshot> _snapshots = new List<MachineSnapshot>();
    public Task CreateMachineSnapshotAsync(MachineSnapshot snapshot)
    {
        _snapshots.Add(snapshot);
        return Task.CompletedTask;
    }

    public Task<List<MachineSnapshot>> GetLatestMachineSnapshotsAsync()
    {
        return Task.FromResult(_snapshots);
    }
}