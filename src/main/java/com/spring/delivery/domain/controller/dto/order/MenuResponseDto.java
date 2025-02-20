package com.spring.delivery.domain.controller.dto.order;

import com.spring.delivery.domain.domain.entity.Menu;
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
    private Boolean publicStatus;
    private String menuImage;
    private UUID storeId;
    private LocalDateTime createdAt;
    private String createdBy;


    private MenuResponseDto(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
        this.publicStatus = menu.isPublic_status();
        this.menuImage = menu.getMenu_image();
        this.storeId = menu.getStore().getId();
        this.createdAt = menu.getCreatedAt();
        this.createdBy = menu.getCreatedBy();
    }

    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(menu);
    }

}
