namespace dotnet_backend.Model;

public record Player(string id, string name, List<Action> actions, int score, int deaths, int highScore);
