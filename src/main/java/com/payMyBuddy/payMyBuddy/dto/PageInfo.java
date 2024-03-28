package com.payMyBuddy.payMyBuddy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageInfo {
    private String label;
    private String url;

    public PageInfo(String label, String url) {
        this.label = label;
        this.url = url;
    }
}
