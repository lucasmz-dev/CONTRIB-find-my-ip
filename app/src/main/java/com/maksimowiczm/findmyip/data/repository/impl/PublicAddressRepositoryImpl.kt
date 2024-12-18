package com.maksimowiczm.findmyip.data.repository.impl

import androidx.room.withTransaction
import com.maksimowiczm.findmyip.data.database.AppDatabase
import com.maksimowiczm.findmyip.data.database.entity.AddressEntity
import com.maksimowiczm.findmyip.data.database.entity.AddressEntityDao
import com.maksimowiczm.findmyip.data.model.Address
import com.maksimowiczm.findmyip.data.model.InternetProtocolVersion
import com.maksimowiczm.findmyip.data.network.CurrentAddressDataSource
import com.maksimowiczm.findmyip.data.repository.PublicAddressRepository
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PublicAddressRepositoryImpl(
    private val ipv4DataSource: CurrentAddressDataSource,
    private val ipv6DataSource: CurrentAddressDataSource,
    private val database: AppDatabase,
    private val addressEntityDao: AddressEntityDao
) : PublicAddressRepository {
    override fun observeCurrentAddress(internetProtocolVersion: InternetProtocolVersion) =
        when (internetProtocolVersion) {
            InternetProtocolVersion.IPv4 -> ipv4DataSource.observeCurrentAddress(autoFetch = true)
            InternetProtocolVersion.IPv6 -> ipv6DataSource.observeCurrentAddress(autoFetch = true)
        }

    /**
     * Fetches the current address and returns it.
     *
     * @return The current address or null if the fetch failed.
     */
    override suspend fun refreshCurrentAddress(internetProtocolVersion: InternetProtocolVersion) =
        when (internetProtocolVersion) {
            InternetProtocolVersion.IPv4 -> ipv4DataSource.refreshCurrentAddress()
            InternetProtocolVersion.IPv6 -> ipv6DataSource.refreshCurrentAddress()
        }

    override suspend fun deleteAll() = addressEntityDao.deleteAll()

    override fun observeAddressHistory(
        internetProtocolVersion: InternetProtocolVersion
    ): Flow<List<Address>> {
        return addressEntityDao.observeAddresses(internetProtocolVersion)
            .map { addressEntities ->
                addressEntities.map { it.toAddress() }
            }
    }

    /**
     * Inserts the given address into the database if it is different from the latest address.
     */
    override suspend fun insertIfDistinct(address: Address) {
        database.withTransaction {
            val latestAddress = addressEntityDao.getLatestAddress(address.internetProtocolVersion)
            if (latestAddress == null || latestAddress.ip != address.ip) {
                val entity = address.toEntity()
                if (entity.timestamp != latestAddress?.timestamp) {
                    addressEntityDao.insertAddress(entity)
                }
            }
        }
    }
}

private fun AddressEntity.toAddress(): Address {
    return Address(
        ip = ip,
        date = Calendar.getInstance().apply { timeInMillis = timestamp.toLong() }.time,
        networkType = null,
        internetProtocolVersion = internetProtocolVersion
    )
}

private fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        ip = ip,
        timestamp = date.time.toLong(),
        internetProtocolVersion = internetProtocolVersion
    )
}
