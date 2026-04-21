package com.back.mozu.domain.setting.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestaurantSettings is a Querydsl query type for RestaurantSettings
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantSettings extends EntityPathBase<RestaurantSettings> {

    private static final long serialVersionUID = 1221484794L;

    public static final QRestaurantSettings restaurantSettings = new QRestaurantSettings("restaurantSettings");

    public final TimePath<java.time.LocalTime> closingTime = createTime("closingTime", java.time.LocalTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final TimePath<java.time.LocalTime> openingTime = createTime("openingTime", java.time.LocalTime.class);

    public final NumberPath<Integer> totalTables = createNumber("totalTables", Integer.class);

    public QRestaurantSettings(String variable) {
        super(RestaurantSettings.class, forVariable(variable));
    }

    public QRestaurantSettings(Path<? extends RestaurantSettings> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurantSettings(PathMetadata metadata) {
        super(RestaurantSettings.class, metadata);
    }

}

