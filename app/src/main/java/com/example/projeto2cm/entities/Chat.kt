package com.example.projeto2cm.entities

class Chat {
    private var sender: String = ""
    private var message: String = ""
    private var receiver: String = ""
    private var isseen = false
    private var url: String = ""
    private var messagerId: String = ""

    constructor()
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        url: String,
        messagerId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messagerId = messagerId
    }

    fun getSender(): String? {
        return sender
    }

    fun setSender(sender: String?) {
        this.sender = sender!!
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message!!
    }

    fun getReceiver(): String? {
        return receiver
    }

    fun setReceiver(receiver: String?) {
        this.receiver = receiver!!
    }

    fun isIsSeen(): Boolean? {
        return isseen
    }

    fun setIsSeen(isseen: Boolean?) {
        this.isseen = isseen!!
    }

    fun getURL(): String? {
        return url
    }

    fun setURL(url: String?) {
        this.url = url!!
    }

    fun getMessageID(): String? {
        return messagerId
    }

    fun setMessageID(messagerId: String?) {
        this.messagerId = messagerId!!
    }

}