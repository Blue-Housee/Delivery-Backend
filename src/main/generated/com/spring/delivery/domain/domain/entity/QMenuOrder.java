package com.spring.delivery.domain.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMenuOrder is a Querydsl query type for MenuOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenuOrder extends EntityPathBase<MenuOrder> {

    private static final long serialVersionUID = 1796729834L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMenuOrder menuOrder = new QMenuOrder("menuOrder");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QMenu menu;

    public final QOrder order;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QMenuOrder(String variable) {
        this(MenuOrder.class, forVariable(variable), INITS);
    }

    public QMenuOrder(Path<? extends MenuOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMenuOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMenuOrder(PathMetadata metadata, PathInits inits) {
        this(MenuOrder.class, metadata, inits);
    }

    public QMenuOrder(Class<? extends MenuOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.menu = inits.isInitialized("menu") ? new QMenu(forProperty("menu"), inits.get("menu")) : null;
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
    }

}

