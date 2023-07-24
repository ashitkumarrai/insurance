package com.serivce.insurance.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.serivce.insurance.entity.File;
import com.serivce.insurance.exceptionhandler.RecordNotFoundException;
import com.serivce.insurance.repository.FileRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class FileController {

    @Autowired
    FileRepository fr;

    @PostMapping(value = "/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "uploadMedia", responses = {
        @ApiResponse(responseCode = "401", description = "Unauthorized request" ),
       @ApiResponse(responseCode = "403", description = "Forbidden request"),
       @ApiResponse(responseCode = "201", description = "sucessfull") },description = "upload media",summary = "UPLOAD MEDIA")
    public @ResponseBody ResponseEntity<Object> uploadmedia(HttpServletRequest request,
            final  @RequestPart("file") MultipartFile file) {
        try {

            String name = file.getOriginalFilename();
            log.info(name);

            if (name == null || name.contains("..") || name.contains(" ")) {
                Map<String, String> hasMap = new HashMap<>();

                hasMap.put("Error",
                        "Sorry! Filename contains invalid path sequence, correct name format before upload");

                return new ResponseEntity<>(hasMap, HttpStatus.BAD_REQUEST);

            }

            byte[] mediaData = file.getBytes();

            File f = File.builder().media(mediaData)

                    .mediaContentType(file.getContentType())
                    .id(UUID.randomUUID().toString() + file.getOriginalFilename()).build();

            fr.save(f);

            Map<String, String> hasMap = new HashMap<>();
            hasMap.put("mediaUrl", new URI("/media/show/" + f.getId()).toString());

            return new ResponseEntity<>(hasMap, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception: " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/media/show/{id}")
       @Operation(operationId = "getMediaById", responses = {
   
    @ApiResponse(responseCode = "200", description = "sucessfull") },description = "get media by id",summary = "GET MEDIA BY ID")
    @ResponseBody

    void showMedia(@PathVariable("id") String id, HttpServletResponse response)
            throws IOException, RecordNotFoundException {
        File dbSavedFile = fr.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("media not found with id: " + id));

        response.setContentType(dbSavedFile.getMediaContentType());
        response.getOutputStream().write(dbSavedFile.getMedia());
        response.getOutputStream().close();

    }

    // @GetMapping("/media/show/all")
    // @Operation(operationId = "showAllMedia",summary = "SHOW ALL MEDIA URLS")
    // List<File> showAllMedia() {

    //     return fr.findAll();
    // }

}
