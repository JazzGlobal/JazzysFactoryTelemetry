# Jazzy's Factory Telemetry
Welcome! 🤖 This is a mod I put together to assist my Minecraft factory building. To be honest, setup is a bit convoluted and requires some technical know-how.

## Telemetry Flow
A Telemetry Node without linked machines is intentionally idle. Once at least one supported machine is linked, the node periodically generates MachineSnapshots, enqueues them, and they are asynchronously persisted by the API.

When a failure response is returned from the API when pushing telemetry, the Dequeue service will attempt to retry. If it fails after the maximum retry attempts (configurable via server.properties), the failed payload is thrown away.

![Telemetry Sequence Diagram](./doc/planning/diagrams/telemetry-sequence-diagram.svg)