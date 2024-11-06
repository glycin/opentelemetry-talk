namespace dotnet_backend.Model;

public record SessionStateUpdate(string id, List<Player> players, List<HighscoreItem> highscores, List<HighscoreItem> deathscores);