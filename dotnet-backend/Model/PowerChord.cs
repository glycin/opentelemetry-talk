using System.Text.Json.Serialization;

namespace dotnet_backend.Model;

[JsonConverter(typeof(JsonStringEnumConverter))]
public enum PowerChord
{
    GUITAR,
    HORNS,
    EXPLOSION,
    SKULL,
    TIGER,
    NOTE,
}