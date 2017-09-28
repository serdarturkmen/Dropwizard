package com.serdar.comodo.model;

import lombok.Data;

@Data
public class ConnectionSummary {

    public ConnectionSummary() {
    }

    public ConnectionSummary(Integer id, long timeBlock, long sourceIp, long destinationIp, int protocol, int numberOfEvents, String sourceFile, String uploadCode) {
        this.id=id;
        this.timeBlock = timeBlock;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.protocol = protocol;
        this.numberOfEvents = numberOfEvents;
        this.sourceFile = sourceFile;
        this.uploadCode = uploadCode;
    }


    public ConnectionSummary(long timeBlock, long sourceIp, long destinationIp, int protocol, int numberOfEvents, String sourceFile, String uploadCode) {
        this.timeBlock = timeBlock;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.protocol = protocol;
        this.numberOfEvents = numberOfEvents;
        this.sourceFile = sourceFile;
        this.uploadCode = uploadCode;
    }

    private Integer id;

    private long timeBlock;

    private long sourceIp;

    private long destinationIp;

    private int protocol;

    private int numberOfEvents;

    private String sourceFile;

    private String uploadCode;
}
