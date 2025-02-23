package com.spring.delivery.domain.controller.dto.menu;


import com.spring.delivery.domain.domain.entity.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class MenuRequestDto {

    // 메뉴명
    private String name;

    // 메뉴 가격
    private Long price;

    // 메뉴 설명
    private String description;

    // 메뉴 이미지 url
    private String menuImage;

    // 메뉴 노출 상태
    private Boolean publicStatus;

    // 가게 ID
    private UUID storeId;

    public static MenuRequestDto of(String name, Long price, String description, String menuImage, Boolean publicStatus, UUID storeId) {
        MenuRequestDto menuRequestDto = new MenuRequestDto();
        menuRequestDto.name = name;
        menuRequestDto.price = price;
        menuRequestDto.description = description;
        menuRequestDto.menuImage = menuImage;
        menuRequestDto.publicStatus = publicStatus;
        menuRequestDto.storeId = storeId;
        return menuRequestDto;
    }


}
