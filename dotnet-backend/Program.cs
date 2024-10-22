var builder = WebApplication.CreateBuilder(args);
// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

//app.UseHttpsRedirection();

app.MapGet("/gamestate", () =>
{
    var time = DateTime.Now;
    return new GameState(time, new List<PlayerState> 
    { 
        new PlayerState(time, "Alex", new(1f,1f)),
        new PlayerState(time, "Ricco", new (2f, 2f))
    });
})
.WithName("GetGameState")
.WithOpenApi();

app.Run();

internal record GameState(DateTime dateTime, List<PlayerState> players);

internal record PlayerState(DateTime dateTime, string playerName, Position position);

internal record Position(float x, float y);
