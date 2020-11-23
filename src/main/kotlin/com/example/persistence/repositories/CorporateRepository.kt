package com.example.persistence.repositories

import com.example.persistence.entities.CorporateAddressEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface CorporateAddressRepository : CrudRepository<CorporateAddressEntity, Long> {
    fun findByCompanyEntityId(companyId: Long): List<CorporateAddressEntity>
}
