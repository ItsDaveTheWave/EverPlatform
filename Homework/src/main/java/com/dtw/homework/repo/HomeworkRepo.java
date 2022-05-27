package com.dtw.homework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.homework.entity.Homework;

public interface HomeworkRepo extends JpaRepository<Homework, Long> {

}