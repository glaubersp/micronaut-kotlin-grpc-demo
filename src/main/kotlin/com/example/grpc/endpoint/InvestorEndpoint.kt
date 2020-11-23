package com.example.grpc.endpoint

import com.example.application.services.InvestorService
import com.example.grpc.investor.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class InvestorEndpoint(private val service: InvestorService)
    : InvestorServiceGrpcKt.InvestorServiceCoroutineImplBase() {

    override suspend fun saveInvestor(request: SaveInvestorRequest): Investor {

        val newInvestor = SaveInvestorRequest
                .newBuilder()
                .setName(request.name)
                .setStreet(request.street)
                .setStreetNumber(request.streetNumber)
                .setZipCode(request.zipCode + "-000")
                .build()

        val result = service.saveInvestor(newInvestor)

        logger.info("Salvo com sucesso")
        return result
    }

    override fun saveInvestorStream(requests: Flow<SaveInvestorRequest>): Flow<Investor> = flow {
        requests.collect { emit(service.saveInvestor(it)) }
    }

    override suspend fun updateInvestor(request: UpdateInvestorRequest): Investor {
        return service.updateInvestor(request)
    }

    override fun updateInvestorStream(requests: Flow<UpdateInvestorRequest>): Flow<Investor> = flow {
        requests.collect { emit(service.updateInvestor(it)) }
    }

    override suspend fun findInvestorById(request: FindInvestorRequest): Investor {
        return service.findInvestorById(request)
    }

    override fun findInvestorByIdStream(requests: Flow<FindInvestorRequest>): Flow<Investor> = flow {
        requests.collect { emit(findInvestorById(it)) }
    }

    override fun findAllInvestorsStream(request: FindAllInvestorsRequest): Flow<Investor> =
            service.findAllInvestor(request).asFlow()


    companion object {
        var logger = LoggerFactory.getLogger(InvestorEndpoint::class.java.name)
    }
}