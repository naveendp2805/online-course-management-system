
package com.ocms.course.entity;

import com.ocms.course.enums.ContentType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "course_contents")
@Setter
public class CourseContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 150)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentType contentType;

    @Setter
    @Column(nullable = false, length = 1000)
    private String resourceUrl;

    @Setter
    @Column(nullable = false)
    private Integer sequenceOrder;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public CourseContent() {
    }

}
