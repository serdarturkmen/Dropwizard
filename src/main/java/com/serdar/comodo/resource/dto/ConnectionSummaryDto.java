package com.serdar.comodo.resource.dto;

import lombok.Data;

@Data
public class ConnectionSummaryDto {

    public ConnectionSummaryDto() {
    }

    public ConnectionSummaryDto(String sourceIp, String destinationIp, long timestamp, String protocol, int eventCount) {
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.timestamp = timestamp;
        this.protocol = protocol;
        this.eventCount = eventCount;
    }

    private String sourceIp;

    private String destinationIp;

    private long timestamp;

    private String protocol;

    private int eventCount;


}