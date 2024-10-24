var builder = WebApplication.CreateBuilder(args);
// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddHttpClient();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

//app.UseHttpsRedirection();

app.MapGet("/init", async(HttpClient httpClient) =>
{
    var requestUrl = "http://localhost:1337/session/init";
    var response = await httpClient.GetAsync(requestUrl);
    if (response.IsSuccessStatusCode)
    {
        var content = await response.Content.ReadAsStringAsync();
        return Results.Ok(content);
    } else
    {
        return Results.StatusCode((int)response.StatusCode);
    }
})
.WithName("InitGame")
.WithOpenApi();

app.MapGet("/latestState", async (HttpClient httpClient) =>
{
    var requestUrl = "http://localhost:1337/session/getLatestState";
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


app.Run();