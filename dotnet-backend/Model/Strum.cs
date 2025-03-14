using System.Text.Json.Serialization;

namespace dotnet_backend.Model;

public class Strum {
    public long timeStamp { get; set; }

    [JsonConverter(typeof(JsonStringEnumConverter))]
    public PowerChord chord {  get; set; }
}
