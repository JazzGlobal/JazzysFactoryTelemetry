using JazzyFactoryTelemetryApi.Services;
using JazzyFactoryTelemetryApi.Models;
using Microsoft.AspNetCore.Mvc;

namespace JazzyFactoryTelemetryApi.Controllers;

[ApiController]
[Route("api/machine-snapshots")]
public class MachineSnapshotController : ControllerBase
{
    private readonly IMachineSnapshotService _machineSnapshotService;

    public MachineSnapshotController(IMachineSnapshotService machineSnapshotService)
    {
        _machineSnapshotService = machineSnapshotService;
    }

    [HttpGet]
    public async Task<IActionResult> GetMachineSnapshots()
    {
        var snapshots = await _machineSnapshotService.GetLatestMachineSnapshotsAsync();
        return Ok(snapshots);
    }

    [HttpPost]
    public async Task<IActionResult> CreateMachineSnapshot(MachineSnapshot snapshot)
    {
        // Logic to create a new machine snapshot
        // This could involve calling a service method to save the snapshot to a database
        await _machineSnapshotService.CreateMachineSnapshotAsync(snapshot);
        return Ok();
    }

    [HttpPost("batch")]
    public async Task<IActionResult> CreateMachineSnapshots(List<MachineSnapshot> snapshots)
    {
        // Logic to create multiple machine snapshots
        await _machineSnapshotService.CreateMachineSnapshotsAsync(snapshots);
        Console.WriteLine($"{DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss")} Created {snapshots.Count} machine snapshots.");

        return Ok();
    }

    [HttpGet("total")]
    public async Task<IActionResult> GetTotalMachineSnapshots()
    {
        var total = await _machineSnapshotService.GetTotalMachineSnapshotsAsync();
        return Ok(total);
    }
}