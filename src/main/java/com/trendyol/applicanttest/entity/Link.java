package com.trendyol.applicanttest.entity;

import com.trendyol.applicanttest.enums.LinkType;
import com.trendyol.applicanttest.enums.PageType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Link entity class only will be used while saving to
 * the Postgresql database.
 */

@Entity
@Table(name = "link_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    @Column(name = "original_link")
    private String originalLink;

    @NonNull
    @Column(name = "converted_link")
    private String convertedLink;

    @NonNull
    @Column(name = "original_link_type")
    private LinkType linkType;

    @NonNull
    @Column(name = "original_link_page")
    private PageType pageType;
}
