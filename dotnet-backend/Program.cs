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
        return Results.Ok(session);
    }
    else
    {
        app.Logger.LogError($"Couldn't get latest state because {response.StatusCode}");
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("GetLatestState")
.WithOpenApi();

app.MapHealthChecks("/health");

app.Run();
