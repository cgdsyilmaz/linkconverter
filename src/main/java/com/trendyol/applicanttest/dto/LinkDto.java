package com.trendyol.applicanttest.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Only need the original link field of entity link while
 * constructing the converted links.
 */
@Data
@Builder
public class LinkDto {

    @NonNull
    private String link;
}
