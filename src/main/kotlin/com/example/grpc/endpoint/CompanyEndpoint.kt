package com.example.grpc.endpoint

import com.example.application.services.CompanyService
import com.example.grpc.company.*
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CompanyEndpoint(private val companyService: CompanyService)
    : CompanyServiceGrpcKt.CompanyServiceCoroutineImplBase() {

    override suspend fun saveCompany(request: SaveCompanyRequest): Company {
        if (request.legalName == ""
                || request.tradeName.isBlank()
                || request.corporateAddressStreet.isBlank()
                || request.corporateAddressStreetNumber <= 0
                || request.corporateAddressZipCode.isBlank()
                || request.investorName.isBlank()
                || request.personalAddressStreet.isBlank()
                || request.personalAddressStreetNumber <= 0
                || request.personalAddressZipCode.isBlank()
        ) {
            throw StatusException(Status.INVALID_ARGUMENT
                    .withDescription("Invalid argument in SaveCompanyRequest."))
        }

        val result = companyService.saveCompany(request)

        logger.info("Company ${result.id} saved")
        return result
    }

    override fun saveCompanyStream(requests: Flow<SaveCompanyRequest>): Flow<Company> =
            requests.map { request -> saveCompany(request) }

    override suspend fun updateCompany(request: UpdateCompanyRequest): Company {
        if (request.legalName == ""
                || request.tradeName == ""
        ) {
            throw StatusException(Status.INVALID_ARGUMENT
                    .withDescription("Invalid argument in UpdateCompanyRequest."))
        }

        val result = companyService.updateCompany(request)

        logger.info("Company ${result.id} updated.")
        return result
    }

    override fun updateCompanyStream(requests: Flow<UpdateCompanyRequest>): Flow<Company> =
            requests.map { updateCompany(it) }

    override suspend fun saveCorporateAddress(request: SaveCorporateAddressRequest): CorporateAddress {

        if (request.companyId == 0L
                || request.street == ""
                || request.streetNumber == 0
                || request.zipCode == ""
        ) {
            throw StatusException(Status.INVALID_ARGUMENT
                    .withDescription("Invalid argument in SaveCorporateAddressRequest."))
        }

        val result = companyService.saveCorporateAddress(request)
        logger.info("Corporate Address ${result.id} saved")
        return result
    }

    override fun saveCorporateAddressStream(requests: Flow<SaveCorporateAddressRequest>): Flow<CorporateAddress> =
            requests.map { saveCorporateAddress(it) }

    override suspend fun updateCorporateAddress(request: UpdateCorporateAddressRequest): CorporateAddress {
        if (request.companyId == 0L
                || request.street == ""
                || request.streetNumber == 0
                || request.zipCode == ""
        ) {
            throw StatusException(Status.INVALID_ARGUMENT
                    .withDescription("Invalid argument in UpdateCorporateAddressRequest."))
        }

        val result = companyService.updateCorporateAddress(request)
        logger.info("Corporate Address ${result.id} saved")
        return result
    }

    override fun updateCorporateAddressStream(requests: Flow<UpdateCorporateAddressRequest>): Flow<CorporateAddress> =
            requests.map { updateCorporateAddress(it) }

    override suspend fun findCompanyById(request: FindCompanyRequest): Company {
        return companyService.findCompanyById(request)
    }

    override fun findAllCompanies(request: FindAllCompaniesRequest): Flow<Company> {
        return companyService.findAllCompanies()
    }

    override suspend fun findCorporateAddressById(request: FindCorporateAddressRequest): CorporateAddress {
        return companyService.findCorporateAddressById(request)

    }

    @ExperimentalCoroutinesApi
    override fun findAllCorporateAddressesByCompanyId(request: FindAllCorporateAddressesRequest): Flow<CorporateAddress> {
        return companyService.findAllCorporateAddressesByCompanyId(request)
    }

    companion object {
        var logger: Logger = LoggerFactory.getLogger(CompanyEndpoint::class.java.name)
    }
}