package com.serdar.comodo.resource.dto;

import lombok.Data;

@Data
public class SummaryDto {

    public SummaryDto(String sourceIp, String destIp, String protocol, long startMilis, long endMilis, int numOfMinEvent, int hourResolution) {
        this.sourceIp = sourceIp;
        this.destIp = destIp;
        this.protocol = protocol;
        this.startMilis = startMilis;
        this.endMilis = endMilis;
        this.numOfMinEvent = numOfMinEvent;
        this.hourResolution = hourResolution;
    }

    private String sourceIp;

    private String destIp;

    private String protocol;

    private long startMilis;

    private long endMilis;

    private int numOfMinEvent;

    private int hourResolution;

}
