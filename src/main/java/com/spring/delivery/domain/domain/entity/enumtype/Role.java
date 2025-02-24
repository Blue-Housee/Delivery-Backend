package com.spring.delivery.domain.domain.entity.enumtype;

public enum Role {
    CUSTOMER(Authority.CUSTOMER, 1),
    OWNER(Authority.OWNER, 2),
    MANAGER(Authority.MANAGER, 3),
    MASTER(Authority.MASTER, 4);

    private final String authority;
    private final int level; //역할의 순위

    Role(String authority, int level) {
        this.authority = authority;
        this.level = level;
    }

    public static boolean isGreaterThen(Role role, Role currentUserRole) {
        if (role == null || currentUserRole == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return role.level > currentUserRole.level;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }
}
