package com.serdar.comodo.resource;

import com.serdar.comodo.db.ConnectionSummaryDao;
import com.serdar.comodo.service.FileService;
import com.serdar.comodo.service.ParquetFileService;
import com.serdar.comodo.util.HttpMethodType;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
public class FileUploadResource {

    private ConnectionSummaryDao connectionSummaryDAO;

    public String fileUploadLocation;

    public FileUploadResource(ConnectionSummaryDao connectionSummaryDAO, String fileUploadLocation) {
        this.connectionSummaryDAO = connectionSummaryDAO;
        this.fileUploadLocation = fileUploadLocation;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadResource.class);


    /**
     * I choose post for first scenario (Post is chosen becouse of creating new values)
     *
     * @param uploadedInputStream
     * @param fileDetail
     * @param uploadCode
     * @return
     * @throws IOException
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload")
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("uploadCode") String uploadCode) throws IOException {

        if (uploadCode.equals("undefined") || uploadCode.length() < 3 || uploadCode.length() > 10) {
            return Response.status(500).build();
        } else {
            String uploadedFileLocation = fileUploadLocation + fileDetail.getFileName() + "-"+ uploadCode;

            //write inputstream to file
            FileService.getInstance().writeToFile(uploadedInputStream, uploadedFileLocation);
            ParquetFileService.getInstance(connectionSummaryDAO).addParquetFile(uploadedFileLocation, fileDetail.getFileName(), uploadCode, HttpMethodType.POST);
            return Response.ok("File has uploaded to : " + uploadedFileLocation).build();
        }

    }

    /**
     * PATH should only be used if you're replacing a resource in it's entirety.
     *
     * @param uploadedInputStream
     * @param fileDetail
     * @param uploadCode
     * @return
     * @throws IOException
     */
    @PATCH
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload")
    public Response patchFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("uploadCode") String uploadCode) throws IOException {
        if (uploadCode.length() < 3 || uploadCode.length() > 10) {
            return Response.status(500).build();
        } else {
            String uploadedFileLocation = fileUploadLocation + fileDetail.getFileName() + "-"+ uploadCode;
            LOGGER.info(uploadedFileLocation);

            //write inputstream to file
            FileService.getInstance().writeToFile(uploadedInputStream, uploadedFileLocation);
            ParquetFileService.getInstance(connectionSummaryDAO).addParquetFile(uploadedFileLocation, fileDetail.getFileName(), uploadCode, HttpMethodType.PATCH);
            return Response.ok("File has uploaded to : " + uploadedFileLocation).build();
        }
    }

    /**
     * Search files by upload codes
     *
     * @param uploadCode
     * @return List<FileName>
     */
    @GET
    @Path("/searchFile/{code}")
    public Response searchFileByCode(@PathParam("code") String uploadCode) {
        List<String> byUploadCodeName = connectionSummaryDAO.findUniqueFileNames(uploadCode);
        return Response.ok(byUploadCodeName).build();
    }

    /**
     * Deleting files from path
     *
     * @param uploadCode
     * @return result of delete operation
     */
    @DELETE
    @Path("/{code}")
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response deleteFilesByCode(@PathParam("code") String uploadCode) {
        List<String> byUploadCodeName = connectionSummaryDAO.findUniqueFileNames(uploadCode);
        final String[] results = new String[1];
        if (byUploadCodeName.size() > 0) {
            byUploadCodeName.forEach(name -> {
                results[0] = FileService.getInstance().removeFileFromPath(fileUploadLocation + name + "-" + uploadCode);
            });
            return Response.ok(results).build();
        }

        return Response.ok("There is no file").build();
    }


}
