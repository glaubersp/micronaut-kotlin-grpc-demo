package com.example.persistence.entities

import javax.persistence.*

@Entity
data class CorporateAddressEntity(@Id
                                  @GeneratedValue(strategy = GenerationType.IDENTITY)
                                  var id: Long? = null,
                                  @Column(name = "street", length = 200)
                                  var street: String = "",
                                  @Column(name = "street_number")
                                  var streetNumber: Int = 0,
                                  @Column(name = "zip_code", length = 15)
                                  var zipCode: String = "",
                                  @ManyToOne
                                  var companyEntity: CompanyEntity? = null
) {
    constructor(saveCorporateAddressCommand: SaveCorporateAddressCommand) : this(
            null,
            saveCorporateAddressCommand.street,
            saveCorporateAddressCommand.streetNumber,
            saveCorporateAddressCommand.zipCode,
            saveCorporateAddressCommand.companyEntity)

}

data class SaveCorporateAddressCommand(
        var street: String,
        var streetNumber: Int,
        var zipCode: String,
        var companyEntity: CompanyEntity
)