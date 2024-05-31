package dev.farukh.network.services.copyClose.admin.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
type BlockRequest struct {
	UserID    string `json:"user_id"`
	AuthToken string `json:"auth_token"`

	UserBlockID string `json:"user_block_id"`
	ReportID    string `json:"report_id"`
}

 */

@Serializable
class BlockRequest (
    @SerialName("user_id")
    val userID: String,
    @SerialName("auth_token")
    val authToken: String,
    @SerialName("user_block_id")
    val userBlockId: String,
    @SerialName("report_id")
    val reportID: String
)