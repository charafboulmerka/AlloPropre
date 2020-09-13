package com.dz25.epicapp.Models

 class addModel {
    var id:String?=null
    var latitude:String?=null
    var longitude:String?=null
    var address:String ?=null
    var note:String?=null
    var type:String?=null
    var pic:String?=null
    var date:String?=null
    var millis:String?=null
    var wilaya:String?=null
    var draft :String?=null
    constructor(id:String,latitude:String,longitude:String,address:String,note:String,type:String,pic:String,date:String,millis:String,wilaya:String,draft :String){
        this.id=id
        this.latitude=latitude
        this.longitude=longitude
        this.address=address
        this.note=note
        this.type=type
        this.pic=pic
        this.date=date
        this.millis=millis
        this.wilaya=wilaya
        this.draft=draft
    }

    constructor()
}