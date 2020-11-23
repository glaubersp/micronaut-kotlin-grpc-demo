package com.example.application.services

import com.example.grpc.company.*
import com.example.persistence.entities.*
import com.example.persistence.repositories.CompanyRepository
import com.example.persistence.repositories.CorporateAddressRepository
import com.example.persistence.repositories.InvestorRepository
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
open class CompanyService constructor(
        private val companyRepository: CompanyRepository,
        private val corporateAddressRepository: CorporateAddressRepository,
        private val investorRepository: InvestorRepository
) {
    @Transactional
    open fun saveCompany(request: SaveCompanyRequest): Company {

        val companyCommand = SaveCompanyCommand(
                request.tradeName,
                request.legalName
        )
        val savedCompany = companyRepository.save(CompanyEntity(companyCommand))

        val saveCorporateAddressCommand = SaveCorporateAddressCommand(
                request.corporateAddressStreet,
                request.corporateAddressStreetNumber,
                request.corporateAddressZipCode,
                savedCompany)
        val savedCorporateAddress = corporateAddressRepository.save(
                CorporateAddressEntity(saveCorporateAddressCommand))
        savedCompany.corporateAddresses.add(savedCorporateAddress)

        val saveInvestorCommand = SaveInvestorCommand(request.investorName,
                request.personalAddressStreet,
                request.personalAddressStreetNumber,
                request.personalAddressZipCode)
        val savedInvestor = investorRepository.save(InvestorEntity(saveInvestorCommand))
        savedCompany.investors.add(savedInvestor)

        companyRepository.update(savedCompany)

        return Company.newBuilder()
                .setId(savedCompany.id!!)
                .setTradeName(savedCompany.tradeName)
                .setLegalName(savedCompany.legalName)
                .build()
    }

    @Transactional
    open fun saveCorporateAddress(request: SaveCorporateAddressRequest): CorporateAddress {

        val companyEntityOpt = companyRepository.findById(request.companyId)

        if (companyEntityOpt.isEmpty) {
            throw StatusException(Status.NOT_FOUND
                    .withDescription("Company ${request.companyId} not found."))
        }

        val corporateAddressCommand = SaveCorporateAddressCommand(
                request.street,
                request.streetNumber,
                request.zipCode,
                companyEntityOpt.get())

        val savedCorporateAddress = corporateAddressRepository.save(
                CorporateAddressEntity(corporateAddressCommand))

        return CorporateAddress.newBuilder()
                .setCompanyId(savedCorporateAddress.companyEntity!!.id!!)
                .setId(savedCorporateAddress.id!!)
                .setStreet(savedCorporateAddress.street)
                .setStreet(savedCorporateAddress.street)
                .setZipCode(savedCorporateAddress.zipCode)
                .build()
    }

    @Transactional
    open fun updateCompany(request: UpdateCompanyRequest): Company {

        val foundCompanyOpt = companyRepository.findById(request.id)
        if (foundCompanyOpt.isEmpty) {
            throw StatusException(Status.NOT_FOUND
                    .withDescription("Company ${request.id} not found."))
        }

        val foundCompany = foundCompanyOpt.get()
        foundCompany.legalName = request.legalName
        foundCompany.tradeName = request.tradeName

        val updatedCompany = companyRepository.save(foundCompany)

        return Company.newBuilder()
                .setId(updatedCompany.id!!)
                .setTradeName(updatedCompany.tradeName)
                .setLegalName(updatedCompany.legalName)
                .build()
    }

    @Transactional
    open fun updateCorporateAddress(request: UpdateCorporateAddressRequest): CorporateAddress {

        val foundCorporateAddressOpt = corporateAddressRepository.findById(request.id)

        if (foundCorporateAddressOpt.isEmpty) {
            throw StatusException(Status.NOT_FOUND
                    .withDescription("CorporateAddress ${request.id} not found."))
        }

        val foundCorporateAddress = foundCorporateAddressOpt.get()
        foundCorporateAddress.street = request.street
        foundCorporateAddress.streetNumber = request.streetNumber
        foundCorporateAddress.zipCode = request.zipCode

        val updatedCorporateAddress = corporateAddressRepository.update(foundCorporateAddress)

        return CorporateAddress.newBuilder()
                .setCompanyId(updatedCorporateAddress.companyEntity!!.id!!)
                .setId(updatedCorporateAddress.id!!)
                .setStreet(updatedCorporateAddress.street)
                .setStreet(updatedCorporateAddress.street)
                .setZipCode(updatedCorporateAddress.zipCode)
                .build()
    }

    fun findCompanyById(request: FindCompanyRequest): Company {
        val companyFound = companyRepository.findById(request.id)
                .orElseThrow {
                    StatusException(
                            Status.NOT_FOUND
                                    .withDescription("CorporateAddress ${request.id} not found."),
                    )
                }
        return Company.newBuilder()
                .setId(companyFound.id!!)
                .setLegalName(companyFound.legalName)
                .setTradeName(companyFound.tradeName)
                .build()
    }

    fun findAllCompanies(): Flow<Company> = flow {
        companyRepository.findAll().asFlow().collect { entity ->
            val company = Company.newBuilder()
                    .setId(entity.id!!)
                    .setLegalName(entity.legalName)
                    .setTradeName(entity.tradeName)
                    .build()
            emit(company)
        }
    }

    fun findCorporateAddressById(request: FindCorporateAddressRequest): CorporateAddress {
        val corporateAddress = corporateAddressRepository.findById(request.id)
                .orElseThrow {
                    StatusException(
                            Status.NOT_FOUND
                                    .withDescription("CorporateAddress ${request.id} not found."),
                    )

                }
        return CorporateAddress.newBuilder()
                .setId(corporateAddress.id!!)
                .setCompanyId(corporateAddress.companyEntity!!.id!!)
                .setStreet(corporateAddress.zipCode)
                .setStreetNumber(corporateAddress.streetNumber)
                .setZipCode(corporateAddress.zipCode)
                .build()
    }

    @ExperimentalCoroutinesApi
    fun findAllCorporateAddressesByCompanyId(request: FindAllCorporateAddressesRequest): Flow<CorporateAddress> = flow {
        val addresses = corporateAddressRepository.findByCompanyEntityId(request.companyId)

        addresses.asFlow().cancellable().onEmpty {
            // Observations:
            // If we throw Status.NOT_FOUND with a description, BloomRPC receives the description message correctly, but the
            // connection is not closed. We can verify that the gRPC OUTGOING HEADER has the endStream flag set and the
            // correct message, as in:
            // io.grpc.netty.NettyServerHandler - [id: 0x2ae2f51e, L:/127.0.0.1:50051 - R:/127.0.0.1:34190]
            // OUTBOUND HEADERS: streamId=1 headers=GrpcHttp2OutboundHeaders[grpc-status: 5, grpc-message: Company 5 not found.] padding=0 endStream=true
            //
            // If we use the error() function, BloomRPC receives a generic message ( "error": "2 UNKNOWN: ") and won't close
            // the connection, although the OUTGOING HEADER contains the endStream flag set.
            // There seems to exist an error when rethrowing the Exception of the error function, as the message is lost
            // during the process. A CancellationException is thrown io.grpc.kotlin.ServerCalls.serverCallListener:234.
            // io.grpc.netty.NettyServerHandler - [id: 0x2a2602fa, L:/127.0.0.1:50051 - R:/127.0.0.1:34350]
            // OUTBOUND HEADERS: streamId=1 headers=GrpcHttp2OutboundHeaders[grpc-status: 2] padding=0 endStream=true

            // 01 - Empty Flow attempt - Closes the connection on gRPC.
//            emptyFlow<CorporateAddress>()

            // 02 - Status.NOT_FOUND attempt - Doesn't close the connection on gRPC.
//            throw Status.NOT_FOUND
//                    .withDescription("Company ${request.companyId} not found.")
//                    .asRuntimeException()

            // 03 - error function attempt - Closes the connection on gRPC.
            error("Company ${request.companyId} not found.")
        }.collect { entity ->
            val value = CorporateAddress.newBuilder()
                    .setId(entity.id!!)
                    .setStreet(entity.street)
                    .setStreetNumber(entity.streetNumber)
                    .setZipCode(entity.zipCode)
                    .setCompanyId(entity.companyEntity!!.id!!)
                    .build()
            emit(value)
        }
    }
}