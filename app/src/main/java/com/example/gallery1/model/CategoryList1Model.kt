package com.example.gallery1.model

class CategoryList1Model {
    var cat_image:String = ""
    var cat_title:String?=null
    var cat_id:String?=null

    constructor(cat_image: String, cat_title: String?,cat_id:String) {
        this.cat_image = cat_image
        this.cat_title = cat_title
        this.cat_id=cat_id
    }

}