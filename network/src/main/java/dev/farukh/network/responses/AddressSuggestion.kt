package dev.farukh.network.responses

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressSuggestion(
    val area: String?,
    @SerialName("area_fias_id")
    val areaFiasId: String,
    @SerialName("area_kladr_id")
    val areaKladrId: String,
    @SerialName("area_type")
    val areaType: String,
    @SerialName("area_type_full")
    val areaTypeFull: String,
    @SerialName("area_with_type")
    val areaWithType: String,
    @SerialName("beltway_distance")
    val beltwayDistance: String,
    @SerialName("beltway_hit")
    val beltwayHit: String,
    @Contextual
    val block: String,
    @SerialName("block_type")
    val blockType: String,
    @SerialName("block_type_full")
    val blockTypeFull: String,
    @SerialName("capital_marker")
    val capitalMarker: String,
    val city: String,
    @SerialName("city_area")
    val cityArea: String,
    @SerialName("city_district")
    val cityDistrict: String,
    @SerialName("city_district_fias_id")
    val cityDistrictFiasId: String,
    @SerialName("city_district_kladr_id")
    val cityDistrictKladrId: String,
    @SerialName("city_district_type")
    val cityDistrictType: String,
    @SerialName("city_district_type_full")
    val cityDistrictTypeFull: String,
    @SerialName("city_district_with_type")
    val cityDistrictWithType: String,
    @SerialName("city_fias_id")
    val cityFiasId: String,
    @SerialName("city_kladr_id")
    val cityKladrId: String,
    @SerialName("city_type")
    val cityType: String,
    @SerialName("city_type_full")
    val cityTypeFull: String,
    @SerialName("city_with_type")
    val cityWithType: String,
    val country: String,
    val countryIsoCode: String,
    val entrance: String,
    @SerialName("federal_district")
    val federalDistrict: String,
    @SerialName("fias_actuality_state")
    val fiasActualityState: String,
    @SerialName("fias_code")
    val fiasCode: String,
    @SerialName("fias_id")
    val fiasId: String,
    @SerialName("fias_level")
    val fiasLevel: String,
    val flat: String,
    @SerialName("flat_area")
    val flatArea: String,
    @SerialName("flat_cadnum")
    val flatCadnum: String,
    @SerialName("flat_fias_id")
    val flatFiasId: String,
    @SerialName("flat_price")
    val flatPrice: String,
    @SerialName("flat_type")
    val flatType: String,
    @SerialName("flat_type_full")
    val flatTypeFull: String,
    val floor: String,
    @SerialName("geo_lat")
    val geoLat: String,
    @SerialName("geo_lon")
    val geoLon: String,
    val house: String,
    @SerialName("house_cadnum")
    val houseCadnum: String,
    @SerialName("house_fias_id")
    val houseFiasId: String,
    @SerialName("house_kladr_id")
    val houseKladrId: String,
    @SerialName("house_type")
    val houseType: String,
    @SerialName("house_type_full")
    val houseTypeFull: String,
    @SerialName("kladr_id")
    val kladrId: String,
    @Contextual
    val metro: List<Metro>,
    val okato: String,
    val oktmo: String,
    @SerialName("postal_box")
    val postalBox: String,
    @SerialName("postal_code")
    val postalCode: String,
    val qc: Int,
    @SerialName("qc_complete")
    val qcComplete: Int,
    @SerialName(":")
    val qcGeo: Int,
    @SerialName("qc_house")
    val qcHouse: Int,
    val region: String,
    @SerialName("region_fias_id")
    val regionFiasId: String,
    @SerialName("region_iso_code")
    val regionIsoCode: String,
    @SerialName("region_kladr_id")
    val regionKladrId: String,
    @SerialName("region_type")
    val regionType: String,
    @SerialName("region_type_full")
    val regionTypeFull: String,
    @SerialName("region_with_type")
    val regionWithType: String,
    val result: String,
    val settlement: String,
    @SerialName("settlement_fias_id")
    val settlementFiasId: String,
    @SerialName("settlement_kladr_id")
    val settlementKladrId: String,
    @SerialName("settlement_type")
    val settlementType: String,
    @SerialName("settlement_type_full")
    val settlementTypeFull: String,
    @SerialName("settlement_with_type")
    val settlementWithType: String,
    val source: String,
    @SerialName("square_meter_price")
    val squareMeterPrice: String,
    val stead: String,
    @SerialName("stead_cadnum")
    val steadCadnum: String,
    @SerialName("stead_fias_id")
    val steadFiasId: String,
    @SerialName("stead_kladr_id")
    val steadKladrId: String,
    @SerialName("stead_type")
    val steadType: String,
    @SerialName("stead_type_full")
    val steadTypeFull: String,
    val street: String,
    @SerialName("street_fias_id")
    val streetFiasId: String,
    @SerialName("street_kladr_id")
    val streetKladrId: String,
    @SerialName("street_type")
    val streetType: String,
    @SerialName("street_type_full")
    val streetTypeFull: String,
    @SerialName("street_with_type")
    val streetWithType: String,
    @SerialName("tax_office")
    val taxOffice: String,
    @SerialName("tax_office_legal")
    val taxOfficeLegal: String,
    val timezone: String,
    @SerialName("unparsed_parts")
    val unparsedParts: String
)