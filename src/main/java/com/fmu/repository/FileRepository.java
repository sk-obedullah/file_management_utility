package com.fmu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fmu.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long>{

}
