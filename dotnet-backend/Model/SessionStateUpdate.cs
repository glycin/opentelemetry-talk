namespace dotnet_backend.Model;

public record SessionStateUpdate(string id, List<Player> players, List<Player> highscores, List<Player> deathscores);