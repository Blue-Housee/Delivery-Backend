package com.spring.delivery.domain.fixture;

import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;

public class DeliveryAddressFixtureGenerator {
    public static final String ADDRESS = "345 Test Street";
    public static final String REQUEST = "Leave at door";
    public static final String UPDATE_ADDRESS = "123 Test Street";

    public static final String CREATE_SUCCESS_MESSAGE = "배송지가 생성되었습니다";
    public static final String UPDATE_SUCCESS_MESSAGE = "배송지가 수정되었습니다.";
    public static final String ALREADY_DELETE_DATA_MESSAGE = "삭제된 데이터입니다";
    public static final String ALREADY_DATA_MESSAGE = "이미 존재하는 배송지입니다.";
    public static final String MAXIMUM_DATA_MESSAGE = "최대 배송지는 3개입니다.";
    public static final String SAME_DATA_MESSAGE = "수정할 배송지와 기존 배송지가 같습니다.";
    public static final String NO_DATA_MESSAGE = "해당되는 배송지가 없습니다.";

    // 공통적으로 사용되는 DTO 생성 헬퍼 메소드
    public static DeliveryAddressRequestDto createDto(String address, String request) {
        DeliveryAddressRequestDto dto = new DeliveryAddressRequestDto();
        dto.setAddress(address);
        dto.setRequest(request);
        return dto;
    }
}
