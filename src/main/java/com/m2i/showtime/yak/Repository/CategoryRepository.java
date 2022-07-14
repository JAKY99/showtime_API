package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
