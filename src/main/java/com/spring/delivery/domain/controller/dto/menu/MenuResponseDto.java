package com.spring.delivery.domain.controller.dto.menu;

import com.spring.delivery.domain.domain.entity.Menu;
<<<<<<< HEAD
=======
import lombok.Builder;
>>>>>>> 13da6d5d308f526412fd69768e96ffa253678f7b
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


<<<<<<< HEAD
    private MenuResponseDto(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
        this.publicStatus = menu.isPublicStatus();
        this.menuImage = menu.getMenuImage();
        this.storeId = menu.getStore().getId();
        this.createdAt = menu.getCreatedAt();
        this.createdBy = menu.getCreatedBy();
    }

    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(menu);
=======
    @Builder
    private MenuResponseDto(UUID id, String name, Long price, String description, Boolean publicStatus, String menuImage, UUID storeId, LocalDateTime createdAt, String createdBy) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.publicStatus = publicStatus;
        this.menuImage = menuImage;
        this.storeId = storeId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public static MenuResponseDto from(Menu menu) {
        return MenuResponseDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .publicStatus(menu.isPublicStatus())
                .menuImage(menu.getMenuImage())
                .storeId(menu.getStore().getId())
                .createdAt(menu.getCreatedAt())
                .createdBy(menu.getCreatedBy())
                .build();
>>>>>>> 13da6d5d308f526412fd69768e96ffa253678f7b
    }

}
