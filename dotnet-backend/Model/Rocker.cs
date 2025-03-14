namespace dotnet_backend.Model;

public record Rocker(string id, string name, List<Strum> chordsPlayed);
