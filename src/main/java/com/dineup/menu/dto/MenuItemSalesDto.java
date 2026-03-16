package com.dineup.menu.dto;

public record MenuItemSalesDto(
    Long menuItemId,
    String menuItemName,
    Long totalQuantitySold
) {}
