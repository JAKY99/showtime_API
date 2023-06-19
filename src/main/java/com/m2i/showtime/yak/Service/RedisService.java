package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Configuration.RedisLetuceConfig;
import com.m2i.showtime.yak.Dto.RunInsertRedisCacheDto;
import com.m2i.showtime.yak.Dto.getDataFromRedisDto;
import com.m2i.showtime.yak.Dto.getImageFromRedisDto;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.protocol.RedisCommand;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Service
public class RedisService {

    private final RedisClient redisClient;
    private RedisLetuceConfig redisLetuceConfig;

    public RedisService(RedisLetuceConfig redisLetuceConfig) {

        this.redisLetuceConfig = redisLetuceConfig;
        this.redisClient = this.redisLetuceConfig.redisClient();
    }

    public getImageFromRedisDto getRedisCache(String urlApi) {
        getImageFromRedisDto getImageFromRedisDto = new getImageFromRedisDto();
        String  check = this.redisClient.connect().sync().get(urlApi);
        if(check==null) {
            String posterToInsert = this.getByteArrayFromImageURL(urlApi);
            String imageBase64 = "data:image/jpg;base64," + posterToInsert;
            this.redisClient.connect().sync().set(urlApi, imageBase64);
            this.redisClient.connect().sync().expire(urlApi, 3600);
            getImageFromRedisDto.setUrlApi(this.redisClient.connect().sync().get(urlApi));
            return getImageFromRedisDto;
        }
        getImageFromRedisDto.setUrlApi(this.redisClient.connect().sync().get(urlApi));
        return getImageFromRedisDto;
    }

    private String getByteArrayFromImageURL(String url) {
        try {
            URL imageUrl = new URL(url);
            BufferedInputStream bis = new BufferedInputStream(imageUrl.openConnection().getInputStream());
            return Base64.getEncoder().encodeToString(bis.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public getDataFromRedisDto getRedisCacheData(String brutUrl, HttpServletResponse HttpServletResponse) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI encodedUrl = new URI(brutUrl);
        StatefulRedisConnection<String, String> connection = this.redisClient.connect();

        RedisCommands<String, String> commands = connection.sync();
        getDataFromRedisDto getDataFromRedisDto = new getDataFromRedisDto();
        String  check = commands.get(encodedUrl.toString());
        if(check==null) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(encodedUrl)
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject documentObj = new JSONObject(response.body().toString());
            getDataFromRedisDto.setData(documentObj.toString());
            commands.set(encodedUrl.toString(), documentObj.toString());
            commands.expire(encodedUrl.toString(), 3600);
            HttpServletResponse.addHeader("cache-control", "public, max-age=28800");
            connection.close();

            return getDataFromRedisDto;
        }
        HttpServletResponse.addHeader("cache-control", "public, max-age=28800");
        getDataFromRedisDto.setData(check);
        connection.close();
        return getDataFromRedisDto;
    }
    public JSONObject getDataFromRedisForInternalRequest(String brutUrl) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        StatefulRedisConnection<String, String> connection = this.redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        URI encodedUrl = new URI(brutUrl);
        String  check = commands.get(encodedUrl.toString());
        JSONObject documentObj = null;
        if(check==null) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(encodedUrl)
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            documentObj = new JSONObject(response.body().toString());
            commands.set(encodedUrl.toString(), documentObj.toString());
            commands.expire(encodedUrl.toString(), 3600);
            connection.close();

            return documentObj;
        }
        if(check!=null) {
            documentObj = new JSONObject(check);
            connection.close();
            return documentObj;
        }
        connection.close();
        return documentObj;
    }

    public boolean getLastCheckOnAirDateEpisode(String key){
        StatefulRedisConnection<String, String> connection = this.redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        String  check = commands.get(key);
        JSONObject documentObj = null;
        if(check==null) {
            return false;
        }
        if(check!=null) {
            commands.set(key, "true");
            commands.expire(key, 3600*24*7);
            connection.close();
            return true;
        }
        return true;
    }
}
