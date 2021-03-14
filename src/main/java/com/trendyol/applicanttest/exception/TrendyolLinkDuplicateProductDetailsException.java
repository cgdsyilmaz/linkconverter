package com.trendyol.applicanttest.exception;

public class TrendyolLinkDuplicateProductDetailsException extends TrendyolLinkException{

	public TrendyolLinkDuplicateProductDetailsException(String fieldName) {
		super(String.format("Following product field has multiple occurrences in the original link: %s", fieldName));
	}
}
