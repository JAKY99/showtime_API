package com.m2i.showtime.yak.Service;

import com.google.gson.Gson;
import com.m2i.showtime.yak.Dto.SearchCast;
import com.m2i.showtime.yak.Repository.ActorRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ActorService {

    @Value("${application.imdb.apiKey}")
    private String apiKey;
    private final ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public SearchCast getMovieCast(int IdMovie) throws IOException, InterruptedException, URISyntaxException {
        String urlToCall = "https://api.themoviedb.org/3/movie/" + IdMovie
                + "/credits?api_key=" + apiKey + "&language=en-US";
        SearchCast resultSearch = new SearchCast();
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(new URI(urlToCall))
                                         .GET()
                                         .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject documentObj = new JSONObject(response.body()
                                                        .toString());
        JSONArray results = documentObj.getJSONArray("cast");
        Gson gson = new Gson();
        JSONObject resultToSendObj = new JSONObject("{\"page\":1,\"results\":" + results + "}");
        return gson.fromJson(resultToSendObj.toString(), SearchCast.class);
    }
}
