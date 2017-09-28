package com.serdar.comodo.db;

import com.serdar.comodo.db.util.BindWhereClause;
import com.serdar.comodo.db.util.WhereClause;
import com.serdar.comodo.model.ConnectionSummary;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

@RegisterMapper(ConnectionSummaryMapper.class)
@UseStringTemplate3StatementLocator
public interface ConnectionSummaryDao {

    @SqlUpdate("create table IF NOT EXISTS connection_summary (id int auto_increment primary key, hour_range bigint(80), src_ip bigint(80), dest_ip bigint(80), protocol bigint(80), number_of_events bigint(80), source_file varchar(80), upload_code varchar(80))")
    void createTable();

    @SqlUpdate("insert into connection_summary (hour_range, src_ip, dest_ip, protocol, number_of_events, source_file, upload_code) values (:timeBlock, :sourceIp, :destinationIp, :protocol, :numberOfEvents, :sourceFile, :uploadCode)")
    void insert(@BindBean ConnectionSummary connectionSummary);

    @SqlUpdate("delete from connection_summary where upload_code = :it")
    void deleteRecordsByCode(@Bind String uploadCode);

    @SqlQuery("select * from connection_summary")
    List<ConnectionSummary> getAll();

    @SqlQuery("select * from connection_summary where upload_code = :uploadCode")
    List<ConnectionSummary> findByUploadCode(@Bind("uploadCode") String uploadCode);

    @SqlQuery("select distinct(source_file) from connection_summary where upload_code = :uploadCode")
    List<String> findUniqueFileNames(@Bind("uploadCode") String uploadCode);

    @SqlQuery("select * from connection_summary <where>")
    List<ConnectionSummary> findAllFields(@Define("where") String where, @BindWhereClause() WhereClause whereClause);

}

