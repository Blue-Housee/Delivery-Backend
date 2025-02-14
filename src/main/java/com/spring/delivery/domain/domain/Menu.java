package com.spring.delivery.domain.domain;


import com.spring.delivery.domain.dto.MenuRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_menu")
@NoArgsConstructor
public class Menu extends Audit {

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
    private boolean public_status;

    @Column(name = "menu_image")
    private String menu_image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


    @Builder
    public Menu(MenuRequestDto requestDto, Store store) {
        this.name = requestDto.getName();
        this.price = requestDto.getPrice();
        this.description = requestDto.getDescription();
        this.public_status = true;
        this.menu_image = requestDto.getMenuImage();
        this.store = store;
    }


    public void updateMenu(MenuRequestDto requestDto) {
        this.name = requestDto.getName();
        this.price = requestDto.getPrice();
        this.description = requestDto.getDescription();
        this.public_status = true;
        this.menu_image = requestDto.getMenuImage();
    }

    public void deleteMenu(MenuRequestDto requestDto) {

    }

}



