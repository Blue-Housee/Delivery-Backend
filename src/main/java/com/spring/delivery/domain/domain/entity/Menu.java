package com.spring.delivery.domain.domain.entity;

import com.spring.delivery.domain.controller.dto.MenuRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    private boolean public_status;

    @Column(name = "menu_image")
    private String menu_image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "menu")
    private List<MenuOrder> menuOrderList = new ArrayList<>();

    @Builder
    public Menu(MenuRequestDto requestDto, Store store) {
        this.name = requestDto.getName();
        this.price = requestDto.getPrice();
        this.description = requestDto.getDescription();
        this.public_status = true;
        this.menu_image = requestDto.getMenuImage();
        this.store = store;
    }

}
