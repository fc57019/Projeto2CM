package com.example.projeto2cm.entities

class Achievement {
    private var timer: String = ""
    private var participante: String = ""
    private var criador: String = ""
    private var timeStamp: String = ""
    private var tipoAchievement: String = ""
    private var stepsCriador: String = ""
    private var stepsFamiliar: String = ""
    private var pointsCriador: String = ""
    private var pointsFamiliar: String = ""
    private var criadorID: String = ""
    private var familiarID: String = ""
    private var cPVez: String = ""
    private var fPVez: String = ""
    private var t: String = ""


    constructor()
    constructor(
        timer: String,
        participante: String,
        criador: String,
        timeStamp: String,
        tipoAchievement: String,
        stepsCriador: String,
        stepsFamiliar: String,
        pointsCriador: String,
        pointsFamiliar: String,
        criadorID: String,
        familiarID: String,
        cPVez: String,
        fPVez: String,
        t: String
    ) {
        this.timer = timer
        this.participante = participante
        this.criador = criador
        this.timeStamp = timeStamp
        this.tipoAchievement = tipoAchievement
        this.stepsCriador = stepsCriador
        this.stepsFamiliar = stepsFamiliar
        this.pointsCriador = pointsCriador
        this.pointsFamiliar = pointsFamiliar
        this.criadorID = criadorID
        this.familiarID = familiarID
        this.cPVez = cPVez
        this.fPVez = fPVez
        this.t = t

    }

    fun getT(): String? {
        return t
    }

    fun setT(t: String?) {
        this.t = t!!
    }

    fun getCPVez(): String? {
        return cPVez
    }

    fun setCPVez(cPVez: String) {
        this.cPVez = cPVez!!
    }

    fun getFPVez(): String? {
        return fPVez
    }

    fun setFPVez(fPVez: String) {
        this.fPVez = fPVez!!
    }

    fun getCriadorID(): String? {
        return criadorID
    }

    fun setCriadorID(criadorID: String) {
        this.criadorID = criadorID!!
    }

    fun getFamiliarID(): String? {
        return familiarID
    }

    fun setFamiliarID(familiarID: String) {
        this.familiarID = familiarID!!
    }

    fun getStepsCriador(): String? {
        return stepsCriador
    }

    fun setStepsCriador(stepsCriador: String) {
        this.stepsCriador = stepsCriador!!
    }

    fun getStepsFamiliar(): String? {
        return stepsFamiliar
    }

    fun setStepsFamiliar(stepsFamiliar: String) {
        this.stepsFamiliar = stepsFamiliar!!
    }

    fun getPointsCriador(): String? {
        return pointsCriador
    }

    fun setPointsCriador(pointsCriador: String) {
        this.pointsCriador = pointsCriador!!
    }

    fun getPointsFamiliar(): String? {
        return pointsFamiliar
    }

    fun setPointsFamiliar(pointsFamiliar: String) {
        this.pointsFamiliar = pointsFamiliar!!
    }

    fun getTimer(): String? {
        return timer
    }

    fun setTimer(timer: String) {
        this.timer = timer!!
    }

    fun getParticipante(): String? {
        return participante
    }

    fun setParticipante(participante: String) {
        this.participante = participante!!
    }

    fun getCriador(): String? {
        return criador
    }

    fun setCriador(criador: String) {
        this.criador = criador!!
    }

    fun getTimeStamp(): String? {
        return timeStamp
    }

    fun setTimeStamp(timeStamp: String) {
        this.timeStamp = timeStamp!!
    }


    fun getTipoAchievement(): String? {
        return tipoAchievement
    }

    fun setTipoAchievement(tipoAchievement: String) {
        this.tipoAchievement = tipoAchievement!!
    }

}