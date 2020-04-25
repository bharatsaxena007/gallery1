package com.example.gallery1.model

class ImageListModel {
    var id:String =""
    var imageUrl1:String = ""
    var timeStamp=""

    constructor(id: String, imageUrl1: String, timeStamp: String) {
        this.id = id
        this.imageUrl1 = imageUrl1
        this.timeStamp = timeStamp
    }
}