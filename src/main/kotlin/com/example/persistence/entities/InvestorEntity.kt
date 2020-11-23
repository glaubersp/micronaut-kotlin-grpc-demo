package com.example.persistence.entities

import javax.persistence.*

@Entity
data class InvestorEntity(@Id
                          @GeneratedValue(strategy = GenerationType.IDENTITY)
                          var id: Long? = null,
                          var name: String = "",
                          @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
                          var address: PersonalAddressEntity = PersonalAddressEntity(),
                          @ManyToMany(targetEntity = CompanyEntity::class, mappedBy = "investors",
                                  cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.EAGER)
                          var companies: MutableSet<CompanyEntity> = mutableSetOf()
) {
    constructor(saveInvestorCommand: SaveInvestorCommand) : this(
            null,
            saveInvestorCommand.name,
            PersonalAddressEntity(
                    null,
                    saveInvestorCommand.street,
                    saveInvestorCommand.streetNumber,
                    saveInvestorCommand.zipCode),
            mutableSetOf()
    )

    override fun toString(): String {
        return "InvestorEntity(id=$id, name='$name', address=$address, companies='${companies.map { c -> c.tradeName }}')"
    }

}

data class SaveInvestorCommand(
        var name: String,
        var street: String,
        var streetNumber: Int,
        var zipCode: String
)