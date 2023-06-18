package com.m2i.showtime.yak.Entity;


import com.fasterxml.jackson.annotation.*;
import com.m2i.showtime.yak.common.comment.CommentType;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "comment")
@Getter
@Setter

public class Comment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long element_id;

        @ManyToOne
        @JsonIgnoreProperties("comments")
        private User user;
        private String content;
        private LocalDateTime datePublication = LocalDateTime.now();
        private boolean isValidate = true;
        private boolean isSpoiler = false;
        private boolean isDeleted = false;

        @OneToMany(cascade = CascadeType.ALL)
        @JsonManagedReference
        private Set<Like> likes;

        @OneToMany(cascade = CascadeType.ALL)
        private Set<Response> responses;
        @Enumerated(EnumType.STRING)
        private CommentType typeElement;
        public Comment() {
        }

        public Comment(Long element_id, User user, String content, LocalDateTime datePublication, boolean isValidate, boolean isSpoiler, boolean isDeleted,CommentType typeElement) {
                this.element_id = element_id;
                this.user = user;
                this.content = content;
                this.datePublication = datePublication;
                this.isValidate = isValidate;
                this.isSpoiler = isSpoiler;
                this.isDeleted = isDeleted;
                this.typeElement = typeElement;
        }
}
