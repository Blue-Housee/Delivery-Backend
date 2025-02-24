package com.spring.delivery.domain.domain.entity;

import com.spring.delivery.domain.controller.dto.menu.MenuRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_menu")
public class Menu extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column
    private String name;

    @Column
    private Long price;

    @Column
    private String description;

    @Column(name = "public_status")
    private boolean publicStatus;

    @Column(name = "menu_image")
    private String menuImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "menu")
    private List<MenuOrder> menuOrderList = new ArrayList<>();


    private Menu(String name, Long price, String description, boolean publicStatus, String menuImage, Store store) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.publicStatus = publicStatus;
        this.menuImage = menuImage;
        this.store = store;
    }

    public static Menu of(MenuRequestDto requestDto, Store store) {
        return new Menu(
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getDescription(),
                requestDto.getPublicStatus(),
                requestDto.getMenuImage(),
                store
        );
    }

    public static void update(Menu menu, MenuRequestDto requestDto) {
        if (requestDto.getName() != null) menu.name = requestDto.getName();
        if (requestDto.getPrice() != null) menu.price = requestDto.getPrice();
        if (requestDto.getDescription() != null) menu.description = requestDto.getDescription();
        if (requestDto.getPublicStatus() != null) menu.publicStatus = requestDto.getPublicStatus();
        if (requestDto.getMenuImage() != null) menu.menuImage = requestDto.getMenuImage();
    }

    public void delete(String deletedBy) {
        super.delete(deletedBy);
        this.publicStatus = false;
    }


}
