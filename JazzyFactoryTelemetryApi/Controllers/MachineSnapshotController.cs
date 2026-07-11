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

    [HttpGet("/api/machine-snapshots/latest")]
    public async Task<IActionResult> GetLatestMachineSnapshots()
    {
        var snapshots = await _machineSnapshotService.GetLatestMachineSnapshotsAsync();
        return Ok(snapshots);
    }

    [HttpPost("/api/machine-snapshots")]
    public async Task<IActionResult> CreateMachineSnapshot([FromBody] MachineSnapshot snapshot)
    {
        // Logic to create a new machine snapshot
        // This could involve calling a service method to save the snapshot to a database
        await _machineSnapshotService.CreateMachineSnapshotAsync(snapshot);
        return Ok();
    }
}