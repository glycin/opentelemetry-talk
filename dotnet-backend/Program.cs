using dotnet_backend.Model;
using System.Text.Json;

var builder = WebApplication.CreateBuilder(args);
// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddHttpClient();
builder.Services.AddHealthChecks();

var app = builder.Build();
var persistenceServiceBaseUrl = System.Environment.GetEnvironmentVariable("ENV_PERSISTENCE_URL") ?? "http://localhost:1337/persistence-service";

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UsePathBase("/dotnet-backend");

//app.UseHttpsRedirection();

app.MapGet("/init", async(HttpClient httpClient) =>
{
    var requestUrl = persistenceServiceBaseUrl + "/session/init";
    app.Logger.LogInformation("Creating new super cool Tracey Bird session");
    var response = await httpClient.PostAsync(requestUrl, null);
    if (response.IsSuccessStatusCode)
    {
        var content = await response.Content.ReadAsStringAsync();
        var session = JsonSerializer.Deserialize<Session>(content);
        app.Logger.LogInformation($"Session {session?.id} created");
        return Results.Ok(session);
    } else
    {
        app.Logger.LogError($"Could not create session...");
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("InitGame")
.WithOpenApi();

app.MapGet("/latestState", async (HttpClient httpClient) =>
{
    var requestUrl = persistenceServiceBaseUrl + "/session/latest";
    var response = await httpClient.GetAsync(requestUrl);
    app.Logger.LogInformation("Getting latest state...");

    if (response.IsSuccessStatusCode)
    {
        var content = await response.Content.ReadAsStringAsync();
        var session = JsonSerializer.Deserialize<Session>(content);
        app.Logger.LogInformation($"Returning latest state with {session?.players.Count} players");
        var highscores = session.players.OrderByDescending(p => p.highScore).Take(5).Select(p => new HighscoreItem(p.highScore, p.name, p.id)).ToList();
        var deathscores = session.players.OrderByDescending(p => p.deaths).Take(5).Select(p => new HighscoreItem(p.deaths, p.name, p.id)).ToList();
        return Results.Ok(new SessionStateUpdate(session.id, session.players, highscores, deathscores));
    }
    else
    {
        app.Logger.LogError($"Couldn't get latest state because {response.StatusCode}");
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("GetLatestState")
.WithOpenApi();

app.MapGet("/jam/init", async (HttpClient httpClient) =>
{
    var requestUrl = persistenceServiceBaseUrl + "/jam/init";
    app.Logger.LogInformation("Creating new super cool Tracey Bird session");
    var response = await httpClient.PostAsync(requestUrl, null);
    if (response.IsSuccessStatusCode)
    {
        var content = await response.Content.ReadAsStringAsync();
        var session = JsonSerializer.Deserialize<JamSession>(content);
        app.Logger.LogInformation($"Jam session {session?.id} created");
        return Results.Ok(session);
    }
    else
    {
        app.Logger.LogError($"Could not create jam session...");
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("InitJam")
.WithOpenApi();

app.MapGet("/jam/latestState", async (HttpClient httpClient) =>
{
    var requestUrl = persistenceServiceBaseUrl + "/jam/latest";
    var response = await httpClient.GetAsync(requestUrl);
    app.Logger.LogInformation("Getting latest jamming state...");

    if (response.IsSuccessStatusCode)
    {
        var content = await response.Content.ReadAsStringAsync();
        var session = JsonSerializer.Deserialize<JamSession>(content);
        app.Logger.LogInformation($"Returning latest jam state with {session?.rockers.Count} rockers");
        return Results.Ok(new JamSessionUpdate(session.id, session.rockers));
    }
    else
    {
        app.Logger.LogError($"Couldn't get latest jam state because {response.StatusCode}");
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("GetLatestJamState")
.WithOpenApi();

app.MapHealthChecks("/health");

app.Run();
