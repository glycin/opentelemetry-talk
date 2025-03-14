namespace dotnet_backend.Model;

using System.Collections.Generic;

public record JamSession(string id, List<Rocker> rockers);
