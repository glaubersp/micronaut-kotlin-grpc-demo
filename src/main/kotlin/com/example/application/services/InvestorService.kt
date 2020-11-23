package com.example.application.services

import com.example.grpc.investor.*
import com.example.persistence.entities.InvestorEntity
import com.example.persistence.entities.SaveInvestorCommand
import com.example.persistence.repositories.InvestorRepository
import javax.inject.Singleton

@Singleton
class InvestorService(private val repository: InvestorRepository) {

    fun saveInvestor(request: SaveInvestorRequest): Investor {
        return save(request)
    }

    fun updateInvestor(request: UpdateInvestorRequest): Investor {
        return update(request)
    }

    fun findInvestorById(request: FindInvestorRequest): Investor {
        return findById(request)
    }

    fun findAllInvestor(request: FindAllInvestorsRequest): List<Investor> {
        return findAll(request)
    }

    private fun save(request: SaveInvestorRequest): Investor {
        val investorCommand = SaveInvestorCommand(
                request.name!!, request.street, request.streetNumber,
                request.zipCode
        )

        val newInvestor = repository.save(InvestorEntity(investorCommand))

        return Investor.newBuilder()
                .setId(newInvestor.id!!)
                .setName(newInvestor.name)
                .setStreet(newInvestor.address.street)
                .setStreetNumber(newInvestor.address.streetNumber)
                .setZipCode(newInvestor.address.zipCode)
                .build()
    }

    private fun update(request: UpdateInvestorRequest): Investor {
        val investor = repository.findById(request.id)
                .orElseThrow { RuntimeException("Cliente não encontrado!") }

        investor.name = request.name
        investor.address.street = request.street
        investor.address.streetNumber = request.streetNumber
        investor.address.zipCode = request.zipCode

        val newInvestor = repository.update(investor)

        return Investor.newBuilder()
                .setId(newInvestor.id!!)
                .setName(newInvestor.name)
                .setStreet(newInvestor.address.street)
                .setStreetNumber(newInvestor.address.streetNumber)
                .setZipCode(newInvestor.address.zipCode)
                .build()
    }

    private fun findById(request: FindInvestorRequest): Investor {
        val investor = repository.findById(request.id)
                .orElseThrow { RuntimeException("Cliente não encontrado!") }

        return Investor.newBuilder()
                .setId(investor.id!!)
                .setName(investor.name)
                .setStreet(investor.address.street)
                .setStreetNumber(investor.address.streetNumber)
                .setZipCode(investor.address.zipCode)
                .build()
    }

    private fun findAll(request: FindAllInvestorsRequest): List<Investor> {
        val investors = repository.findAll().sortedWith(compareBy {
            when (request.orderBy) {
                OrderInvestorsBy.DEFAULT -> it.id
                OrderInvestorsBy.NAME -> it.name
                OrderInvestorsBy.STREET -> it.address.id
                OrderInvestorsBy.UNRECOGNIZED -> it.id
                else -> it.id
            }
        })

        return investors.map { entity: InvestorEntity? ->
            Investor.newBuilder()
                    .setId(entity?.id!!)
                    .setName(entity.name)
                    .setStreet(entity.address.street)
                    .setStreetNumber(entity.address.streetNumber)
                    .setZipCode(entity.address.zipCode)
                    .build()
        }
    }
}