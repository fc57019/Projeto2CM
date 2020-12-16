package com.example.projeto2cm.entities

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
        weight: String
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


}

