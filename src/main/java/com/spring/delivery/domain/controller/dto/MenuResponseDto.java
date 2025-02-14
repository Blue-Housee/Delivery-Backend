package com.spring.delivery.domain.controller.dto;

import com.spring.delivery.domain.domain.entity.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@NoArgsConstructor
public class MenuResponseDto {

    private UUID id;
    private String name;
    private Long price;
    private String description;
    private Boolean public_status;
    private String menu_image;
    private UUID storeId;
    private LocalDateTime created_at;
    private String created_by;


    public MenuResponseDto(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
        this.public_status = menu.isPublic_status();
        this.menu_image = menu.getMenu_image();
        this.storeId = menu.getStore().getId();
        this.created_at = menu.getCreatedAt();
        this.created_by = menu.getCreatedBy();
    }



}
