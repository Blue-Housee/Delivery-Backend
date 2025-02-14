package com.spring.delivery.domain.service;



import com.spring.delivery.domain.domain.entity.Menu;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.controller.dto.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.MenuResponseDto;
import com.spring.delivery.domain.repository.MenuRepository;
import com.spring.delivery.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;


    // 인증 개발 전단계라서 PreAuthorize 임시로 사용함
    //@PreAuthorize("hasRole('OWNER') or hasRole('MASTER')")
    //public MenuResponseDto createMenu(MenuRequestDto requestDto, UserDetails userDetails)  {
    public MenuResponseDto createMenu(MenuRequestDto requestDto)  {

        /* 권한 확인 로직 필요함 */

        /*
        boolean isOwnerOrMaster = true; // 지금은 Mock 데이터이므로 true로 설정
        if (!isOwnerOrMaster) {
            throw new AccessDeniedException("메뉴를 생성할 권한이 없습니다.");
        }
        */

        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게 입니다."));


        Menu menu = Menu.of(requestDto, store);

        // created_by 자동생성이 안된다...

        menuRepository.save(menu);

        return new MenuResponseDto(menu);
    }

    public MenuResponseDto updateMenu(Long menuId, MenuRequestDto requestDto) {
        return null;
    }

    public void deleteMenu(Long menuId) {
    }
}
