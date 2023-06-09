package com.fcadev.rickandmortyapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcadev.rickandmortyapp.model.character.CharacterResult
import com.fcadev.rickandmortyapp.model.character.RamCharacter
import com.fcadev.rickandmortyapp.model.location.RamLocation
import com.fcadev.rickandmortyapp.model.location.Result
import com.fcadev.rickandmortyapp.service.character.CharacterAPIService
import com.fcadev.rickandmortyapp.service.location.LocationAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CharacterListViewModel : ViewModel() {

    private val locationAPIService = LocationAPIService()
    private val characterAPIService = CharacterAPIService()
    private val disposable = CompositeDisposable()

    val locations = MutableLiveData<MutableList<Result>>(mutableListOf())
    val characters = MutableLiveData<MutableList<CharacterResult>?>()
    val multipleCharactersByLocation = MutableLiveData<ArrayList<CharacterResult>>()
    val singleCharacterByLocation = MutableLiveData<CharacterResult>()
    val locationsLoading = MutableLiveData<Boolean>()
    val residentNumbersArray = MutableLiveData<ArrayList<String>>()


    fun downloadLocationData(page: String) {
        getLocationDataFromAPI(page)
    }

    fun downloadCharacterData(page: String) {
        getCharacterDataFromAPI(page)
    }

    private fun getLocationDataFromAPI(page: String) {
        locationsLoading.postValue(true)

        disposable.add(
            locationAPIService.getLocationsData(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RamLocation>() {
                    override fun onSuccess(t: RamLocation) {
                        val existingResults = locations.value ?: mutableListOf()
                        existingResults.addAll(t.results as MutableList<Result>)
                        locations.value = existingResults
                        locationsLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        locationsLoading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    private fun getCharacterDataFromAPI(page: String) {
        locationsLoading.value = true

        disposable.add(
            characterAPIService.getCharacterData(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RamCharacter>() {
                    override fun onSuccess(t: RamCharacter) {
                        characters.value = t.results as ArrayList<CharacterResult>?
                        locationsLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        locationsLoading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }

    fun getCharacterDataByLocation() {
        locationsLoading.value = true

        var ids = residentNumbersArray.value.toString()
            .substring(1, residentNumbersArray.value.toString().length - 1)
        ids = ids.replace(" ", "")
        Log.d("ids", ids)

        if (ids.contains(",")) {
            disposable.add(
                characterAPIService.getCharactersByIds(ids)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<ArrayList<CharacterResult>>() {
                        override fun onSuccess(t: ArrayList<CharacterResult>) {
                            multipleCharactersByLocation.value = t as ArrayList<CharacterResult>
                            locationsLoading.value = false
                        }

                        override fun onError(e: Throwable) {
                            locationsLoading.value = false
                            e.printStackTrace()
                        }
                    })
            )
        } else {
            disposable.add(
                characterAPIService.getCharactersBySingleId(ids)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<CharacterResult>() {
                        override fun onSuccess(t: CharacterResult) {
                            singleCharacterByLocation.value = t as CharacterResult
                            locationsLoading.value = false
                        }

                        override fun onError(e: Throwable) {
                            locationsLoading.value = false
                            e.printStackTrace()
                        }
                    })
            )
        }


    }

}
