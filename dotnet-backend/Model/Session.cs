namespace dotnet_backend.Model;

public record Session(string id, List<Obstacle> obstacles, List<Player> players);