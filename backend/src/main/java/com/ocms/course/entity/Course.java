
package com.ocms.course.entity;

import com.ocms.course.enums.CourseStatus;
import com.ocms.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "courses",
        indexes = {
                @Index(name = "idx_course_status", columnList = "status"),
                @Index(name = "idx_course_category", columnList = "category"),
                @Index(name = "idx_course_instructor", columnList = "instructor_id")
        }
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseStatus status = CourseStatus.DRAFT;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sequenceOrder ASC")
    private List<CourseContent> contents = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Course() {
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }

    public void addContent(CourseContent content) {

        contents.add(content);
        content.setCourse(this);
    }

    public void clearContents() {

        for (CourseContent content : contents) {
            content.setCourse(null);
        }

        contents.clear();
    }

}
