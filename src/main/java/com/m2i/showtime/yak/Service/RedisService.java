package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Configuration.RedisLetuceConfig;
import com.m2i.showtime.yak.Dto.RunInsertRedisCacheDto;
import com.m2i.showtime.yak.Dto.getDataFromRedisDto;
import com.m2i.showtime.yak.Dto.getImageFromRedisDto;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@Service
public class RedisService {

    private  RedisConfig redisConfig;
    private RedisLetuceConfig redisLetuceConfig;

    public RedisService(RedisConfig redisConfig,RedisLetuceConfig redisLetuceConfig) {

        this.redisConfig = redisConfig;
        this.redisLetuceConfig = redisLetuceConfig;
    }

    public getImageFromRedisDto getRedisCache(String urlApi) {
        getImageFromRedisDto getImageFromRedisDto = new getImageFromRedisDto();
        String  check = redisLetuceConfig.redisClient().connect().sync().get(urlApi);
        if(check==null) {
            String posterToInsert = this.getByteArrayFromImageURL(urlApi);
            String imageBase64 = "data:image/jpg;base64," + posterToInsert;
            redisLetuceConfig.redisClient().connect().sync().set(urlApi, imageBase64);
            redisLetuceConfig.redisClient().connect().sync().expire(urlApi, 3600);
            getImageFromRedisDto.setUrlApi(redisLetuceConfig.redisClient().connect().sync().get(urlApi));
            return getImageFromRedisDto;
        }
        getImageFromRedisDto.setUrlApi(redisLetuceConfig.redisClient().connect().sync().get(urlApi));
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

    public getDataFromRedisDto getRedisCacheData(String urlApi) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        getDataFromRedisDto getDataFromRedisDto = new getDataFromRedisDto();
        String  check = redisLetuceConfig.redisClient().connect().sync().get(urlApi);
        if(check==null) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlApi))
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject documentObj = new JSONObject(response.body().toString());
            getDataFromRedisDto.setData(documentObj.toString());
            redisLetuceConfig.redisClient().connect().sync().set(urlApi, documentObj.toString());
            redisLetuceConfig.redisClient().connect().sync().expire(urlApi, 3600);
            return getDataFromRedisDto;
        }
        getDataFromRedisDto.setData(check);
        return getDataFromRedisDto;
    }
    public String getRedisCacheDataBDD(String key) throws URISyntaxException, IOException, InterruptedException {
        getDataFromRedisDto getDataFromRedisDto = new getDataFromRedisDto();
        String  check = redisLetuceConfig.redisClient().connect().sync().get(key);
        if(check==null) {
            return null;
        }
        return check;
    }
    public Boolean setRedisCacheDataBDD(String key, String value, int expire) throws URISyntaxException, IOException, InterruptedException {
        redisLetuceConfig.redisClient().connect().sync().set(key,value);
        redisLetuceConfig.redisClient().connect().sync().expire(key, expire);
        return true;
    }
}
