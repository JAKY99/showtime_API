package com.m2i.showtime.yak.Service;
import com.google.gson.Gson;
import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Dto.RunInsertFromIdDto;
import com.m2i.showtime.yak.Dto.RunInsertRedisCacheDto;
import com.m2i.showtime.yak.Dto.SearchMovieAPIDto;
import com.m2i.showtime.yak.Dto.RunInsertBulkDto;
import org.apache.http.HttpHeaders;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;

@Service
public class CustomThreadService extends Thread{
    RunInsertBulkDto currentBulkDto;
    RunInsertFromIdDto RunInsertFromIdDto;
    RunInsertRedisCacheDto RunInsertRedisCacheDto;
    String methodToCall;

    private final LoggerService LOGGER = new LoggerService();
    public CustomThreadService(RunInsertBulkDto currentBulkDto, String methodToCall) {
        this.currentBulkDto = currentBulkDto;
        this.methodToCall = methodToCall;
    }
    public CustomThreadService(RunInsertFromIdDto RunInsertFromIdDto, String methodToCall) {
        this.RunInsertFromIdDto = RunInsertFromIdDto;
        this.methodToCall = methodToCall;
    }
    public CustomThreadService(RunInsertRedisCacheDto RunInsertRedisCacheDto, String methodToCall) {
        this.RunInsertRedisCacheDto = RunInsertRedisCacheDto;
        this.methodToCall = methodToCall;
    }

    public CustomThreadService() {

    }


    @Override
    public void run (){
        try {
            switch (methodToCall){
                case "runInsertBulk":
                    runInsertBulk(currentBulkDto.getElasticbaseUrl(),currentBulkDto.getCurrentUrl(),currentBulkDto.getPage(),currentBulkDto.getUrlMovieUserElastic());
                    break;
                case "readJsonFile":
                    readJsonFile(RunInsertFromIdDto.getToFile(),RunInsertFromIdDto.getIndexUrlToUse(),RunInsertFromIdDto.getDateStrFormatted(),RunInsertFromIdDto.getElasticbaseUrl(),RunInsertFromIdDto.getElasticUsername(),RunInsertFromIdDto.getElasticPassword());
                    break;
                case "runInsertRedisCache":
                    runInsertRedisCache(this.RunInsertRedisCacheDto.getRedisConfig(),this.RunInsertRedisCacheDto.getUrlApi());
                    break;
                case "insertUrlApiInRedis":
                    insertUrlApiInRedis(this.RunInsertRedisCacheDto.getRedisConfig(),this.RunInsertRedisCacheDto.getUrlApi());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void runInsertBulk(String elasticbaseUrl , String currentUrl, int page, String urlMovieUserElastic) throws URISyntaxException, IOException, InterruptedException {
            StringBuilder builk_build_string = new StringBuilder();
            Gson gson = new Gson();
            HttpClient client = HttpClient.newHttpClient();
            SearchMovieAPIDto result_search;


            HttpRequest currentPage = HttpRequest.newBuilder()
                    .uri(new URI(currentUrl))
                    .GET()
                    .build();
            HttpResponse currentResponse = client.send(currentPage, HttpResponse.BodyHandlers.ofString());

            JSONObject documentObj = new JSONObject(currentResponse.body().toString());
            result_search = gson.fromJson(String.valueOf(documentObj), SearchMovieAPIDto.class);

            for(int j = 0; j < result_search.results.length; j++){
                if(j==0){
                    LOGGER.print("Page : " + page + " - Initialisation du bulk");
                }
                builk_build_string.append("{ \"index\":{ \"_index\": \""+ urlMovieUserElastic +"\" } }\n");
                builk_build_string.append( gson.toJson( result_search.results[j]) + "\n");
                HttpRequest elasticInsert = HttpRequest.newBuilder()
                        .uri(new URI(elasticbaseUrl + "/" + urlMovieUserElastic + "/_bulk"))
                        .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(builk_build_string.toString()))
                        .build();
                client.send(elasticInsert, HttpResponse.BodyHandlers.ofString());
                builk_build_string = new StringBuilder();
                if(j==result_search.results.length-1){
                    LOGGER.print("Page : " + page + " - Bulk terminé");
                }
            }
   }
    private void readJsonFile(File toFile,int index,String dateStrFormatted,String elasticbaseUrl,String elasticUsername,String elasticPassword)  {
        try {
            BufferedReader br = new BufferedReader(new FileReader(toFile));
            String[] elementType = {"movie","tv","person"};
            String line = null;
            HttpClient client = HttpClient.newHttpClient();
            StringBuilder builk_build_string = new StringBuilder();
            LOGGER.print("Bulk start - genererating bulk from file : " + toFile.getName());
            while ((line = br.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                builk_build_string.append("{ \"index\":{ \"_index\": \""+ elementType[index] + "s_"+ dateStrFormatted + "\" } }\n");
                builk_build_string.append( obj + "\n");
            }
            br.close();
            toFile.delete();
            LOGGER.print("Bulk genererating done - Deleting file : " + toFile.getName());
            toFile.delete();
            LOGGER.print("sending bulk : " + elementType[index] + " - to elastic " );
            this.RunInsertFromId(builk_build_string,index,dateStrFormatted,elasticbaseUrl,client,elasticUsername,elasticPassword);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
    public void RunInsertFromId(StringBuilder row , int indexUrlToUse, String dateStrFormatted, String elasticbaseUrl, HttpClient client,String elasticUsername,String elasticPassword) throws URISyntaxException, IOException, InterruptedException {
        String[] elementType = {"movie","tv","person"};


        LOGGER.print("Insertion " + elementType[indexUrlToUse]+ " bulk - Start");
            try {
                   HttpRequest elasticInsert = HttpRequest.newBuilder()
                           .uri(new URI(elasticbaseUrl + "/" + elementType[indexUrlToUse] + "s_" + dateStrFormatted + "/_bulk"))
                           .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                           .setHeader(HttpHeaders.AUTHORIZATION,getBasicAuthenticationHeader(elasticUsername, elasticPassword))
                           .PUT(HttpRequest.BodyPublishers.ofString(row.toString()))
                           .build();
                   HttpResponse elasticResponse = client.send(elasticInsert, HttpResponse.BodyHandlers.ofString());
                LOGGER.print("Insertion " + elementType[indexUrlToUse]+ " bulk - Over - Status code : " + elasticResponse.statusCode());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }



    }
    public void runInsertRedisCache(RedisConfig redisConfig,String KeyUrl) throws URISyntaxException, IOException, InterruptedException {
        String base64Prefix = "data:image/jpg;base64,";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(KeyUrl))
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject documentObj = new JSONObject(response.body().toString());
        JSONArray results = documentObj.getJSONArray("results");
        try {
            final String[] resultToInsert = {new String()};
            results.forEach(movie -> {
                JSONObject currentMovie = new JSONObject(movie.toString());
                String baseUrl = "https://image.tmdb.org/t/p/";
                String sizePoster = "w500";
                String sizeBackdrop = "original";
                String posterPath = baseUrl + sizePoster + currentMovie.getString("poster_path");
                String backdropPath = baseUrl + sizeBackdrop + currentMovie.getString("backdrop_path");
                String posterToInsert = getByteArrayFromImageURL(posterPath);
                String backdropToInsert = getByteArrayFromImageURL(backdropPath);
                currentMovie.put("poster_path", base64Prefix + posterToInsert);
                currentMovie.put("backdrop_path", base64Prefix + backdropToInsert);
                resultToInsert[0] += currentMovie;

            });
            redisConfig.jedis().set(KeyUrl, Arrays.toString(resultToInsert));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
    void insertUrlApiInRedis(RedisConfig redisConfig, String urlApi){
        String posterToInsert = getByteArrayFromImageURL(urlApi);
        String imageBase64 = "data:image/jpg;base64," + posterToInsert;
        redisConfig.jedis().set(urlApi, imageBase64);
        redisConfig.jedis().expire(urlApi, 3600);
    }

}
