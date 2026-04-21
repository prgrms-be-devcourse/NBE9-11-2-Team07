package com.back.mozu.domain.reservation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTimeSlot is a Querydsl query type for TimeSlot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTimeSlot extends EntityPathBase<TimeSlot> {

    private static final long serialVersionUID = -1382316959L;

    public static final QTimeSlot timeSlot = new QTimeSlot("timeSlot");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> stock = createNumber("stock", Integer.class);

    public final TimePath<java.time.LocalTime> time = createTime("time", java.time.LocalTime.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public QTimeSlot(String variable) {
        super(TimeSlot.class, forVariable(variable));
    }

    public QTimeSlot(Path<? extends TimeSlot> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTimeSlot(PathMetadata metadata) {
        super(TimeSlot.class, metadata);
    }

}

