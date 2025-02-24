package com.spring.delivery.domain.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreCategory is a Querydsl query type for StoreCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreCategory extends EntityPathBase<StoreCategory> {

    private static final long serialVersionUID = -327632326L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreCategory storeCategory = new QStoreCategory("storeCategory");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCategory category;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QStore store;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QStoreCategory(String variable) {
        this(StoreCategory.class, forVariable(variable), INITS);
    }

    public QStoreCategory(Path<? extends StoreCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreCategory(PathMetadata metadata, PathInits inits) {
        this(StoreCategory.class, metadata, inits);
    }

    public QStoreCategory(Class<? extends StoreCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.store = inits.isInitialized("store") ? new QStore(forProperty("store"), inits.get("store")) : null;
    }

}

