package com.example.testprinter

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.bolt.consumersdk.CCConsumerApiBridge
import com.bolt.consumersdk.CCConsumerApiBridgeCallbacks
import com.bolt.consumersdk.domain.CCConsumerAccount
import com.bolt.consumersdk.domain.response.CCConsumerApiBridgeDeleteAccountResponse
import com.bolt.consumersdk.domain.response.CCConsumerApiBridgeGetAccountsResponse
import com.bolt.consumersdk.domain.response.CCConsumerApiBridgeSaveAccountResponse


class ApiBridgeImpl : CCConsumerApiBridge, Parcelable {
    constructor() {}
    protected constructor(`in`: Parcel?) {
        //unused
    }

    override fun getAccounts(apiBridgeCallbacks: CCConsumerApiBridgeCallbacks) {
        val response = CCConsumerApiBridgeGetAccountsResponse()
        //TODO Implement get Accounts from Third party server here
        //TODO provide result through apiBridgeCallbacks object
    }

    override fun saveAccountToCustomer(
        account: CCConsumerAccount,
        apiBridgeCallbacks: CCConsumerApiBridgeCallbacks
    ) {
        val response = CCConsumerApiBridgeSaveAccountResponse()
        //TODO Implement add Account to Profile from Third party server here
        //TODO provide result through apiBridgeCallbacks object
    }

    override fun deleteCustomerAccount(
        accountToDelete: CCConsumerAccount,
        apiBridgeCallbacks: CCConsumerApiBridgeCallbacks
    ) {
        val response = CCConsumerApiBridgeDeleteAccountResponse()
        //TODO Implement remove Account to Profile from Third party server here                //TODO provide result through apiBridgeCallbacks object
    }

    override fun updateAccount(
        account: CCConsumerAccount,
        apiBridgeCallbacks: CCConsumerApiBridgeCallbacks
    ) {
        //TODO Implement update Account to Profile from Third party server here
        //TODO provide result through apiBridgeCallbacks object
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        //unused
    }

    companion object {
        //Parcelable implementation required for passing this object to Consumer SDK
        @JvmField
        val CREATOR: Creator<ApiBridgeImpl?> = object : Creator<ApiBridgeImpl?> {
            override fun createFromParcel(`in`: Parcel): ApiBridgeImpl {
                return ApiBridgeImpl(`in`)
            }

            override fun newArray(size: Int): Array<ApiBridgeImpl?> {
                return arrayOfNulls(size)
            }
        }
    }
}