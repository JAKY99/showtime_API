package com.m2i.showtime.yak.Configuration;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebFilter;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebFilter;

@Component
@WebFilter(urlPatterns = "/*")
public class JpaEntityManagerFilter extends OpenEntityManagerInViewFilter {
}