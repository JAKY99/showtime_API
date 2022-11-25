package com.m2i.showtime.yak.Dto.Search;

public enum FiltersMatchModeEnum {
    STARTS_WITH("startsWith"), CONTAINS("contains"), NOT_CONTAINS("notContains"), ENDS_WITH("endsWith"), EQUALS(
            "equals"), NOT_EQUALS("notEquals");

    private final String filterMatchMode;
    FiltersMatchModeEnum(String filterMatchMode) {
        this.filterMatchMode = filterMatchMode;
    }

    public String getFilterMatchMode() {
        return filterMatchMode;
    }
}
