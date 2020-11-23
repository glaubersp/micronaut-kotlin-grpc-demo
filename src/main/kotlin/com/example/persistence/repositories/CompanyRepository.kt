package com.example.persistence.repositories

import com.example.persistence.entities.CompanyEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface CompanyRepository : CrudRepository<CompanyEntity, Long>
