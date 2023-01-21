package com.m2i.showtime.yak.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2i.showtime.yak.Configuration.HazelcastConfig;
import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Configuration.RedisLetuceConfig;
import com.m2i.showtime.yak.Dto.getDataFromHazelcastDto;
import com.m2i.showtime.yak.Dto.getDataFromRedisDto;
import com.m2i.showtime.yak.Dto.getImageFromHazelcastDto;
import com.m2i.showtime.yak.Dto.getImageFromRedisDto;
import org.json.JSONObject;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class HazelcastService {
    HazelcastConfig hazelcastConfig;
    public HazelcastService(HazelcastConfig hazelcastConfig) {
        this.hazelcastConfig = hazelcastConfig;
    }

    public getImageFromHazelcastDto getHazelcastCache(String urlApi) {
        getImageFromHazelcastDto getImageFromHazelcastDto = new getImageFromHazelcastDto();
        String  check = (String) hazelcastConfig.hazelcastInstance().getMap("getImageFromHazelcastDto").get(urlApi);

        if(check==null) {
            String posterToInsert = this.getByteArrayFromImageURL(urlApi);
            String imageBase64 = "data:image/jpg;base64," + posterToInsert;
            getImageFromHazelcastDto.setUrlApi(imageBase64);
            hazelcastConfig.hazelcastInstance().getMap("getImageFromHazelcastDto").set(urlApi,imageBase64, 15, TimeUnit.MINUTES);
            return getImageFromHazelcastDto;
        }
        getImageFromHazelcastDto.setUrlApi(check);
        return getImageFromHazelcastDto;
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

    public getDataFromHazelcastDto getHazelcastCacheData(String urlApi, HttpServletResponse servletResponse) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        getDataFromHazelcastDto getDataFromHazelcastDto = new getDataFromHazelcastDto();
        String  check = (String) hazelcastConfig.hazelcastInstance().getMap("getHazelcastCacheData").get(urlApi);
        if(check==null) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlApi))
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject documentObj = new JSONObject(response.body().toString());
            getDataFromHazelcastDto.setData(documentObj.toString());
            hazelcastConfig.hazelcastInstance().getMap("getHazelcastCacheData").set(urlApi,documentObj.toString(), 15, TimeUnit.MINUTES);
            return getDataFromHazelcastDto;
        }
        getDataFromHazelcastDto.setData(check);
        servletResponse.setHeader("cache-control", "public, max-age=360000");
        return getDataFromHazelcastDto;
    }
    public String getRedisCacheDataBDD(String key) throws URISyntaxException, IOException, InterruptedException {
        getDataFromRedisDto getDataFromRedisDto = new getDataFromRedisDto();
        String  check = (String) hazelcastConfig.hazelcastInstance().getMap("getHazelcastCacheData").get(key);
        if(check==null) {
            return null;
        }
        return check;
    }
    public Boolean setRedisCacheDataBDD(String key, String value, int expire) throws URISyntaxException, IOException, InterruptedException {
        hazelcastConfig.hazelcastInstance().getMap("getHazelcastCacheData").set(key,value, expire, TimeUnit.MINUTES);
        return true;
    }

}
