package com.spring.delivery.domain.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = 191132956L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStore store = new QStore("store");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<Menu, QMenu> menus = this.<Menu, QMenu>createList("menus", Menu.class, QMenu.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final BooleanPath openStatus = createBoolean("openStatus");

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final ListPath<StoreCategory, QStoreCategory> storeCategories = this.<StoreCategory, QStoreCategory>createList("storeCategories", StoreCategory.class, QStoreCategory.class, PathInits.DIRECT2);

    public final StringPath tel = createString("tel");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final QUser user;

    public QStore(String variable) {
        this(Store.class, forVariable(variable), INITS);
    }

    public QStore(Path<? extends Store> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStore(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStore(PathMetadata metadata, PathInits inits) {
        this(Store.class, metadata, inits);
    }

    public QStore(Class<? extends Store> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

