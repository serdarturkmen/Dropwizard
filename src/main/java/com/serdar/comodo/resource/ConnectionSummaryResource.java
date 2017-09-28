package com.serdar.comodo.resource;

import com.codahale.metrics.annotation.Timed;
import com.serdar.comodo.db.ConnectionSummaryDao;
import com.serdar.comodo.db.util.WhereClause;
import com.serdar.comodo.model.ConnectionSummary;
import com.serdar.comodo.resource.dto.ConnectionSummaryDto;
import com.serdar.comodo.util.CSVReader;
import com.serdar.comodo.util.IpAddressConverter;
import io.dropwizard.hibernate.UnitOfWork;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Path("/connectionSummary")
@Produces(MediaType.APPLICATION_JSON)
public class ConnectionSummaryResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadResource.class);

    private ConnectionSummaryDao connectionSummaryDAO;

    public ConnectionSummaryResource(ConnectionSummaryDao connectionSummaryDAO) {
        this.connectionSummaryDAO = connectionSummaryDAO;
    }

    /**
     * fetching result from db with generating a preparing statement
     * from mehtod generatePrepareStatement()
     *
     * @param srcIpBlock
     * @param dstIpBlock
     * @param protocol
     * @param startInMillis
     * @param endInMillis
     * @param connectionThreshold
     * @param hourResolution
     * @return {@link List<ConnectionSummaryDto>}
     */
    @GET
    @Path("/summary")
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response getConnectionSummary(
            @DefaultValue("none") @QueryParam("srcIpBlock") String srcIpBlock,
            @DefaultValue("none") @QueryParam("dstIpBlock") String dstIpBlock,
            @DefaultValue("none") @QueryParam("protocol") String protocol,
            @DefaultValue("-1") @QueryParam("startTime") Long startInMillis,
            @DefaultValue("-1") @QueryParam("endTime") Long endInMillis,
            @DefaultValue("-1") @QueryParam("connectionThreshold") Integer connectionThreshold,
            @DefaultValue("1") @QueryParam("hourResolution") Integer hourResolution
    ) {

        WhereClause whereClause = generatePrepareStatement(srcIpBlock,dstIpBlock, protocol ,startInMillis,endInMillis,connectionThreshold,hourResolution);
        // query by this fields

        // todo hour resolution check
        ConnectionSummary sum = new ConnectionSummary();
        sum.setTimeBlock(hourResolution);
        sum.setProtocol(1);

        List<ConnectionSummaryDto> dtoList = new ArrayList<>();

        List<ConnectionSummary> fieldsSecond = connectionSummaryDAO.findAllFields(whereClause.preparedString,whereClause);
        fieldsSecond.forEach(element->{
            dtoList.add(new ConnectionSummaryDto(IpAddressConverter.longToIp(element.getSourceIp()), IpAddressConverter.longToIp(element.getDestinationIp()), element.getTimeBlock(), CSVReader.getInstance().convertNumberToCode(element.getProtocol()) + "", element.getNumberOfEvents()));
        });

        return Response.status(200).entity(dtoList).build();
    }

    /**
     *
     * @param srcIpBlock
     * @param destIpBlock
     * @param protocol
     * @param startInMillis
     * @param endInMillis
     * @param minEvent
     * @param hourResolution
     * @return whereClause of sql statemt
     */
    public WhereClause generatePrepareStatement(String srcIpBlock, String destIpBlock, String protocol, Long startInMillis, Long endInMillis, Integer minEvent, Integer hourResolution ) {
        boolean checkWhereString = false;
        String where = "";
        WhereClause whereClause = new WhereClause();
        whereClause.queryValues = new HashMap<>();
        String preparedString = "";

        if (!protocol.equals("none")) {
            checkWhereString = true;
            preparedString = "protocol= :protocol";
            whereClause.queryValues.put("protocol", CSVReader.getInstance().convertCodeToNUmber(protocol));
        }

        if (!srcIpBlock.equals("none")) {

            Pair<String, String> pair = IpAddressConverter.getIpBlockFromCidrMask(srcIpBlock);
            long startIp = IpAddressConverter.ipToLong(pair.getLeft());
            long endIp = IpAddressConverter.ipToLong(pair.getRight());

            checkWhereString = true;
            if (!preparedString.isEmpty()) {
                preparedString += " AND src_ip between "+startIp +" and "+ endIp;
            } else {
                preparedString += "src_ip between "+startIp +" and "+ endIp;
            }
        }

        if (!destIpBlock.equals("none")) {
            Pair<String, String> pair = IpAddressConverter.getIpBlockFromCidrMask(destIpBlock);
            long startIp = IpAddressConverter.ipToLong(pair.getLeft());
            long endIp = IpAddressConverter.ipToLong(pair.getRight());

            checkWhereString = true;
            if (!preparedString.isEmpty()) {
                preparedString += " AND dest_ip between " +startIp +" and "+ endIp;
            } else {
                preparedString += "dest_ip between "+startIp +" and "+ endIp;
            }
            whereClause.queryValues.put("destIp", Integer.parseInt(destIpBlock));
        }

        if (!startInMillis.equals(-1L)) {
            checkWhereString = true;
            if (!preparedString.isEmpty()) {
                preparedString += " AND hour_range>= :startInMillis";
            } else {
                preparedString += "hour_range>= :startInMillis";
            }
            whereClause.queryValues.put("startInMillis", startInMillis);
        }

        if (!endInMillis.equals(-1L)) {
            checkWhereString = true;
            if (!preparedString.isEmpty()) {
                preparedString += " AND hour_range<= :endInMillis";
            } else {
                preparedString += "hour_range<= :endInMillis";
            }
            whereClause.queryValues.put("endInMillis", endInMillis);
        }


        if (!minEvent.equals(-1)) {
            checkWhereString = true;
            if (!preparedString.isEmpty()) {
                preparedString += " AND number_of_events>= :minEvent";
            } else {
                preparedString += "number_of_events>= :minEvent";
            }
            whereClause.queryValues.put("minEvent", minEvent);
        }

        if (checkWhereString) {
            whereClause.preparedString = "where " + preparedString;
        }

        return whereClause;
    }


    @GET
    @Timed
    @Path("/")
    public List<ConnectionSummary> listAll() {
        List<ConnectionSummary> tcp = connectionSummaryDAO.getAll();
        return tcp;
    }

    @DELETE
    @Path("/{code}")
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response deleteConnectionByCode(@PathParam("code") String uploadCode) {
        connectionSummaryDAO.deleteRecordsByCode(uploadCode);
        return Response.status(200).entity("deleted records").build();
    }

    /**
     * Search files by upload codes
     *
     * @param uploadCode
     * @return List<FileName>
     */
    @GET
    @Path("/{code}")
    public Response searchFileByCode(@PathParam("code") String uploadCode) {
        List<ConnectionSummary> summaries = connectionSummaryDAO.findByUploadCode(uploadCode);
        List<ConnectionSummaryDto> dtoList = new ArrayList<>();
        summaries.forEach(element ->{
            dtoList.add(new ConnectionSummaryDto(IpAddressConverter.longToIp(element.getSourceIp()), IpAddressConverter.longToIp(element.getDestinationIp()), element.getTimeBlock(), CSVReader.getInstance().convertNumberToCode(element.getProtocol()) + "", element.getNumberOfEvents()));

        });

        return Response.status(200).entity(dtoList).build();
    }

}
