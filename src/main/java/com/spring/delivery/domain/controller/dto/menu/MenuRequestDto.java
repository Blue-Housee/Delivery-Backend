package com.spring.delivery.domain.controller.dto.menu;


<<<<<<< HEAD
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
=======
import com.spring.delivery.domain.domain.entity.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

>>>>>>> 13da6d5d308f526412fd69768e96ffa253678f7b
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

<<<<<<< HEAD
=======
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

>>>>>>> 13da6d5d308f526412fd69768e96ffa253678f7b

}
