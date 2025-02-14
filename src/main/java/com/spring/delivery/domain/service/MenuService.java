package com.spring.delivery.domain.service;


import com.spring.delivery.domain.domain.Menu;
import com.spring.delivery.domain.domain.Store;
import com.spring.delivery.domain.dto.MenuRequestDto;
import com.spring.delivery.domain.dto.MenuResponseDto;
import com.spring.delivery.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuResponseDto createMenu(MenuRequestDto requestDto) {

        Store store = storeRepository.finById(requestDto.getStore_id())
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));

        menuRepository.save(menu);

        return new MenuResponseDto(menu);
    }

    public MenuResponseDto updateMenu(Long menuId, MenuRequestDto requestDto) {
        return null;
    }

    public void deleteMenu(Long menuId) {
    }
}
