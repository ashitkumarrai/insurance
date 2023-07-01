package com.serivce.insurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@JsonIgnoreProperties({ "media" })

public class File {

      @Id
    private String id;

    @Lob
    @Column(name = "image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] media;

    @Column(name = "meida_content_type")
    private String mediaContentType;

}
