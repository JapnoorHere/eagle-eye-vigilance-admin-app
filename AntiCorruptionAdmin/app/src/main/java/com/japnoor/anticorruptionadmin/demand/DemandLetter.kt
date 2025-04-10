package com.japnoor.anticorruptionadmin.demand

data class DemandLetter(var demandSubject : String="",
                        var demandDetails : String="",
                        var demandDate : String="",
                        var demandDistrict : String="",
                        var userId : String="",
                        var demandId : String="",
                        var imageUrl : String="",
                        var imageName : String="",
                        var userName : String="",
                        var userEmail : String="",
                        var userOldEmail : String="",
                        var status : String="",
                        var demandNumber : String="",
                        var demandTime : String="",
                        var statusDescription: String = "",
                        var unionName: String = ""

)