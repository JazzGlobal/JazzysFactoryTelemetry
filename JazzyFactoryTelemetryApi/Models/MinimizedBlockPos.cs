namespace JazzyFactoryTelemetryApi.Models;

public class MinimizedBlockPos
{
    public int X { get; set; }
    public int Y { get; set; }
    public int Z { get; set; }

    public MinimizedBlockPos(int x, int y, int z)
    {
        X = x;
        Y = y;
        Z = z;
    }
}