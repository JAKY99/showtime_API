package com.m2i.showtime.yak.Service;


        import com.google.gson.Gson;
        import com.m2i.showtime.yak.Dto.RunInsertFromIdDto;
        import com.m2i.showtime.yak.Dto.SearchMovieAPIDto;
        import com.m2i.showtime.yak.Dto.RunInsertBulkDto;
        import org.apache.http.HttpHeaders;
        import org.json.JSONObject;

        import java.io.*;
        import java.net.Authenticator;
        import java.net.URI;
        import java.net.URISyntaxException;
        import java.net.http.HttpClient;
        import java.net.http.HttpRequest;
        import java.net.http.HttpResponse;
        import java.util.Base64;

        import static org.apache.kafka.common.utils.Utils.readFileAsString;

public class CustomThreadService extends Thread{
    RunInsertBulkDto currentBulkDto;
    RunInsertFromIdDto RunInsertFromIdDto;
    String methodToCall;
    private String elasticbaseUrl;
    private final LoggerService LOGGER = new LoggerService();
    public CustomThreadService(RunInsertBulkDto currentBulkDto, String methodToCall) {
        this.currentBulkDto = currentBulkDto;
        this.methodToCall = methodToCall;
    }
    public CustomThreadService(RunInsertFromIdDto RunInsertFromIdDto, String methodToCall) {
        this.RunInsertFromIdDto = RunInsertFromIdDto;
        this.methodToCall = methodToCall;
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
                HttpResponse elasticResponse = client.send(elasticInsert, HttpResponse.BodyHandlers.ofString());
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

}
