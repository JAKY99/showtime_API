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
    String elasticUsername;
    String elasticPassword;

    public RunInsertFromIdDto(int id, int index, String dateStrFormatted, String elasticbaseUrl, String apikey,File toFile, String elasticUsername, String elasticPassword) {
        this.elementId = id;
        this.indexUrlToUse = index;
        this.dateStrFormatted = dateStrFormatted;
        this.elasticbaseUrl = elasticbaseUrl;
        this.apiKey = apikey;
        this.toFile = toFile;
        this.elasticUsername = elasticUsername;
        this.elasticPassword = elasticPassword;
    }
    public RunInsertFromIdDto(int index, String dateStrFormatted, String elasticbaseUrl, String apikey,File toFile, String elasticUsername, String elasticPassword) {
        this.indexUrlToUse = index;
        this.dateStrFormatted = dateStrFormatted;
        this.elasticbaseUrl = elasticbaseUrl;
        this.apiKey = apikey;
        this.toFile = toFile;
        this.elasticUsername = elasticUsername;
        this.elasticPassword = elasticPassword;

    }
}
