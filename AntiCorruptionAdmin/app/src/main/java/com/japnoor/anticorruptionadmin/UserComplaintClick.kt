package com.japnoor.anticorruptionadmin

import com.japnoor.anticorruptionadmin.demand.DemandLetter

interface UserComplaintClick {
    fun onClick(complaints: Complaints)
}
interface UserDemandClick {
    fun onClick(demandLetter: DemandLetter)
}
