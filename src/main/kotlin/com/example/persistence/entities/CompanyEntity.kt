package com.example.persistence.entities

import javax.persistence.*

@Entity
data class CompanyEntity(@Id
                         @GeneratedValue(strategy = GenerationType.IDENTITY)
                         var id: Long? = null,
                         var tradeName: String = "",
                         var legalName: String = "",
                         @OneToMany(mappedBy = "companyEntity")
                         var corporateAddresses: MutableList<CorporateAddressEntity> = mutableListOf(),
                         @ManyToMany(targetEntity = InvestorEntity::class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
                         var investors: MutableSet<InvestorEntity> = mutableSetOf()
) {
    constructor(saveCompanyCommand: SaveCompanyCommand) : this(
            null,
            saveCompanyCommand.tradeName,
            saveCompanyCommand.legalName,
            mutableListOf(),
            mutableSetOf()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompanyEntity

        if (id != other.id) return false
        if (tradeName != other.tradeName) return false
        if (legalName != other.legalName) return false
        if (corporateAddresses != other.corporateAddresses) return false
        if (investors != other.investors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + tradeName.hashCode()
        result = 31 * result + legalName.hashCode()
        return result
    }

}

data class SaveCompanyCommand(
        var tradeName: String,
        var legalName: String,
)

data class UpdateCompanyCommand(
        var id: Long,
        var tradeName: String,
        var legalName: String
)