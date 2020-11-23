package com.example.persistence.entities

import javax.persistence.*

@Entity
data class AddressEntity(@Id
                         @GeneratedValue(strategy = GenerationType.IDENTITY)
                         var id: Long?,
                         @Column(name = "street", length = 200)
                         var street: String,
                         @Column(name = "street_number")
                         var streetNumber: Int,
                         @Column(name = "zip_code", length = 15)
                         var zipCode: String) {
    constructor(): this(null, "", 0, "")
}