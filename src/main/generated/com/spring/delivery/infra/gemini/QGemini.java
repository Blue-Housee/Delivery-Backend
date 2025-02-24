package com.spring.delivery.infra.gemini;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGemini is a Querydsl query type for Gemini
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGemini extends EntityPathBase<Gemini> {

    private static final long serialVersionUID = 1054897642L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGemini gemini = new QGemini("gemini");

    public final com.spring.delivery.domain.domain.entity.QBaseEntity _super = new com.spring.delivery.domain.domain.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath requestText = createString("requestText");

    public final StringPath responseText = createString("responseText");

    public final com.spring.delivery.domain.domain.entity.QStore store;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QGemini(String variable) {
        this(Gemini.class, forVariable(variable), INITS);
    }

    public QGemini(Path<? extends Gemini> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGemini(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGemini(PathMetadata metadata, PathInits inits) {
        this(Gemini.class, metadata, inits);
    }

    public QGemini(Class<? extends Gemini> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.spring.delivery.domain.domain.entity.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

