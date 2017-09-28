package com.serdar.comodo.resource.dto;

import lombok.Data;

@Data
public class ConnectionRecordDto {

    private long sourceIp;

    private long destinationIp;

    private long timestamp;

    private int protocol;

}
