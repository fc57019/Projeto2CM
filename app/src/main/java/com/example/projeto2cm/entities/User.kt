package com.example.projeto2cm.entities

import java.util.*
import kotlin.collections.ArrayList

class User {
    private var uid: String = ""
    private var name: String = ""
    private var email: String = ""
    private var genre: String = ""
    private var date: String = ""
    private var height: String = "0.0"
    private var profile: String = ""
    private var searchUser: String = ""
    private var steps: String = "0"
    private var weight: String = "0.0"
    private var dia: Int = Calendar.DAY_OF_MONTH
    private var dailySteps: String = "0.0"
    private var distance: String = "0.0"
    private var allSteps: String = "0"
    private var achievementList: ArrayList<Achievement> = ArrayList()

    constructor()
    constructor(
        uid: String,
        name: String,
        email: String,
        genre: String,
        date: String,
        height: String,
        profile: String,
        searchUser: String,
        steps: String,
        weight: String,
        dia: Int,
        dailySteps: String,
        distance: String,
        allSteps: String,
        achievementList: ArrayList<Achievement>
    ) {
        this.uid = uid
        this.name = name
        this.email = email
        this.genre = genre
        this.date = date
        this.height = height
        this.profile = profile
        this.searchUser = searchUser
        this.steps = steps
        this.weight = weight
        this.dia = dia
        this.dailySteps = dailySteps
        this.distance = distance
        this.allSteps = allSteps
        this.achievementList = achievementList
    }

    fun getAchievementList(): ArrayList<Achievement>? {
        return achievementList
    }

    fun setAchievementList(achievementList: ArrayList<Achievement>) {
        this.achievementList = achievementList
    }

    fun getAllSteps(): String? {
        return allSteps
    }

    fun setAllSteps(allSteps: String) {
        this.allSteps = allSteps
    }

    fun getDailySteps(): String? {
        return dailySteps
    }

    fun setDailySteps(dailySteps: String) {
        this.dailySteps = dailySteps
    }

    fun getDistance(): String? {
        return distance
    }

    fun setDistance(distance: String) {
        this.distance = distance
    }

    fun getUID(): String? {
        return uid
    }

    fun setUID(uid: String) {
        this.uid = uid
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getGenre(): String? {
        return genre
    }

    fun setGenre(genre: String) {
        this.genre = genre
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String) {
        this.date = date
    }

    fun getHeight(): String? {
        return height
    }

    fun setHeight(height: String) {
        this.height = height
    }

    fun getWeight(): String? {
        return weight
    }

    fun setWeight(weight: String) {
        this.weight = weight
    }

    fun getProfile(): String? {
        return profile
    }

    fun setProfile(profile: String) {
        this.profile = profile
    }

    fun getSearchUser(): String? {
        return searchUser
    }

    fun setSearchUser(searchUser: String) {
        this.searchUser = searchUser
    }

    fun getSteps(): String? {
        return steps
    }

    fun setSteps(steps: String) {
        this.steps = steps
    }

    fun getDia(): Int? {
        return dia
    }

    fun setDia(dia: Int) {
        this.dia = dia
    }


}

