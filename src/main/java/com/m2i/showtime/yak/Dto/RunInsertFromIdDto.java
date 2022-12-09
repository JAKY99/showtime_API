package com.m2i.showtime.yak.Dto;

import lombok.Getter;

import java.io.File;

@Getter
public class RunInsertFromIdDto {
    File toFile;
    int elementId;
    int indexUrlToUse;
    String dateStrFormatted;
    String elasticbaseUrl;
    String apiKey;

    public RunInsertFromIdDto(int id, int index, String dateStrFormatted, String elasticbaseUrl, String apikey,File toFile){
        this.elementId = id;
        this.indexUrlToUse = index;
        this.dateStrFormatted = dateStrFormatted;
        this.elasticbaseUrl = elasticbaseUrl;
        this.apiKey = apikey;
        this.toFile = toFile;
    }
    public RunInsertFromIdDto(int index, String dateStrFormatted, String elasticbaseUrl, String apikey,File toFile){
        this.indexUrlToUse = index;
        this.dateStrFormatted = dateStrFormatted;
        this.elasticbaseUrl = elasticbaseUrl;
        this.apiKey = apikey;
        this.toFile = toFile;
    }
}
