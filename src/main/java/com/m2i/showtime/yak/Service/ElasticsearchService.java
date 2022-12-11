package com.m2i.showtime.yak.Service;

import com.google.gson.Gson;
import com.m2i.showtime.yak.Dto.RunInsertFromIdDto;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.zip.GZIPInputStream;

@Service
@EnableScheduling
@EnableAsync
public class ElasticsearchService {
    @Value("${application.imdb.apiKey}")
    private String apiKey;
    @Value("${application.elasticurl}")
    private String elasticbaseUrl;
    @Value("${application.elasticsearch.username}")
    private String elasticUsername;
    @Value("${application.elasticsearch.password}")
    private String elasticPassword;

    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private final LoggerService LOGGER = new LoggerService();



    @Scheduled(cron = "0 0 9 * * *")
    @Async
    public void dailyUpdate() throws IOException, URISyntaxException, InterruptedException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM_dd_yyyy");
        Date date = new Date();
        Date previousDate = new Date(System.currentTimeMillis() - MILLIS_IN_A_DAY);
        String previousDateStrFormatted = formatter.format(previousDate);
        String[] dailyUrlsNamesUnzippedToDelete = {"movie_ids_" + previousDateStrFormatted + ".json", "tv_series_ids_" + previousDateStrFormatted + ".json", "person_ids_" + previousDateStrFormatted + ".json"};
        String dateStrFormatted = formatter.format(date);
        Path currentRelativePath = Paths.get("");
        String basePath = currentRelativePath.toAbsolutePath().toString();
        String dailyUrlMovieDB = "https://files.tmdb.org/p/exports/movie_ids_" + dateStrFormatted + ".json.gz";
        String dailyUrlTVDB = "https://files.tmdb.org/p/exports/tv_series_ids_" + dateStrFormatted + ".json.gz";
        String dailyUrlPersonDB = "https://files.tmdb.org/p/exports/person_ids_" + dateStrFormatted + ".json.gz";
        String[] dailyUrls = {dailyUrlMovieDB, dailyUrlTVDB};
        String[] dailyUrlsNames = {"movie_ids_" + dateStrFormatted + ".json.gz", "tv_series_ids_" + dateStrFormatted + ".json.gz"};
        String[] dailyUrlsNamesUnzipped = {"movie_ids_" + dateStrFormatted + ".json", "tv_series_ids_" + dateStrFormatted + ".json", "person_ids_" + dateStrFormatted + ".json"};
        int i = 0;
        for (String dailyUrl : dailyUrls) {
            switch (i) {
                case 0:
                    LOGGER.print("Downloading movies");
                    break;
                case 1:
                    LOGGER.print("Downloading TV shows");
                    break;
                case 2:
                    LOGGER.print("Downloading people");
                    break;
            }
            InputStream in = new URL(dailyUrl).openStream();
            Files.copy(in, Paths.get(basePath + "/src/main/temp/" + dailyUrlsNames[i]), StandardCopyOption.REPLACE_EXISTING);
            try (GZIPInputStream gis = new GZIPInputStream(
                    new FileInputStream(Paths.get(basePath + "/src/main/temp/" + dailyUrlsNames[i]).toFile()));
                 FileOutputStream fos = new FileOutputStream(Paths.get(basePath + "/src/main/temp/" + dailyUrlsNamesUnzipped[i]).toFile())) {

                // copy GZIPInputStream to FileOutputStream
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            File fileToDelete = new File(basePath + "/src/main/temp/" + dailyUrlsNames[i]);
            fileToDelete.delete();
            int finalI = i;
            RunInsertFromIdDto RunInsertFromIdDto = new RunInsertFromIdDto( i, dateStrFormatted, elasticbaseUrl, apiKey, Paths.get(basePath + "/src/main/temp/" + dailyUrlsNamesUnzipped[finalI]).toFile(), elasticUsername, elasticPassword);
            CustomThreadService thread = new CustomThreadService(RunInsertFromIdDto,"readJsonFile");
            thread.start();
            i++;
        }
        for (String fileToDelete : dailyUrlsNamesUnzippedToDelete) {
            Path path = Paths.get(basePath + "/src/main/temp/" +fileToDelete);
            Files.deleteIfExists(path);
        }
        this.deleteOldIndex(previousDateStrFormatted,this.elasticbaseUrl);

    }

    private void readJsonFile(File toFile,int index,String dateStrFormatted)  {
        try {
            BufferedReader br = new BufferedReader(new FileReader(toFile));
            String line;
            while ((line = br.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                int id = obj.getInt("id");
                this.RunInsertFromId(id,index,dateStrFormatted,this.apiKey,this.elasticbaseUrl);

            }

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
    public void RunInsertFromId(int elementId,int indexUrlToUse, String dateStrFormatted,String apiKey,String elasticbaseUrl) throws URISyntaxException, IOException, InterruptedException {
        String[] elementType = {"movie","tv","person"};
        String[] urlToUse = {
                "https://api.themoviedb.org/3/movie/" + elementId + "?api_key=" + apiKey + "&language=en-US",
                "https://api.themoviedb.org/3/tv/" + elementId + "?api_key=" + apiKey + "&language=en-US",
                "https://api.themoviedb.org/3/person/" + elementId + "?api_key=" + apiKey + "&language=en-US",
        };


        StringBuilder builk_build_string = new StringBuilder();
        LOGGER.print("Insertion" + elementType[indexUrlToUse]+ " id : " + elementId + " - Début");
        try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest currentPage = HttpRequest.newBuilder()
                        .uri(new URI(urlToUse[indexUrlToUse]))
                        .GET()
                        .build();
                HttpResponse currentResponse = client.send(currentPage, HttpResponse.BodyHandlers.ofString());
                JSONObject documentObj = new JSONObject(currentResponse.body().toString());
                Gson gson = new Gson();
                String json = gson.toJson(documentObj);
                builk_build_string.append("{ \"index\":{ \"_index\": \""+ elementType[indexUrlToUse] + "s_"+ dateStrFormatted + "\" } }\n");
                builk_build_string.append( json + "\n");
                HttpRequest elasticInsert = HttpRequest.newBuilder()
                        .uri(new URI(elasticbaseUrl + "/" + elementType[indexUrlToUse] + "s/_bulk"))
                        .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .setHeader(HttpHeaders.AUTHORIZATION,getBasicAuthenticationHeader(elasticUsername, elasticPassword))
                        .PUT(HttpRequest.BodyPublishers.ofString(builk_build_string.toString()))
                        .build();
                HttpResponse elasticResponse = client.send(elasticInsert, HttpResponse.BodyHandlers.ofString());
                LOGGER.print("Insertion" + elementType[indexUrlToUse]+ " id : " + elementId + " - Fin");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteOldIndex(String dateStrFormatted,String elasticbaseUrl) throws URISyntaxException, IOException, InterruptedException {
        String[] elementType = {"movie","tv","person"};
        for (String element : elementType) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest deleteIndex = HttpRequest.newBuilder()
                    .uri(new URI(elasticbaseUrl + "/" + element + "s_" + dateStrFormatted))
                    .setHeader(HttpHeaders.AUTHORIZATION,getBasicAuthenticationHeader(elasticUsername, elasticPassword))
                    .DELETE()
                    .build();
            HttpResponse elasticResponse = client.send(deleteIndex, HttpResponse.BodyHandlers.ofString());
            LOGGER.print("Delete index " + element + "s_" + dateStrFormatted + " - Fin");
        }
    }
    @Scheduled(cron = "0 0 9 * * *")
    public void uploadLogsToElasticsearch() throws IOException, URISyntaxException, InterruptedException {

        Path currentRelativePath = Paths.get("");
        String basePath = currentRelativePath.toAbsolutePath().toString();
        File Oldfolder = new File(basePath + "/src/main/logs/old/");
        Date timestamp = new Date();
        if(Oldfolder.exists()) {

            File[] listOfDirectory = Oldfolder.listFiles();
            for (File directorys : listOfDirectory) {
                File[] listOfFiles = directorys.listFiles();
                if(listOfFiles.length==0){
                    directorys.delete();
                    continue;
                }

                for (File file : listOfFiles) {
                    LOGGER.print("Start sending historic log to elasticsearch");
                    StringBuilder builk_build_string = new StringBuilder();
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(line);
                        builk_build_string.append("{ \"index\":{ \"_index\": \"logs_historic\"}\n");
                        builk_build_string.append( "{ \"text\" : " + json + ", \"date\" : " + timestamp + "}\n");
                    }
                    br.close();
                    file.delete();
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest elasticInsert = HttpRequest.newBuilder()
                            .uri(new URI(elasticbaseUrl + "/logs_historic/_bulk"))
                            .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .setHeader(HttpHeaders.AUTHORIZATION,getBasicAuthenticationHeader(elasticUsername, elasticPassword))
                            .PUT(HttpRequest.BodyPublishers.ofString(builk_build_string.toString()))
                            .build();
                    HttpResponse elasticResponse = client.send(elasticInsert, HttpResponse.BodyHandlers.ofString());
                    LOGGER.print("End sending historic log to elasticsearch");

                }
                directorys.delete();
            }
        }
    }
    @Scheduled(initialDelay=15000,fixedDelay=3600000)
    public void uploadLogsToElasticsearchHourly() throws IOException, URISyntaxException, InterruptedException {

        StringBuilder builk_build_string = new StringBuilder();
        Path currentRelativePath = Paths.get("");
        String basePath = currentRelativePath.toAbsolutePath().toString();
        File currentLogFile = new File(basePath + "/src/main/logs/spring-boot-logger-log4j2.log");
        Date timestamp = new Date();
        if (currentLogFile.isFile()) {
            LOGGER.print("Start sending hourly log to elasticsearch");
            BufferedReader br = new BufferedReader(new FileReader(currentLogFile));
            String line;
            while ((line = br.readLine()) != null) {
                Gson gson = new Gson();
                String json = gson.toJson(line);
                builk_build_string.append("{ \"index\":{ \"_index\": \"hourly_log\" } }\n");
                builk_build_string.append( "{ \"text\" : " + json + ", \"date\" : " + timestamp + "}\n");
            }
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest elasticInsert = HttpRequest.newBuilder()
                    .uri(new URI(elasticbaseUrl + "/hourly_log/_bulk"))
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setHeader(HttpHeaders.AUTHORIZATION,getBasicAuthenticationHeader(elasticUsername, elasticPassword))
                    .PUT(HttpRequest.BodyPublishers.ofString(builk_build_string.toString()))
                    .build();
            HttpResponse elasticResponse = client.send(elasticInsert, HttpResponse.BodyHandlers.ofString());
            LOGGER.print("End sending hourly log to elasticsearch");
            LOGGER.print("Delete hourly log file");
            br.close();
            currentLogFile.delete();
        }
    }
    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}