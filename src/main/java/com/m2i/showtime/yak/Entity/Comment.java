package com.m2i.showtime.yak.Entity;


import lombok.Getter;
import lombok.Setter;
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

        private Long movie_id;

        @ManyToOne
        private User user;

        private String content;
        private LocalDateTime datePublication = LocalDateTime.now();
        private boolean isValidate = false;
        private boolean isSpoiler = false;
        private boolean isDeleted = false;

        @OneToMany(cascade = CascadeType.ALL)
        private Set<Like> likes;

        public Comment() {
        }

        public Comment(Long movie_id, User user, String content, LocalDateTime datePublication, boolean isValidate, boolean isSpoiler, boolean isDeleted) {
                this.movie_id = movie_id;
                this.user = user;
                this.content = content;
                this.datePublication = datePublication;
                this.isValidate = isValidate;
                this.isSpoiler = isSpoiler;
                this.isDeleted = isDeleted;
        }
}
