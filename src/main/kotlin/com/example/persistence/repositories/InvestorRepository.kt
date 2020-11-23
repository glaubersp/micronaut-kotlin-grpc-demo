package com.example.persistence.repositories

import com.example.persistence.entities.InvestorEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface InvestorRepository : CrudRepository<InvestorEntity, Long>
