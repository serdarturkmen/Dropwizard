package com.serdar.comodo.service;

import com.serdar.comodo.db.ConnectionSummaryDao;
import com.serdar.comodo.model.TimeConverter;
import com.serdar.comodo.resource.dto.ConnectionRecordDto;
import com.serdar.comodo.model.ConnectionSummary;
import com.serdar.comodo.util.HttpMethodType;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This singleton service is responsible for
 * converting parquet file and storing its data to h2 database
 */
public class ParquetFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParquetFileService.class.getName());


    private static ParquetFileService instance = null;

    public ConnectionSummaryDao connectionSummaryDao;


    private ParquetFileService(ConnectionSummaryDao connectionSummaryDao) {
        this.connectionSummaryDao = connectionSummaryDao;
    }

    /**
     * add file by request type
     * if req method is post -> remove all the records related to the uploaded code
     * and then recreate tables
     * else if req methos is patch we will be updating records
     * @param path
     * @param fileName
     * @param uploadCode
     * @param type
     * @throws IOException
     *
     */
    public void addParquetFile(String path, String fileName, String uploadCode, HttpMethodType type) throws IOException {

        if(type == HttpMethodType.POST){
            // delete old records and create new ones

            //delete old records which has this code
            List<ConnectionSummary> existingRecords = connectionSummaryDao.findByUploadCode(uploadCode);
            if(existingRecords.size()>0){
                connectionSummaryDao.deleteRecordsByCode(uploadCode);
            }
            Map<ConnectionRecordDto, Integer> newRecords = new HashMap<>();
            parseRecordsByCount(newRecords, path);
            saveRecordsToDb(newRecords, fileName, uploadCode);

        }else if(type == HttpMethodType.PATCH){

            // at patch method we will update existing records
            List<ConnectionSummary> recordsFromDb = connectionSummaryDao.findByUploadCode(uploadCode);
            Map<ConnectionRecordDto, Integer> existingRecords = new HashMap<>();
            //add existing records to newRecords
            if(recordsFromDb.size()>0){
                recordsFromDb.forEach(element -> {
                    ConnectionRecordDto connectionRecordDto = new ConnectionRecordDto();
                    connectionRecordDto.setSourceIp(element.getSourceIp());
                    connectionRecordDto.setDestinationIp(element.getDestinationIp());
                    connectionRecordDto.setTimestamp(element.getTimeBlock());
                    connectionRecordDto.setProtocol(element.getProtocol());
                    Integer count = existingRecords.get(connectionRecordDto);
                    if (count == null) {
                        existingRecords.put(connectionRecordDto, element.getNumberOfEvents());

                    } else {
                        count++;
                        existingRecords.put(connectionRecordDto, count);
                    }
                });
            }

            if(recordsFromDb.size()>0){
                connectionSummaryDao.deleteRecordsByCode(uploadCode);
            }

            parseRecordsByCount(existingRecords, path);
            saveRecordsToDb(existingRecords, fileName, uploadCode);
        }


    }

    /**
     * saving all records to the db with
     * adding them fileName and upload code
     *
     * @param recordsWithCount
     * @param name
     * @param code
     */
    private void saveRecordsToDb(Map<ConnectionRecordDto, Integer> recordsWithCount, String name, String code) {
        recordsWithCount.forEach((summaryDTO, count) -> {
            ConnectionSummary connectionSummary = new ConnectionSummary();
            connectionSummary.setSourceIp(summaryDTO.getSourceIp());
            connectionSummary.setDestinationIp(summaryDTO.getDestinationIp());
            connectionSummary.setProtocol(summaryDTO.getProtocol());
            connectionSummary.setTimeBlock(summaryDTO.getTimestamp());
            connectionSummary.setNumberOfEvents(count);
            connectionSummary.setSourceFile(name);
            connectionSummary.setUploadCode(code);
            connectionSummaryDao.insert(connectionSummary);
        });
    }

    /**
     * Parse recods by record counts in a 1 hour range
     * timeStamp for records will be set 10 1 hour range
     *
     * @param recordsWithCount
     * @param path
     * @throws IOException
     */
    private void parseRecordsByCount(Map<ConnectionRecordDto, Integer> recordsWithCount, String path) throws IOException {

        Path file = new Path(path);
        AvroParquetReader.Builder<GenericRecord> builder = AvroParquetReader.builder(file);
        ParquetReader<GenericRecord> reader = builder.build();
        GenericRecord record;
        while ((record = reader.read()) != null) {
            ConnectionRecordDto connectionRecordDTO = new ConnectionRecordDto();
            connectionRecordDTO.setSourceIp((long) record.get("src_ip"));
            connectionRecordDTO.setDestinationIp((long) record.get("dst_ip"));
            //With timeconverter we are changing time to 1 hour resolution
            long time = (long) record.get("time");
            long timeHourResolution = TimeConverter.milisInHourResolution(time);
            connectionRecordDTO.setTimestamp(timeHourResolution);
            connectionRecordDTO.setProtocol((int) record.get("protocol"));
            Integer count = recordsWithCount.get(connectionRecordDTO);
            if (count == null) {
                recordsWithCount.put(connectionRecordDTO, 1);

            } else {
                count++;
                recordsWithCount.put(connectionRecordDTO, count);
            }
        }
    }


    public static ParquetFileService getInstance(ConnectionSummaryDao connectionSummaryDao) {
        if(instance == null) {
            instance = new ParquetFileService(connectionSummaryDao  );
        }
        return instance;
    }
}

