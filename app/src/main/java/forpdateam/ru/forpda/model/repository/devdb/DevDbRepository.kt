package forpdateam.ru.forpda.model.repository.devdb

import forpdateam.ru.forpda.api.Api
import forpdateam.ru.forpda.api.devdb.DevDb
import forpdateam.ru.forpda.api.devdb.models.Brand
import forpdateam.ru.forpda.api.devdb.models.Brands
import forpdateam.ru.forpda.api.devdb.models.Device
import forpdateam.ru.forpda.api.devdb.models.DeviceSearch
import forpdateam.ru.forpda.api.mentions.Mentions
import forpdateam.ru.forpda.api.mentions.models.MentionsData
import forpdateam.ru.forpda.model.SchedulersProvider
import io.reactivex.Observable

/**
 * Created by radiationx on 01.01.18.
 */

class DevDbRepository(
        private val schedulers: SchedulersProvider,
        private val devDbApi: DevDb
) {

    fun getBrands(catId: String): Observable<Brands> = Observable
            .fromCallable { devDbApi.getBrands(catId) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    fun getBrand(catId: String, brandId: String): Observable<Brand> = Observable
            .fromCallable { devDbApi.getBrand(catId, brandId) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    fun getDevice(devId: String): Observable<Device> = Observable
            .fromCallable { devDbApi.getDevice(devId) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    fun search(query: String): Observable<DeviceSearch> = Observable
            .fromCallable { devDbApi.search(query) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

}