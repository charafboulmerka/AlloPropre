package com.dz25.epicapp.Models

class claimModel {
    var id:String?=null
    var username:String?=null
    var userphone:String?=null
    var usernote:String?=null
    var userclaimtype:String?=null
    var usercity:String?=null
    var millis:String?=null
    var date:String?=null
    constructor(id:String,username:String,userphone:String,usernote:String,userclaimtype:String,usercity:String,millis:String,date:String){
        this.id=id
        this.username=username
        this.userphone=userphone
        this.usernote=usernote
        this.userclaimtype=userclaimtype
        this.usercity=usercity
        this.millis=millis
        this.date=date
    }

    constructor()
}