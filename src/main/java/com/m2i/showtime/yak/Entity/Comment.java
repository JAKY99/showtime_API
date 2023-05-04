package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
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
