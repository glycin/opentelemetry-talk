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
var persistenceServiceBaseUrl = System.Environment.GetEnvironmentVariable("ENV_PERSISTENCE_URL") ?? "http://localhost:1337";

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

//app.UseHttpsRedirection();

app.MapGet("/init", async(HttpClient httpClient) =>
{
    var requestUrl = persistenceServiceBaseUrl + "/session/init";
    var response = await httpClient.GetAsync(requestUrl);
    if (response.IsSuccessStatusCode)
    {
        var content = await response.Content.ReadAsStringAsync();
        var session = JsonSerializer.Deserialize<Session>(content);
        return Results.Ok(session);
    } else
    {
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("InitGame")
.WithOpenApi();

app.MapGet("/latestState", async (HttpClient httpClient) =>
{
    var requestUrl = persistenceServiceBaseUrl + "/session/getLatestState";
    var response = await httpClient.GetAsync(requestUrl);
    if (response.IsSuccessStatusCode)
    {
        var content = await response.Content.ReadAsStringAsync();
        return Results.Ok(content);
    }
    else
    {
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("GetLatestState")
.WithOpenApi();

app.MapHealthChecks("/health");

app.Run();
