package com.m2i.showtime.yak.Service;


        import com.google.gson.Gson;
        import com.m2i.showtime.yak.Dto.SearchMovieAPIDto;
        import com.m2i.showtime.yak.Dto.RunInsertBulkDto;
        import org.apache.http.HttpHeaders;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.net.URI;
        import java.net.URISyntaxException;
        import java.net.http.HttpClient;
        import java.net.http.HttpRequest;
        import java.net.http.HttpResponse;

public class CustomThreadService extends Thread{
    RunInsertBulkDto currentBulkDto;
    String methodToCall;

    public CustomThreadService(RunInsertBulkDto currentBulkDto, String methodToCall) {
        this.currentBulkDto = currentBulkDto;
        this.methodToCall = methodToCall;
    }


    @Override
    public void run (){
        try {
            switch (methodToCall){
                case "runInsertBulk":
                    runInsertBulk(currentBulkDto.getElasticbaseUrl(),currentBulkDto.getCurrentUrl(),currentBulkDto.getPage(),currentBulkDto.getUrlMovieUserElastic());
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
                    System.out.println("Page : " + page + " - Initialisation du bulk");
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
                    System.out.println("Page : " + page + " - Bulk terminé");
                }
            }
   }
}
