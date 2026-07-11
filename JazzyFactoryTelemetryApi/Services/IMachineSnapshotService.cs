using JazzyFactoryTelemetryApi.Models;

namespace JazzyFactoryTelemetryApi.Services;

public interface IMachineSnapshotService
{
    public Task<List<MachineSnapshot>> GetLatestMachineSnapshotsAsync();
    public Task CreateMachineSnapshotAsync(MachineSnapshot snapshot);
}   